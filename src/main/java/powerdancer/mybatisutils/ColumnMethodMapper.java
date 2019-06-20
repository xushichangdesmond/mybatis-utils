package powerdancer.mybatisutils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ColumnMethodMapper {
    Optional<Method> map(Column c);
    Optional<Column> map(Method m);
    Column[] columns();
    Method[] methods();

    default String dbSelectList() {
        return Arrays.stream(columns()).map(c-> c.name() + " " + c.meta().alias()).collect(Collectors.joining(","));
    }

    static ColumnMethodMapper forAccessorInterface(Class<? extends Column> columnEnumClass, Class interfaceClass) {
        Column[] cols = columnEnumClass.getEnumConstants();
        Map<Column, Method> m = Arrays.stream(interfaceClass.getMethods())
                .filter(method->!method.isDefault())
                .collect(
                Collectors.toMap(
                        method -> Arrays.stream(cols).filter(c -> c.meta().alias().toUpperCase().equals(method.getName().toUpperCase())).findFirst().orElseThrow(()->new IllegalStateException("Unable to find column for accessor " + method.getName())),
                        method -> method
                )
        );
        Method[] methods = Arrays.stream(cols).map(c->m.get(c)).toArray(Method[]::new);
        return new ColumnMethodMapper() {
            @Override
            public Optional<Method> map(Column c) {
                return Optional.ofNullable(m.get(c));
            }

            @Override
            public Column[] columns() {
                return cols;
            }

            @Override
            public Optional<Column> map(Method method) {
                for (Column c: cols) {
                    if (m.get(c).equals(method)) return Optional.of(c);
                }
                return Optional.empty();
            }

            @Override
            public Method[] methods() {
                return methods;
            }
        };
    }
}
