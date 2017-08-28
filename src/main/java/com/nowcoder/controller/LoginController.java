package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Baoxu on 2017/8/24.
 */
@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/toLogin","/toLogin/"},method = {RequestMethod.GET,RequestMethod.POST})
    private String toLogin(){
        return "login";

    }
    @RequestMapping(path = {"/login","/login/"},method = {RequestMethod.GET,RequestMethod.POST})
    private String login(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value="rember", defaultValue = "0") int rememberme,
                         HttpServletResponse response,
                         Model model){
        User user = new User();
        Map<String, Object> map = userService.login(username,password);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            if (rememberme > 0) {
                cookie.setMaxAge(3600 * 24 * 5);
            }
            response.addCookie(cookie);
            return "redirect:/";
        } else {
            model.addAttribute("msgname",map.get("msgname"));
            model.addAttribute("msgpwd",map.get("msgpwd"));
            return "login";
        }
    }

}
