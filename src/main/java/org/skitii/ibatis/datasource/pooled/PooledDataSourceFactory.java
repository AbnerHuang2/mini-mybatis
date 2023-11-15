package org.skitii.ibatis.datasource.pooled;


import org.skitii.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author skitii
 * @since 2023/11/13
 **/
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }

    @Override
    public DataSource getDataSource() {
       return this.dataSource;
    }
}
