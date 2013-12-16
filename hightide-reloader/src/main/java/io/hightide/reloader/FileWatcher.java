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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import static java.nio.file.StandardWatchEventKinds.*;


public final class FileWatcher {

    private FileWatcher() {}

    public static void spawn(Path path, BiConsumer<FileChangeEvent, Path> consumer) throws IOException, InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();

        final WatchService watcher = FileSystems.getDefault().newWatchService();
        Map<WatchKey, Path> watchedPaths = new HashMap<>();

        registerPath(watcher, watchedPaths, path);

        service.submit(() -> {
            while (Thread.interrupted() == false) {
                WatchKey key = null;
                try {
                    key = watcher.take();
                } catch (InterruptedException | ClosedWatchServiceException e) {
                    System.err.println("Stopping file watcher");
                    e.printStackTrace();
                    break;
                }

                for (WatchEvent event : key.pollEvents()) {
                    final WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    final Path filename = ev.context();
                    final Path changedPath = watchedPaths.get(key).resolve(filename);
                    final boolean isDir = Files.isDirectory(changedPath);

                    if (ev.kind() == ENTRY_CREATE) {
                        if (isDir) {
                            try {
                                registerPath(watcher, watchedPaths, changedPath);
                            } catch (IOException e) {
                                System.err.println("Stopping file watcher");
                                e.printStackTrace();
                                break;
                            }
                        } else {
                            consumer.accept(FileChangeEvent.FILE_CREATED, changedPath);
                        }
                    }
                    if (ev.kind() == ENTRY_DELETE) {
                        if (!isDir) {
                            consumer.accept(FileChangeEvent.FILE_DELETED, changedPath);
                        }
                    }
                    if (ev.kind() == ENTRY_MODIFY) {
                        if (!isDir) {
                            consumer.accept(FileChangeEvent.FILE_MODIFIED, changedPath);
                        }
                    }
                }

                // Reset key and if it's invalid exit loop.
                if (!key.reset()) {
                    break;
                }
            }
        });

    }

    private static void registerPath(WatchService watcher, Map<WatchKey, Path> watchedPaths, Path rootDir) throws IOException {
        if (!Files.isDirectory(rootDir)) {
            throw new NotDirectoryException("You need to provide a directory path.");
        }
        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                watchedPaths.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });

    }

    public enum FileChangeEvent {
        FILE_DELETED, FILE_MODIFIED, FILE_CREATED, DIR_DELETED, DIR_MODIFIED, DIR_CREATED;
    }

}
