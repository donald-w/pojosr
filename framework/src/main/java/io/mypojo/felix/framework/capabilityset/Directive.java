/*
 * Copyright 2015 Donald W - github@donaldw.com
 * Copyright 2011 Karl Pauls karlpauls@gmail.com
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
 *
 */
package io.mypojo.felix.framework.capabilityset;

public class Directive {
    private final String m_name;
    private final Object m_value;

    public Directive(String name, Object value) {
        m_name = name;
        m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public Object getValue() {
        return m_value;
    }

    public String toString() {
        return m_name + "=" + m_value;
    }
}