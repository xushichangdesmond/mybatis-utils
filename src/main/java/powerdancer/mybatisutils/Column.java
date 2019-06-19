package powerdancer.mybatisutils;

public interface Column {
    String name();
    int ordinal();

    default ColumnMeta meta() {
        return ColumnMeta.nonIndexed(name());
    }
}
