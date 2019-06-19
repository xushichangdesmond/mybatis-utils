package powerdancer.mybatisutils;

import java.util.function.Function;

public class ColumnMetaBuilder {
    Function serializer;
    Function deserializer;
    boolean indexed;
    String alias;

    public static ColumnMeta build(Function serializer, Function deserializer, boolean indexed, String alias) {
        return new ColumnMeta() {
            @Override
            public Function serializer() {
                return serializer;
            }

            @Override
            public Function deserializer() {
                return deserializer;
            }

            @Override
            public boolean indexed() {
                return indexed;
            }

            @Override
            public String alias() {
                return alias;
            }
        };
    }

    public ColumnMeta build() {
        return build(serializer, deserializer, indexed, alias);
    }

    public ColumnMetaBuilder indexed() {
        indexed = true;
        return this;
    }

    public ColumnMetaBuilder nonIndexed() {
        indexed = false;
        return this;
    }

    public ColumnMetaBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    public ColumnMetaBuilder serializer(Function serializer) {
        this.serializer = serializer;
        return this;
    }

    public ColumnMetaBuilder deserializer(Function deserializer) {
        this.deserializer = deserializer;
        return this;
    }
}
