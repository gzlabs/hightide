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

package io.hightide;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class TemplateFetcher {

    private static final int BUFFER = 2048;

    private static String TEMP_ARCHIVE_NAME = "template.tar.gz";

    private TemplateFetcher() {}

    public static Path getPrototype(Path tempDir, String templateName) throws IOException {
        if (isNull(templateName)) {
            return null;
        }
        String[] tkns;
        if ((tkns = templateName.split(":")).length < 2) {
            return null;
        }

        String templateType = tkns[0];
        String orgProj;
        switch (templateType) {
            case "file":
                String filePath = tkns[1];
                Path file = Paths.get(filePath);
                if (filePath.endsWith(".tar.gz")) {
                    return extractTemplate(file, tempDir);
                } else {
                    // Copy dir
                    if (Files.isDirectory(file)) {
                        return file;
                    }
                }
                return null;
            case "git":
                //git archive master --remote=<repo>| gzip > archive.tar.gz
            case "github":
                orgProj = tkns[1];
                System.out.println("Downloading template from github.com");

                httpGetTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME), "http://github.com/" + orgProj + "/archive/master.tar.gz");
                return extractTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME));
            case "bitbucket":
                orgProj = tkns[1];
                System.out.println("Downloading template from bitbucket.org");
                httpGetTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME), "http://bitbucket.org/" + orgProj + "/get/master.tar.gz");
                return extractTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME));
            case "url":
                String fileUrl = tkns[1];
                System.out.println("Downloading template archive from " + fileUrl);
                httpGetTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME), fileUrl);
                return extractTemplate(tempDir.resolve(TEMP_ARCHIVE_NAME));
            default:
                return null;
        }
    }

    private static void httpGetTemplate(Path tempFile, String url) throws IOException {
        CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(url));
        byte[] buffer = new byte[BUFFER];
        try (
                InputStream input = response.getEntity().getContent();
                OutputStream output = new FileOutputStream(tempFile.toFile())
        ) {
            for (int length; (length = input.read(buffer)) > 0; ) {
                output.write(buffer, 0, length);
            }
        }
    }

    public static Path extractTemplate(Path tempFile) throws IOException {
        return extractTemplate(tempFile, tempFile.getParent());
    }

    public static Path extractTemplate(Path tempFile, Path targetDir) throws IOException {
        try (
                FileInputStream fin = new FileInputStream(tempFile.toFile());
                BufferedInputStream in = new BufferedInputStream(fin);
                GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
                TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn)
        ) {
            TarArchiveEntry entry;
            Path rootDir = null;
            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry.getName());
                if (entry.isDirectory()) {
                    if (isNull(rootDir)) {
                        rootDir = targetDir.resolve(entry.getName());
                    }
                    Files.createDirectory(targetDir.resolve(entry.getName()));
                } else {
                    int count;
                    byte data[] = new byte[BUFFER];

                    FileOutputStream fos = new FileOutputStream(targetDir.resolve(entry.getName()).toFile());
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = tarIn.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
                }
            }

            return rootDir;
        }
    }
}
