package org.skitii.ibatis.test.domain;

import lombok.Data;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String userEmail;
}
