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

package io.hightide.shell;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.fusesource.jansi.Ansi.Color;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class ShellColors {

    private static final Function<Color, String> colorFunc = color -> ansi().fg(color).toString();

    private static final BiFunction<Color, String, String> colorStrFunc =
            (color, str) -> ansi().fg(color).a(str).fg(Color.DEFAULT).toString();

    private ShellColors() {}

    public static String cyan() {
        return colorFunc.apply(Color.CYAN);
    }

    public static String cyan(String str) {
        return colorStrFunc.apply(Color.CYAN, str);
    }

    public static String green() {
        return colorFunc.apply(Color.GREEN);
    }

    public static String green(String str) {
        return colorStrFunc.apply(Color.GREEN, str);
    }

    public static String magenta(String str) {
        return colorStrFunc.apply(Color.MAGENTA, str);
    }

    public static String yellow(String str) {
        return colorStrFunc.apply(Color.YELLOW, str);
    }

    public static String white() {
        return colorFunc.apply(Color.WHITE);
    }

    public static String white(String str) {
        return colorStrFunc.apply(Color.WHITE, str);
    }

    public static String bold(String str) {
        return ansi().bold().a(str).boldOff().toString();
    }


}
