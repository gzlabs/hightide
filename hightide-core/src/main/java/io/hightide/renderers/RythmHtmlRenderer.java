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

package io.hightide.renderers;

import io.hightide.*;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class RythmHtmlRenderer implements RenderEngine {

    private ApplicationConfig appConfig = ApplicationContext.instance().getConfig();

    private ApplicationStage appStage = ApplicationContext.instance().getAppStage();

    private RythmEngine engine;

    private File templateDir;

    private String templateExtension;

    public RythmHtmlRenderer() {
        templateDir = new File(appConfig.getString("dirs.templates"));
        templateExtension = appConfig.getString("app.templates.file-type");

        Properties p = new Properties();
        p.put("rythm.engine.mode", ApplicationStage.DEV.equals(appStage) ? Rythm.Mode.dev : Rythm.Mode.prod);
        p.put("rythm.engine.class_loader.parent", Application.getAppClassLoader());
//        p.put("rythm.resource.loader", new TemplateResourceLoader());
        p.put("rythm.resource.name.suffix", templateExtension);
        if (templateDir.exists()) {
            p.put("rythm.home.template", templateDir);
        }
        File rythmHomeTmp = new File(appConfig.getString("dirs.gen-src"));
        if (!rythmHomeTmp.exists() && !rythmHomeTmp.mkdirs()) {
            throw new RuntimeException("Failed to create temporary directory for Rythm templates.");
        }
        p.put("rythm.home.tmp", rythmHomeTmp);
        engine = new RythmEngine(p);
    }

    @Override
    public String render(HightideExchange exchange) throws Exception {
        Object obj = exchange.getResponse().getReturnedObj();
        String resource = exchange.getResolvedRoute().getInvocationAction().getClazz().getSimpleName().toLowerCase();
        String action = exchange.getResolvedRoute().getInvocationAction().getMethod().getName();

        File templateFile = new File(templateDir + File.separator + resource + File.separator + action + "." + templateExtension);
        if (templateFile.exists()) {
            return engine.render(templateFile, obj);
        }

        /** Try to find template in classpath */
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(resource + File.separator + action + "." + templateExtension);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String templateStr = br.lines().reduce("", String::concat);
            return engine.renderString(templateStr, obj);
        } catch (Exception e) {
            throw new TemplateNotFoundException("Template " + templateFile.getPath() + " does not exist.", e);
        }
    }

    private class TemplateResourceLoader implements ITemplateResourceLoader {

        @Override
        public ITemplateResource load(String path) {
            return null;
        }

        @Override
        public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerTemplateClass) {
            return null;
        }

        @Override
        public String getFullName(TemplateClass tc, RythmEngine engine) {
            return null;
        }

        @Override
        public void scan(String root, TemplateResourceManager manager) {

        }
    }
}
