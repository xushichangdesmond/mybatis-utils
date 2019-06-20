package powerdancer.mybatisutils;

import java.time.Instant;

import static powerdancer.mybatisutils.ColumnMeta.*;

public enum EmployeeColumns implements Column {
    ID(indexed("ID")),
    NAME,
    JOIN_TIME(ColumnMeta.builder()
            .indexed()
            .alias("JOINTIME")
            .serializer(v-> ((Instant)v).toEpochMilli())
            .deserializer(v->Instant.ofEpochMilli((Long)v)).build()),
    SUPERVISOR_ID;

    final ColumnMeta meta;

    EmployeeColumns() {
        this.meta = nonIndexed(alias(name()));
    }

    EmployeeColumns(ColumnMeta meta) {
        this.meta = meta;
    }


    @Override
    public ColumnMeta meta() {
        return meta;
    }

    public static String alias(String name) {
        return name.replaceAll("_", "");
    }

    static ColumnMethodMapper methodMapper = ColumnMethodMapper.forAccessorInterface(EmployeeColumns.class, Employee.class);
    public static ColumnMethodMapper methodMapper() {
        return methodMapper;
    }
}
