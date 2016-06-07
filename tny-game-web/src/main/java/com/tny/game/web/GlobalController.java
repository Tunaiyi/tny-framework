package com.tny.game.web;

import com.tny.game.web.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
        LOGGER.error("处理 : {} 异常", request, e);
        return HttpUtils.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
