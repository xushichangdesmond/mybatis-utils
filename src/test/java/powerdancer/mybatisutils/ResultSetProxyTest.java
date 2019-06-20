package powerdancer.mybatisutils;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.Driver;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static powerdancer.mybatisutils.EmployeeColumns.ID;
import static powerdancer.mybatisutils.EmployeeColumns.JOIN_TIME;
import static powerdancer.mybatisutils.EmployeeColumns.SUPERVISOR_ID;

public class ResultSetProxyTest {
    @Test
    public void test() throws Throwable {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        new Thread() {
            @Override
            public void run() {
                try {
                    Server.main(new String[]{"-tcpAllowOthers", "-tcpPort", "6789"});
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        DriverManager.registerDriver(new Driver());
        DataSource dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL("jdbc:h2:mem:Employee;DB_CLOSE_DELAY=-1");
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
        configuration.setLazyLoadingEnabled(true);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(EmployeeMapper.class);

        ResultSetProxy.registerHandler(
                configuration, EmployeeColumns.methodMapper(), Employee.class
        );

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory f = builder.build(configuration);

        try (Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("create table Employee(\n" +
                        "   ID VARCHAR(36) not null primary key,\n" +
                        "   NAME  VARCHAR(36) NOT NULL,\n" +
                        "   JOIN_TIME TIMESTAMP(3) WITH TIME ZONE NOT NULL,\n" +
                        "   SUPERVISOR_ID VARCHAR(36) NULL,\n" +
                        ")");
            }
            conn.commit();
        }

        try (SqlSession s = f.openSession(true)) {
            EmployeeMapper mapper = s.getMapper(EmployeeMapper.class);
            mapper.insert(1, "powerdancer", Instant.EPOCH, null);
            mapper.insert(2, "carolina", Instant.EPOCH.plus(Duration.ofMinutes(1)), 1L);

            assertPowerDancer(mapper.selectOne(1));
            assertCarolina(mapper.selectOne(2));

            List<Employee> employees = mapper.selectAll();
            assertPowerDancer(employees.get(0));
            assertCarolina(employees.get(1));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true
            );
            assertPowerDancer(employees.get(0));
            assertCarolina(employees.get(1));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    false
            );
            assertPowerDancer(employees.get(1));
            assertCarolina(employees.get(0));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(SUPERVISOR_ID, Operator.EQ, 1)
            );
            Assertions.assertEquals(1, employees.size());
            assertCarolina(employees.get(0));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(SUPERVISOR_ID, Operator.EQ, null)
            );
            Assertions.assertEquals(1, employees.size());
            assertPowerDancer(employees.get(0));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(SUPERVISOR_ID, Operator.EQ, null),
                    Filter.of(ID, Operator.EQ, 1)
            );
            Assertions.assertEquals(1, employees.size());
            assertPowerDancer(employees.get(0));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(SUPERVISOR_ID, Operator.EQ, null),
                    Filter.of(ID, Operator.EQ, 2)
            );
            Assertions.assertEquals(0, employees.size());

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(SUPERVISOR_ID, Operator.EQ, null),
                    Filter.OR,
                    Filter.of(ID, Operator.EQ, 2)
            );
            assertPowerDancer(employees.get(0));
            assertCarolina(employees.get(1));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(JOIN_TIME, Operator.GREATER_THAN, Instant.EPOCH)
            );
            Assertions.assertEquals(1, employees.size());
            assertCarolina(employees.get(0));

            employees = mapper.slowFind(
                    JOIN_TIME,
                    true,
                    Filter.of(JOIN_TIME, Operator.LESS_THAN, Instant.EPOCH)
            );
            Assertions.assertEquals(0, employees.size());

            Assertions.assertThrows(PersistenceException.class, ()->
                    mapper.find(
                            JOIN_TIME,
                            true,
                            Filter.of(SUPERVISOR_ID, Operator.LESS_THAN, 100)
            ));

        }
    }

    private void assertPowerDancer(Employee employee) {
        Assertions.assertEquals(1, employee.id());
        Assertions.assertEquals(Instant.EPOCH, employee.joinTime());
        Assertions.assertEquals("powerdancer", employee.name());
        Assertions.assertNull(employee.supervisorId());
        Assertions.assertTrue(employee.isBigBoss());
    }

    private void assertCarolina(Employee employee) {
        Assertions.assertEquals(2, employee.id());
        Assertions.assertEquals(Instant.EPOCH.plus(Duration.ofMinutes(1)), employee.joinTime());
        Assertions.assertEquals("carolina", employee.name());
        Assertions.assertEquals(1L, employee.supervisorId());
        Assertions.assertFalse(employee.isBigBoss());
    }
}
