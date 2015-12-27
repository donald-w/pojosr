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
 */

package io.mypojo.launch;

import io.mypojo.framework.launch.ManifestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Map;

/**
 * @author donald-w
 */
public class ManifestUtilTest {

    @Test
    public void testGetHeaders() throws Exception {
        URL manifestURL = this.getClass().getResource("test1-manifest.mf");

        Map<String,String> headers = ManifestUtil.getHeaders(manifestURL);

        Assert.assertEquals(3,headers.size());
    }

    @Test(expected = Exception.class)
    public void testGetHeadersExceptionNoSpace() throws Exception {
        URL manifestURL = this.getClass().getResource("test2-manifest.mf");

        ManifestUtil.getHeaders(manifestURL);
    }

    @Test
    public void testGetHeadersExceptionMissingKey() throws Exception {
        URL manifestURL = this.getClass().getResource("test3-manifest.mf");

        Map<String,String> headers = ManifestUtil.getHeaders(manifestURL);

        Assert.assertEquals(3,headers.size());
    }

    @Test(expected = Exception.class)
    public void testGetHeadersExceptionMissingValue() throws Exception {
        URL manifestURL = this.getClass().getResource("test4-manifest.mf");

        ManifestUtil.getHeaders(manifestURL);
    }

    @Test(expected = Exception.class)
    public void testGetHeadersExceptionDuplicateAttribute() throws Exception {
        URL manifestURL = this.getClass().getResource("test5-manifest.mf");

        ManifestUtil.getHeaders(manifestURL);
    }
}
