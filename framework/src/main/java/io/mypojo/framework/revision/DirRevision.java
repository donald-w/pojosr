/*
 * Copyright 2016 Donald W - github@donaldw.com
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

package io.mypojo.framework.revision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

public class DirRevision implements Revision {
    private static final Logger logger = LoggerFactory.getLogger(DirRevision.class);

    private final File file;

    public DirRevision(File file) {
        this.file = file;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    public Enumeration<String> getEntries() {
        return new FileEntriesEnumeration(file);
    }

    @Override
    public URL getEntry(String entryName) {
        try {
            if (entryName != null) {
                File file = (new File(this.file, (entryName.startsWith("/")) ? entryName.substring(1) : entryName));
                if (file.exists()) {
                    return file.toURL();
                }
            }
        } catch (MalformedURLException e) {
            logger.error("Getting entry: " + entryName, e);
        }
        return null;
    }
}
