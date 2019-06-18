package powerdancer.mybatisutils;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import powerdancer.asmproxy.InterfaceUtils;
import powerdancer.asmproxy.MapClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.V1_8;


public enum ResultSetProxy {
    INSTANCE;

    final MapClassLoader cl = new MapClassLoader(ResultSetProxy.class.getClassLoader());
    final AtomicInteger classCount = new AtomicInteger(0);

    public static <T> void registerHandler(Configuration config, ColumnMethodMapper cmm, Class<T> type) {
        INSTANCE.doRegisterHandler(config, cmm, type);
    }

    public <T> void doRegisterHandler(Configuration config, ColumnMethodMapper cmm, Class<T> type) {
        
        String generatedClassName = type.getSimpleName() + classCount.getAndIncrement();
        InterfaceUtils.generateImplClass(
                (name, classPayload) -> cl.add(name, classPayload),
                V1_8,
                generatedClassName,
                method-> (args -> {
                    Object[] data = args.get(0, Object[].class);
                    Column c = cmm.map(method).orElseThrow(() -> new IllegalStateException("Unable to find column for method " + method));
                    return data[c.ordinal()];
                }),
                type
        );

        Constructor constructor;
        try {
            constructor = cl.loadClass(generatedClassName).getConstructor(Object.class);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to register handler", e);
        }

        config.getTypeHandlerRegistry().register(type, new TypeHandler<T>() {
            @Override
            public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {

            }

            @Override
            public T getResult(ResultSet rs, String columnName) throws SQLException {
                Object[] data = new Object[cmm.methods().length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = config.getTypeHandlerRegistry().getTypeHandler(cmm.methods()[i].getReturnType())
                            .getResult(rs, i + 1);
                }
                try {
                    return (T)constructor.newInstance(new Object[]{data});
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException("unable to instantiate result class ", e);
                }
            }

            @Override
            public T getResult(ResultSet rs, int columnIndex) throws SQLException {
                return null;
            }

            @Override
            public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
                return null;
            }
        });
    }
}
