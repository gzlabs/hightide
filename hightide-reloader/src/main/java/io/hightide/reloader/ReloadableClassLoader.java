/*
 * Copyright (c) 2013. Ground Zero Labs, Private Company.
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

package io.hightide.reloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Resource;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

public class ReloadableClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(ReloadableClassLoader.class);

    private final URLClassPath ucp;

    private final String classesDir;

    public ReloadableClassLoader(URL[] urls, String classesDir) throws IOException, ClassNotFoundException {
        super(urls, ReloadableClassLoader.class.getClassLoader());
        this.ucp = new URLClassPath(urls);
        this.classesDir = classesDir;

        List<Path> paths = FileMatcher.match(classesDir, "glob:*.class");
        logger.trace("Loading classes: ", paths);
        for (Path path : paths) {
            loadClass(normalizeClassname(path.toString()));
        }
    }

    protected Class<?> loadClass(Path path) throws ClassNotFoundException {
        return loadClass(normalizeClassname(path.toString()), true);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    c = super.loadClass(name, false);
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    private String normalizeClassname(String name) {
        return name.substring(classesDir.length() + 1).replace(File.separator, ".")
                .replace(".class", "");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public Class<?> reloadClass(String name) throws ClassNotFoundException {
        logger.info("Reloading class " + name + "...");
        Class<?> c = findLoadedClass(name);

        String path = name.replace('.', '/').concat(".class");
        Resource res = ucp.getResource(path, false);
        if (res != null) {
            try {
                if (ReloaderAgent.isEnabled()) {
                    ReloaderAgent.reload(new ClassDefinition(c, res.getBytes()));
                } else {
                    throw new UnsupportedOperationException("Class " + name + " cannot be reloaded; Instrumentation agent not loaded.");
                }
                return c;
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            } catch (UnmodifiableClassException | UnsupportedOperationException e) {
                throw new UnsupportedOperationException("Class " + name + " cannot be reloaded.", e);
            }
        } else {
            throw new ClassNotFoundException(name);
        }
    }

}
