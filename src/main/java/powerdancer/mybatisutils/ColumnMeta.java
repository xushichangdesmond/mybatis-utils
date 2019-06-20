package powerdancer.mybatisutils;

import java.util.function.Function;

public interface ColumnMeta {
    Function serializer();
    Function deserializer();
    boolean indexed();
    String alias();

    static ColumnMetaBuilder builder() {
        return new ColumnMetaBuilder();
    }

    static ColumnMeta indexed(String alias) {
        return builder()
                .serializer(Function.identity())
                .deserializer(Function.identity())
                .indexed()
                .alias(alias)
                .build();
    }

    static ColumnMeta nonIndexed(String alias) {
        return builder()
                .serializer(Function.identity())
                .deserializer(Function.identity())
                .nonIndexed()
                .alias(alias)
                .build();
    }
}
