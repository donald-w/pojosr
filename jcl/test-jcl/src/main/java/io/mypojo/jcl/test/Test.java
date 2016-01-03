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

package io.mypojo.jcl.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Test implements Serializable, TestInterface {

    /**
     * serialVersionUID:long
     */
    private static final long serialVersionUID = 7683330206220877077L;
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    private String firstName;
    private String lastName;

    public Test() {
        firstName = "World";
        lastName = "";
    }

    public Test(String firstName) {
        this.firstName = firstName;
    }

    public String sayHello() {
        String hello = "Hello " + firstName + " " + lastName;

        logger.info("Hello " + firstName + " " + lastName);

        return hello;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
