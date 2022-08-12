/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.web.utils;

import com.tny.game.common.result.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.context.request.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class HttpAide {

    public static ResponseEntity<String> status(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public static ResponseEntity<String> status(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }

    public static ResponseEntity<String> status(ResultCode code, HttpStatus status) {
        return new ResponseEntity<>(code.getMessage(), status);
    }

    public static ResponseEntity<String> serverError(ResultCode code) {
        return new ResponseEntity<>(code.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static Map<String, String> requestParam2Map(HttpServletRequest request) {
        return requestParam2Map(request, new HashMap<>());
    }

    public static <M extends Map<String, String>> M requestParam2Map(HttpServletRequest request, M map) {
        for (String key : request.getParameterMap().keySet()) {
            String value = request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

    public static String getIP() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

}
