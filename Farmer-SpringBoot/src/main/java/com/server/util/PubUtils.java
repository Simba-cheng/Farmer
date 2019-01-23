package com.server.util;

import com.server.constant.CommConstant;
import freemarker.template.Template;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公共方法
 *
 * @author CYX
 * @create 2018-11-14-21:38
 */
@Service
public class PubUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubUtils.class);

    //Java Bean 约束校验
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    /**
     * 判断是否是Windows系统
     *
     * @return true-windows
     */
    public static boolean isWindowsOS() {

        boolean isWindowsOS = false;

        String osName = System.getProperty(CommConstant.SYSTEM_NAME);
        if (osName.toLowerCase().indexOf(CommConstant.WINDOWS_NAME) > -1) {
            isWindowsOS = true;
        }

        return isWindowsOS;
    }

    /**
     * 检查变量是否为空，只要其中一个变量为空则返回true
     *
     * @param params 参数列表
     * @return
     */
    public static boolean checkParamsIsEmpty(String... params) {

        boolean result = false;

        for (String param : params) {

            if (StringUtils.isEmpty(param)) {
                result = true;
                break;
            }
        }
        return result;
    }

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
            String json = jsonStr;
            PrintWriter writer = response.getWriter();
            writer.print(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取页面文件流
     *
     * @param request
     * @return
     */
    public static MultipartFile getMultipartFile(HttpServletRequest request) throws Exception {

        //页面控件的文件流
        MultipartFile multipartFile = null;

        request.setCharacterEncoding("UTF-8");

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        Map map = multipartRequest.getFileMap();

        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            multipartFile = (MultipartFile) map.get(obj);
        }

        return multipartFile;
    }

    /**
     * 从cookie中获取参数
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String getCookieStr(HttpServletRequest request, String key) {

        String resultValue = StringUtils.EMPTY;

        Cookie[] cookies = request.getCookies();

        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    resultValue = cookie.getValue();
                }
            }
        }
        return resultValue;
    }

    /**
     * 校验Java bean对象中的字段
     *
     * @param t
     * @return
     */
    public static <T> List<String> validateObjectField(T t) {

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            messageList.add(constraintViolation.getMessage());
        }

        return messageList;
    }

}
