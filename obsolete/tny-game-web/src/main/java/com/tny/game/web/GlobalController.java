/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.web;

import com.tny.game.web.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map.Entry;

import static com.tny.game.common.utils.StringAide.*;

@Controller
public class GlobalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalController.class);

    /**
     * 用于处理异常的
     *
     * @return
     */
    @ResponseBody
    @ExceptionHandler({Throwable.class})
    public Object exception(HttpServletRequest request, Throwable e) {
        Enumeration<String> headNames = request.getHeaderNames();
        String heads = "";
        while (headNames.hasMoreElements()) {
            String key = headNames.nextElement();
            heads += key + ":" + request.getHeader(key) + "\n";
        }
        String params = "";
        for (Entry<String, String[]> paramEntry : request.getParameterMap().entrySet()) {
            params += paramEntry.getKey() + ":" + StringUtils.join(paramEntry.getValue(), "|") + "\n";
        }
        String requestStr = format("HttpServletRequest:\n url={}\nquery={}\nmethod={}\nhead:\n{}\nparams:\n{}",
                request.getRequestURL(),
                request.getQueryString(),
                request.getMethod(),
                heads,
                params);
        LOGGER.error("处理Http 请求异常\n{}", requestStr, e);
        return HttpAide.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
