package powerdancer.mybatisutils;

public enum EmployeeColumns implements Column {
    ID(true),
    NAME,
    JOIN_TIME(true),
    SUPERVISOR_ID;

    final ColumnMeta meta;

    EmployeeColumns() {
        this.meta = ColumnMeta.nonIndexed(name().replaceAll("_", ""));
    }

    EmployeeColumns(ColumnMeta meta) {
        this.meta = meta;
    }

    @Override
    public ColumnMeta meta() {
        return meta;
    }

    static ColumnMethodMapper methodMapper = ColumnMethodMapper.forAccessorInterface(EmployeeColumns.class, Employee.class);
    public static ColumnMethodMapper methodMapper() {
        return methodMapper;
    }
}
