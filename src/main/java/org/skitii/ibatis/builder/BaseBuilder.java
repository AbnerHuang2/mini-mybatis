package org.skitii.ibatis.builder;

import org.skitii.ibatis.session.Configuration;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public abstract class BaseBuilder {
    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
