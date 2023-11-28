package org.skitii.middleware.spring.test.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String userEmail;
}
