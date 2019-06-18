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

    static SQL apply(SQL sql, Filter[] filters) {
        int paramIndex = 0;
        for (Filter f: filters) {
            if (f == Filter.OR) sql.OR();
            else if (f == Filter.AND) sql.AND();
            else {
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
}