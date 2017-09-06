package com.nowcoder.controller;

import com.nowcoder.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Baoxu on 2017/9/6.
 */
@Controller
public class MailController {

    @Autowired
    MailSender mailSender;
    @RequestMapping(path = {"/sendMail"},method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String sendMail(){
        System.out.println("!!!!");
        Map<String, Object> map = new HashMap();
        map.put("username","程宝旭");
        mailSender.sendWithHTMLTemplate("13012999962@163.com", "登陆异常",
                "mails/mailModel.html", map);
        return "sakjfesdf";
    }
}
