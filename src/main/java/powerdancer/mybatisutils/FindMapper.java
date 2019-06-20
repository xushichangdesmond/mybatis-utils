package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;

public interface FindMapper<T> {

    String tableName();
    ColumnMethodMapper methodMapper();

    default List<T> find(Column sortColumn, boolean asc, Filter... filters) throws PersistenceException {
        return doFind(tableName(), methodMapper(), sortColumn, asc, filters, Filter.toParams(filters));
    }

    default List<T> slowFind(Column sortColumn, boolean asc, Filter... filters) throws PersistenceException {
        return doSlowFind(tableName(), methodMapper(), sortColumn, asc, filters, Filter.toParams(filters));
    }

    @SelectProvider(type = FindSqlProvider.class)
    List<T> doFind(String tableName, ColumnMethodMapper cmm, Column sortColumn, boolean asc, Filter[] filters, @Param("params") List params) throws Filter.NotIndexedColumnException;

    @SelectProvider(type = FindSqlProvider.class)
    List<T> doSlowFind(String tableName, ColumnMethodMapper cmm, Column sortColumn, boolean asc, Filter[] filters, @Param("params") List params) throws Filter.NotIndexedColumnException;
}
