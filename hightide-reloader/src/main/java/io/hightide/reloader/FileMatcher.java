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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public abstract class FileMatcher {

    public static List<Path> match(String directory, String globPattern) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globPattern);

        final List<Path> foundPaths = new ArrayList<>();

        Files.walkFileTree(Paths.get(directory), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                find(foundPaths, matcher, dir);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                find(foundPaths, matcher, file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.err.println(exc);
                return CONTINUE;
            }

        });

        return foundPaths;
    }

    // Compares the glob pattern against
    // the file or directory name.
    private static void find(List<Path> paths, PathMatcher matcher, Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            paths.add(file);
        }
    }


}
