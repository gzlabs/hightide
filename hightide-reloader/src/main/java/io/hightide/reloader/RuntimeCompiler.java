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

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.getProperty;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static javax.tools.JavaCompiler.CompilationTask;

public class RuntimeCompiler {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeCompiler.class);

    private static DiagnosticCollector<JavaFileObject> diagnostics;

    private String targetDirName;

    public RuntimeCompiler(String targetDirName) {
        requireNonNull(targetDirName);
        this.targetDirName = targetDirName.endsWith(File.separator)
                ? targetDirName : targetDirName.concat(File.separator);
    }

    public void run(final List<Path> files) throws IOException {
        if (files.isEmpty()) {
            logger.info("Nothing to compile.");
            return;
        }
        List<String> filenames = files.stream().map(Path::toString).collect(toList());
        CompilationTask task = makeCompilerTask(filenames);
        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.err::println);
            throw new IOException("Failed to compile classes");
        }
    }

    public void run(Path file) throws IOException {
        this.run(Arrays.asList(file));
    }

    private CompilationTask makeCompilerTask(final List<String> filenames) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            logger.error("Compiler not found; Check if tools.jar is not in your classpath and try again.");
            System.exit(1);
        }

        diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileMan = compiler.getStandardFileManager(diagnostics, null, null);

        File targetDir = new File(targetDirName);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("Failed to create target directory '" + targetDirName + "'");
        }
        fileMan.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(targetDir));

        /** Setting compiler's classpath */
        final String classPath = getProperty("java.class.path", ".");
        List<File> classPathJars = stream(classPath.split(":")).map(s -> new File(s)).collect(toList());
        classPathJars.add(targetDir);
        fileMan.setLocation(StandardLocation.CLASS_PATH, classPathJars);
        logger.debug("Classpath: {}", fileMan.getLocation(StandardLocation.CLASS_PATH));

        logger.info("Compiling file(s): {}", filenames);
        Iterable<? extends JavaFileObject> fileObjs = fileMan.getJavaFileObjectsFromStrings(filenames);
        return compiler.getTask(null, fileMan, diagnostics, Arrays.asList("-Xplugin:HightideCompilerPlugin"), null, fileObjs);

    }

}
