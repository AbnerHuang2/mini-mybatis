package org.skitii.ibatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author skitii
 * @since 2023/11/13
 **/
public interface DataSourceFactory {

    void setProperties(Properties props);

    DataSource getDataSource();
}
