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

import java.net.*;

public class FormulaUtils {

    public static String ufl(String value) {
        if (value.length() == 0) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String lfl(String value) {
        if (value.length() == 0) {
            return value;
        }
        if (value.length() == 1) {
            return value.toLowerCase();
        }
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static String ul(String value) {
        return value.toUpperCase();
    }

    public static String ll(String value) {
        return value.toLowerCase();
    }

    public static String urle(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String urld(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
