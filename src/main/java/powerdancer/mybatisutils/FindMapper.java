package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

public interface FindMapper<T> {

    String tableName();
    ColumnMethodMapper methodMapper();

    default List<T> find(Column sortColumn, boolean asc, Filter... filters) {
        return doFind(tableName(), methodMapper(), sortColumn, asc, filters, Filter.toParams(filters));
    }

    @SelectProvider(type = FindSqlProvider.class)
    List<T> doFind(String tableName, ColumnMethodMapper cmm, Column sortColumn, boolean asc, Filter[] filters, @Param("params") List params);
}
