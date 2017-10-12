package com.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Baoxu on 2017/10/11.
 */
@Controller
public class TestController {
    @RequestMapping(path = {"/t1"},method = {RequestMethod.GET})
    private String test1(){
        System.out.println("执行test1方法了");
        return "/loginTest";
    }
}
