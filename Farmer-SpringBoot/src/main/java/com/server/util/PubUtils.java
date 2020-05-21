package com.server.util;

import com.server.constant.CommConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公共方法
 *
 * @author CYX
 * @create 2018-11-14-21:38
 */
public class PubUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubUtils.class);

    /**
     * Java Bean 约束校验
     */
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

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

        for (Object obj : map.keySet()) {
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
