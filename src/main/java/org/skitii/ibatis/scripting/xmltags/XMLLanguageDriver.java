package org.skitii.ibatis.scripting.xmltags;

import org.dom4j.Element;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }
}
