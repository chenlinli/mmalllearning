package com.mmall.controller;

import com.mmall.dao.UserMapper;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    private  static final Logger  logger  =LoggerFactory.getLogger(TestController.class);

    public static void main(String[] args) {
        TimeStamp a = new TimeStamp((System.currentTimeMillis()));
        System.out.println(a);
        Date c = new Date();
        System.out.println(c);

    }

    @RequestMapping("test.do")
    @ResponseBody
    public  String test(String str){
        logger.info("test info");
        logger.warn("warn ibfo");
        logger.error("error info");
        return "testValue"+str;
    }
}
