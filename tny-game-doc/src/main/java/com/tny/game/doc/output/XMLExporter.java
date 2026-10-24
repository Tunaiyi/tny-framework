/*
 * Copyright (c) 2020 Tunaiyi
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

package com.tny.game.doc.output;

import com.thoughtworks.xstream.XStream;
import com.tny.game.doc.table.*;

import java.io.IOException;

/**
 * xml 格式化
 * Created by Kun Yang on 2017/4/8.
 */
class XMLExporter implements Exporter {

    private final XStream xstream = new XStream();

    XMLExporter() {
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
    }

    @Override
    public String output(OutputScheme table) throws IOException {
        return xstream.toXML(table);
    }

    @Override
    public String getHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }

}
