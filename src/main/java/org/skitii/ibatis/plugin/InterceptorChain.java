package org.skitii.ibatis.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/27
 **/
public class InterceptorChain {
    private final List<Interceptor> interceptors = new ArrayList<>();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
