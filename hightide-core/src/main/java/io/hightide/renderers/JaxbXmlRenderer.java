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

import io.hightide.HightideExchange;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class JaxbXmlRenderer implements RenderEngine {

    private JAXBContext jc;

    public JaxbXmlRenderer() {
        try {
            jc = JAXBContext.newInstance();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String render(HightideExchange exchange) {
        try {
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            Object obj = exchange.getResponse().getReturnedObj();
            Class clazz = obj.getClass();
            JAXBElement elem = new JAXBElement<>(new QName(clazz.getSimpleName().toLowerCase()), clazz, obj);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(elem, baos);
            return baos.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
