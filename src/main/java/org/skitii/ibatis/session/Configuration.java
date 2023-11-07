package org.skitii.ibatis.session;

import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
public class Configuration {
    Connection connection;

    Map<String, String> dataSource;

    Map<String, XNode> mapperElement;


}
