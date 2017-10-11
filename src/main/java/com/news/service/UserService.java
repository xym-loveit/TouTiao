package com.news.service;

import com.news.dao.LoginTicketDAO;
import com.news.dao.UserDAO;
import com.news.model.LoginTicket;
import com.news.model.User;
import com.news.util.ToutiaoUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Baoxu on 2017/8/24.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashedMap();

        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null) {
            map.put("msgname","用户名已存在");
            return map;
        }
        //密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.news.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        return map;
    }

    public Map<String, Object> login(String username, String password) {

        Map<String, Object> map = new HashedMap();

        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msgname","用户名不存在");
            return map;
        }

        if (!user.getName().equals(username)) {
            map.put("msgname","用户名不正确");
            return map;
        }

        if (!user.getPassword().equals(ToutiaoUtil.MD5(password+user.getSalt()))) {
            map.put("msgpwd","密码不正确");
            return map;
        }

        //登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = loginTicketDAO.selectByUserId(userId);
        if (loginTicket == null) {
            loginTicket = new LoginTicket();
            loginTicket.setUserId(userId);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*24);
            loginTicket.setExpired(date);
            loginTicket.setStatus(0);
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
            loginTicketDAO.addTicket(loginTicket);
        } else {
            System.out.println("!!!!!!!!!");
            loginTicket.setUserId(userId);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*24);
            loginTicket.setExpired(date);
            loginTicket.setStatus(0);
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
            loginTicketDAO.updateExpiredAndStatus(loginTicket);
        }
        return loginTicket.getTicket();
    }

    public void deleteTicket(int userId){
        loginTicketDAO.deleteTicket(userId);
    }

}
