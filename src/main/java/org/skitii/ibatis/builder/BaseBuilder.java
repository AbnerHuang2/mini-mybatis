package org.skitii.ibatis.builder;

import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.type.TypeAliasRegistry;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public abstract class BaseBuilder {
    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
