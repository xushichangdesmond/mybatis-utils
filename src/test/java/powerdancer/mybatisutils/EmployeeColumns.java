package powerdancer.mybatisutils;

public enum EmployeeColumns implements Column {
    ID(true),
    NAME,
    JOIN_TIME(true),
    SUPERVISOR_ID;

    final boolean indexed;
    final String alias;

    EmployeeColumns() {
        this(false);
    }

    EmployeeColumns(boolean indexed) {
        this.indexed = indexed;
        alias = name().replaceAll("_", "");
    }

    @Override
    public String alias() {
        return alias;
    }

    static ColumnMethodMapper methodMapper = ColumnMethodMapper.forAccessorInterface(EmployeeColumns.class, Employee.class);
    public static ColumnMethodMapper methodMapper() {
        return methodMapper;
    }
}
