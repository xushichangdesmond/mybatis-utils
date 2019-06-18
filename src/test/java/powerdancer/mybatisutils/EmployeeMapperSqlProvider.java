package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

import static powerdancer.mybatisutils.EmployeeColumns.*;

public class EmployeeMapperSqlProvider implements ProviderMethodResolver {

    public static String selectOne(@Param("id") long id) {
        return new SQL()
                .SELECT(methodMapper().dbSelectList())
                .FROM("Employee")
                .WHERE(ID +"=#{id}")
                .toString();
    }

    public static String selectAll() {
        return new SQL()
                .SELECT(methodMapper().dbSelectList())
                .FROM("Employee")
                .ORDER_BY(ID.name())
                .toString();
    }
}
