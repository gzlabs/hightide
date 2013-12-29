package io.hightide;

import io.hightide.shell.HightideShell;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

public class AppGenerator {

    private static void copyFile(Path source, Path target) {
        CopyOption[] options = new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING};
        if (Files.notExists(target)) {
            try {
                System.out.println("Creating file " + target);
                Files.copy(source, target, options);
            } catch (IOException x) {
                System.err.format("Unable to copy: %s: %s%n", source, x);
            }
        }
    }

    private static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private final Map<String, Object> attributes;
        private final Set<String> ignoreFiles;

        TreeCopier(Path source, Path target, Map<String, Object> attributes) {
            this.source = source;
            this.target = target;
            this.attributes = attributes;
            String ignoreFilesVal;
            if (nonNull(ignoreFilesVal = (String) attributes.get("ignoreFiles")))  {
                this.ignoreFiles = Arrays.asList(ignoreFilesVal.split(","))
                        .stream().map(String::trim)
                        .collect(toSet());
            } else {
                this.ignoreFiles = null;
            }
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = new CopyOption[]{COPY_ATTRIBUTES};

            Path newdir = target.resolve(source.relativize(dir));
            try {
                System.out.println("Creating directory " + newdir);
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n", newdir, x);
                return SKIP_SUBTREE;
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (nonNull(ignoreFiles) && ignoreFiles.contains(file.getFileName().toString())) {
                return CONTINUE;
            }
            final String TEMPL_EXT = ".tmpl";
            if (file.toString().endsWith(TEMPL_EXT)) {
                System.out.println("Parsing template " + file);
                TemplateParser.parseTemplate(file, target.resolve(
                        source.relativize(Paths.get(file.toString().replace(TEMPL_EXT, "")))), attributes);
            } else {
                copyFile(file, target.resolve(source.relativize(file)));
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null) {
                Path newdir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x) {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
                }
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                System.err.println("cycle detected: " + file);
            } else {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }
            return CONTINUE;
        }
    }

    public static void generate(Path source, Path target) throws IOException {
        Map<String, Object> attributes = loadAttributes(source);
        boolean isDir = Files.isDirectory(target);
        Path dest = (isDir) ? target.resolve(source.getFileName()) : target;

        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, dest, attributes);
        Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
    }

    private static Map<String, Object> loadAttributes(Path source) throws IOException {
        Map<String, Object> attributes = new HashMap<>();
        Properties prop = new Properties();
        prop.load(new FileInputStream(source.resolve("prototype.properties").toFile()));
        prop.forEach((k, v) -> {
            try {
                String newValue = HightideShell.instance().readLine("What's the '" + k + "' [default is " + v + "]");
                if (isNull(newValue) || newValue.equals("")) {
                    newValue = (String) v;
                }
                attributes.put(k.toString(), newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        return attributes;
    }
}