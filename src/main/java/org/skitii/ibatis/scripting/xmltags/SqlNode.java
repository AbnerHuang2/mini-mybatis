package org.skitii.ibatis.scripting.xmltags;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public interface SqlNode {

    boolean apply(DynamicContext context);
}
