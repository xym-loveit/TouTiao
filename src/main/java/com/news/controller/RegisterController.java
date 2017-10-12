package com.news.controller;

import com.news.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by Baoxu on 2017/8/24.
 */
@Controller
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    UserService userService;
    @RequestMapping(path = {"/toReg","/toReg/"},method = {RequestMethod.GET,RequestMethod.POST})
    private String toRegister(){
        return "register";
    }

    @RequestMapping(path = {"/reg","/reg/"},method = {RequestMethod.GET,RequestMethod.POST})
    private String login(Model model,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password){
        try {
            Map<String, Object> map = userService.register(username,password);
            if (!map.containsKey("msgname")&&!map.containsKey("msgpwd")) {
                return "regDone";
            } else {
                model.addAttribute("msgname",map.get("msgname"));
                model.addAttribute("msgpwd",map.get("msgpwd"));
                return "register";
            }
        } catch (Exception e) {
            logger.error("注册异常"+e.getMessage());
            model.addAttribute("registerError","注册异常");
            return "register";
        }
    }
    @RequestMapping(path = {"/checkNameUnique"},method = {RequestMethod.POST})
    @ResponseBody
    private String checkNameUnique(@RequestParam("uname") String username){
        return userService.checkUsername(username);
    }
}
