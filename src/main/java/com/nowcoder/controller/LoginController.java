package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(path = {"/login","/login/"},method = {RequestMethod.GET})
    private String login(){
        return "login";
    }
    @RequestMapping(path = {"/toLogout/{userId}"},method = {RequestMethod.GET})
    private String toLogout(@PathVariable("userId") int userId){
        userService.deleteTicket(userId);
        return "redirect:/";
    }

    /**
     * 如果max-age属性为正数，则表示该cookie会在max-age秒之后自动失效(无论浏览器是否关闭)。
     * 如果max-age为负数，则表示该cookie仅在本浏览器窗口以及本窗口打开的子窗口内有效，关闭窗口后该cookie即失效。
     * @param username
     * @param password
     * @param rememberme
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(path = {"/login","/login/"},method = {RequestMethod.POST})
    private String login(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value="rember", defaultValue = "0") int rememberme,
                         HttpServletResponse response,
                         Model model){
        Map<String, Object> map = userService.login(username,password);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            if (rememberme > 0) {
                cookie.setMaxAge(3600 * 24 * 5);
            } else {
                cookie.setMaxAge(-1);
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
