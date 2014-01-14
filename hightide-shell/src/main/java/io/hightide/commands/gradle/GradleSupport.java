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

package io.hightide.commands.gradle;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class GradleSupport {

    public ProjectConnection getProjectConnection() {
        return GradleConnector.newConnector()
                .forProjectDirectory(new File(System.getProperty("user.dir")))
                .connect();
    }

    public void runTask(String task) {
        ProjectConnection pc = getProjectConnection();
        try {
            pc.newBuild().forTasks(task).withArguments("-q").run();
        } catch (Exception e) {
            System.err.println("Failed to run task '" + task + "'");
        } finally {
            pc.close();
        }
    }
}
