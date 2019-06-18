package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class FindSqlProvider implements ProviderMethodResolver {
    public static String doFind(String tableName, ColumnMethodMapper cmm, Column sortColumn, boolean asc, Filter[] filters, @Param("params") List params) {
        return Filter.apply(new SQL(){{
            SELECT(cmm.dbSelectList());
            FROM(tableName);
            if (sortColumn != null)
                ORDER_BY(sortColumn.name() + (asc?" ASC":" DESC"));
        }}, filters).toString();
    }
}
