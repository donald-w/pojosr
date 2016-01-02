/*
 * Copyright 2016 Donald W - github@donaldw.com
 * Copyright 2015 Kamran Zafar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mypojo.jcl.proxy;

import io.mypojo.jcl.JclUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates cglib proxies
 *
 * @author Kamran Zafar
 */
public class CglibProxyProvider implements ProxyProvider {

    public Object createProxy(Object object, Class superClass, Class[] interfaces, ClassLoader cl) {
        CglibProxyHandler handler = new CglibProxyHandler(object);

        Enhancer enhancer = new Enhancer();

        if (superClass != null) {
            enhancer.setSuperclass(superClass);
        }

        enhancer.setCallback(handler);

        if (interfaces != null) {
            List<Class> il = new ArrayList<Class>();

            for (Class i : interfaces) {
                if (i.isInterface()) {
                    il.add(i);
                }
            }

            enhancer.setInterfaces(il.toArray(new Class[il.size()]));
        }

        enhancer.setClassLoader(cl == null ? JclUtils.class.getClassLoader() : cl);

        return enhancer.create();
    }

    private class CglibProxyHandler implements MethodInterceptor {
        private final Object delegate;

        public CglibProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        /**
         * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
         * java.lang.reflect.Method, java.lang.Object[],
         * net.sf.cglib.proxy.MethodProxy)
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
            return delegateMethod.invoke(delegate, args);
        }
    }
}
