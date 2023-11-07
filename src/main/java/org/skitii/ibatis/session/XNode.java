package org.skitii.ibatis.session;

import lombok.Data;

import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
public class XNode {
    private String namespace;
    private String id;
    private String parameterType;
    private String resultType;
    private String sql;
    private Map<Integer, String> parameter;
}
