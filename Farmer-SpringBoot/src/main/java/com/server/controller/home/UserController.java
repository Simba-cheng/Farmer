package com.server.controller.home;

import com.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录
 * <p>
 * 该项目的定位是通用工具，开箱即用。
 * 接口、功能保留。
 *
 * @author CYX
 * @date 2018/11/10 17:08
 */
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param data
     * @param request
     * @param response
     * @param modelAndView
     */
    @RequestMapping(value = "/index.do", method = RequestMethod.POST)
    public ModelAndView userLogin(String data, HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {

        LOGGER.info("data:{}", new Object[]{data});

        userService.userLogin(data);

        //跳转固定页面
        modelAndView.setViewName("home");

        return modelAndView;
    }

}
