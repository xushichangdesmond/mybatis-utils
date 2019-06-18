package powerdancer.mybatisutils;

import java.time.Instant;

public interface Employee {
    long id();
    String name();
    Instant joinTime();

    Long supervisorId();

    default boolean isBigBoss() {
        return supervisorId() == null;
    }
}
