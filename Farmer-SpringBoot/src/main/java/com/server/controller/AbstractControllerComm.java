package com.server.controller;

import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author: simba
 * @create: 2020/05/20 00:13
 */
public abstract class AbstractControllerComm {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractControllerComm.class);

    @Resource
    private FreeMarkerConfig freeMarkerConfig;

    /**
     * 根据模板和参数输出HTML字符串
     *
     * @param module       参数map
     * @param templatePath 模板路径
     * @return
     */
    public String getOutputHtmlStr(Map<String, Object> module, String templatePath) {
        StringWriter stringWriter = new StringWriter();
        try {
            Template template = freeMarkerConfig.getConfiguration().getTemplate(templatePath);
            template.process(module, stringWriter);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return StringUtils.EMPTY;
        }
        return stringWriter.toString();
    }

    /**
     * 向页面输出结果
     *
     * @param response
     * @param jsonStr
     */
    public void flushResultToPage(HttpServletResponse response, String jsonStr) {
        try {
            response.setContentType("text/html");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(jsonStr);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
