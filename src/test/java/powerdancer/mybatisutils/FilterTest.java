package powerdancer.mybatisutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;


public class FilterTest {
    @Test
    public void testUnmarshal() throws Throwable {
        Filter f = Filter.unmarshalFrom(EmployeeColumns.class, "NAME", "IN", "powerDancer", "carolina");
        Assertions.assertEquals(EmployeeColumns.NAME, f.col());
        Assertions.assertEquals(Operator.IN, f.operator());
        Assertions.assertArrayEquals(new Object[]{"powerDancer", "carolina"}, f.operands());

        f = Filter.unmarshalFrom(EmployeeColumns.class, "JOIN_TIME", "GREATER_THAN", 0L);
        Assertions.assertEquals(EmployeeColumns.JOIN_TIME, f.col());
        Assertions.assertEquals(Operator.GREATER_THAN, f.operator());
        Assertions.assertArrayEquals(new Object[]{Instant.EPOCH}, f.operands());
    }
}
