package org.skitii.ibatis.builder;

import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.type.TypeAliasRegistry;
import org.skitii.ibatis.type.TypeHandlerRegistry;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public abstract class BaseBuilder {
    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;

    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected Class<?> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
