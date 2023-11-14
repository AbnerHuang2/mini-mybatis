package org.skitii.ibatis.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/14
 **/
@Data
@AllArgsConstructor
public class BoundSql {
    private String sql;
    private Map<Integer, String> parameterMappings;
    private String parameterType;
    private String resultType;
}
