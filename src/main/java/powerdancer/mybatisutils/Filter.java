package powerdancer.mybatisutils;

import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Filter<T> {
    Filter OR = new Filter(){};
    Filter AND = new Filter(){};

    static List toParams(Filter[] filters) {
        return Arrays.stream(filters).flatMap(f->
                Optional.ofNullable(f.operands())
                        .map(Arrays::stream)
                        .orElse(Stream.empty()))
                .collect(Collectors.toList());
    }

    default Column col() {return null;}
    default Operator operator() {return null;}
    default T[] operands() {return null;}

    static <T> Filter<T> of(Column col, Operator operator, T... operands) {
        return new Filter<T>() {
            @Override
            public Column col() {
                return col;
            }

            @Override
            public Operator operator() {
                return operator;
            }

            @Override
            public T[] operands() {
                return operands;
            }
        };
    }

    static SQL apply(SQL sql, Filter[] filters, boolean enforceIndexedCheck) throws NotIndexedColumnException{
        int paramIndex = 0;
        for (Filter f: filters) {
            if (f == Filter.OR) sql.OR();
            else if (f == Filter.AND) sql.AND();
            else {
                if (enforceIndexedCheck && !f.col().meta().indexed()) {
                    throw new NotIndexedColumnException(f.col());
                }
                Object[] operands = f.operands();
                Column c = f.col();
                switch (f.operator()) {
                    case EQ:
                        if (operands == null) {
                            sql.WHERE(c.name() + " is null");
                        } else {
                            sql.WHERE(c.name() + "=#{params[" + (paramIndex++) + "]}");
                        }
                        break;
                    case NOT_EQ:
                        if (operands == null) {
                            sql.WHERE(c.name() + " is not null");
                        } else {
                            sql.WHERE(c.name() + "<>#{params[" + (paramIndex++) + "]}");
                        }
                        break;
                    case LESS_THAN:
                        sql.WHERE(c.name() + "<#{params[" + (paramIndex++) + "]}");
                        break;
                    case GREATER_THAN:
                        sql.WHERE(c.name() + ">#{params[" + (paramIndex++) + "]}");
                        break;
                    case IN:
                        int offset = paramIndex;
                        sql.WHERE(c.name() + " IN ("
                                + IntStream.range(0, operands.length).mapToObj(i -> "#{params[" + (i + offset) + "]}").collect(Collectors.joining(","))
                                + ")");
                        paramIndex += operands.length;
                        break;

                }
            }
        }
        return sql;
    }

    static <T extends Enum> Filter unmarshalFrom(Class<T> columnEnumClass, Object... params) {
        Column col = (Column) Enum.valueOf(columnEnumClass, params[0].toString());
        Operator operator = Operator.valueOf(params[1].toString());
        if (operator == null) throw new IllegalArgumentException(params[1] + " is not a valid operator name");
        Object[] operands = IntStream.range(2, params.length).mapToObj(i->col.meta().deserializer().apply(params[i])).toArray();

        return new Filter() {
            @Override
            public Column col() {
                return col;
            }

            @Override
            public Operator operator() {
                return operator;
            }

            @Override
            public Object[] operands() {
                return operands;
            }
        };
    }

    class NotIndexedColumnException extends RuntimeException {
        final Column column;
        NotIndexedColumnException(Column col) {
            super(col.name() + " is not an indexed column");
            column = col;
        }
    }


}