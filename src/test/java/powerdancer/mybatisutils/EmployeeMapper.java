package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.time.Instant;
import java.util.List;

public interface EmployeeMapper extends FindMapper<Employee> {
    @Insert("Insert into Employee (ID,NAME,JOIN_TIME,SUPERVISOR_ID) values (#{id}, #{name}, #{joinTime}, #{supervisorId, jdbcType=BIGINT})")
    void insert(@Param("id") long id, @Param("name") String name, @Param("joinTime") Instant joinTime, @Param("supervisorId") Long supervisorId);

    @SelectProvider(type=EmployeeMapperSqlProvider.class)
    Employee selectOne(@Param("id") long id);

    @SelectProvider(type=EmployeeMapperSqlProvider.class)
    List<Employee> selectAll();

    @Override
    default String tableName() {
        return "Employee";
    }

    @Override
    default ColumnMethodMapper methodMapper() {
        return EmployeeColumns.methodMapper();
    }
}
