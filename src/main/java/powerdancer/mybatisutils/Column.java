package powerdancer.mybatisutils;

public interface Column {
    String name();
    int ordinal();

    default boolean indexed() {
        return false;
    }

    default String alias() {
        return name();
    }
}
