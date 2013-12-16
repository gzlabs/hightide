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

import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class TemplateParser {

    private TemplateParser() {}

    public static void parseTemplate(Path template, Path target, Map<String, Object> attributes) {
        try {
            ST st = new ST(readFile(template, Charset.forName("UTF-8")), '$', '$');
            attributes.forEach(st::add);
            st.write(target.toFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
}
