package com.server.service.impl;

import com.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author CYX
 * @date 2018/11/11 16:02
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void userLogin(String data) {

        LOGGER.info("===== userLogin =====");

    }
}
