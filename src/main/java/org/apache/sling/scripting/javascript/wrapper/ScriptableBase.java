/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.scripting.javascript.wrapper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/** Base class for Scriptable objects, uses the NativeJavaObject wrapper to provide
 *  default wrapping of methods and properties (SLING-397)
 */
public abstract class ScriptableBase extends ScriptableObject {

    private final AtomicReference<NativeJavaObject> cachedNjo = new AtomicReference<>(null);
    private final Set<String> jsMethods = getJsMethodNames();

    public static final String JSFUNC_PREFIX = "jsFunction_";

    protected Object getNative(String name, Scriptable start) {
        final Object wrapped = getWrappedObject();

        if (wrapped == null) {
            return Scriptable.NOT_FOUND;
        }

        if (jsMethods.contains(name)) {
            return Scriptable.NOT_FOUND;
        }

        // Use AtomicReference-based caching with double-checked locking to ensure
        // that NativeJavaObject is initialized only once in a thread-safe manner.
        NativeJavaObject njo = cachedNjo.get();
        if (njo == null) {
            synchronized (this) {
                if (cachedNjo.get() == null) {
                    cachedNjo.set(new NativeJavaObject(start, wrapped, getStaticType()));
                }
            }
            njo = cachedNjo.get();
        }
        return njo.get(name, start);
    }

    /** @return the Java object that we're wrapping, used to create a NativeJavaObject
     *  instance for default wrapping.
     */
    protected abstract Object getWrappedObject();

    /** @return the static type to use for NativeJavaObject wrapping */
    protected abstract Class<?> getStaticType();

    /** @return the Set of method names that clazz defines, i.e. all public methods
     *  with names that start with jsFunction_ */
    private Set<String> getJsMethodNames() {
        final Set<String> result = new HashSet<String>();

        for (Method m : getClass().getMethods()) {
            if (m.getName().startsWith(JSFUNC_PREFIX)) {
                result.add(m.getName().substring(JSFUNC_PREFIX.length()));
            }
        }

        return result;
    }
}
