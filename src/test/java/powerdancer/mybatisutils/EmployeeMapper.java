package powerdancer.mybatisutils;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;

public interface EmployeeMapper extends FindMapper<Employee> {
    @Insert("Insert into Employee (ID,NAME,JOIN_TIME,SUPERVISOR_ID) values (#{id}, #{name}, #{joinTime}, #{supervisorId, jdbcType=BIGINT})")
    void insert(@Param("id") long id, @Param("name") String name, @Param("joinTime") Instant joinTime, @Param("supervisorId") Long supervisorId);

    @Override
    default String tableName() {
        return "Employee";
    }

    @Override
    default ColumnMethodMapper methodMapper() {
        return EmployeeColumns.methodMapper();
    }
}
