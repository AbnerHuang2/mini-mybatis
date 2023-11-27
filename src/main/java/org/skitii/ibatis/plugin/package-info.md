该包主要是包含mybatis插件的实现逻辑。
核心功能处理在 Plugin 类中。

Mybatis的基本逻辑分为两部分。
1. 解析，如果将相关内容解析成对象，保存到configuration中。
2. 使用。有些是在sql执行前使用，如ParamentHandler，有些是在sql执行后使用，如ResultSetHandler。

Mybatis的插件机制，就是在解析和使用的过程中，插入自己的逻辑。
1. 解析插件【XMLConfigBuilder#pluginElement】
2. 添加到configuration中【Configuration#addInterceptor】
3. 使用插件【InterceptorChain#pluginAll】，具体体现在 configuration.newStatementHandler()、configuration.newResultSetHandler()、configuration.newParameterHandler()、configuration.newExecutor()等方法中。