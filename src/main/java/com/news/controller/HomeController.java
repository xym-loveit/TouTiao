package com.news.controller;

import com.news.model.EntityType;
import com.news.model.HostHolder;
import com.news.model.News;
import com.news.model.ViewObject;
import com.news.service.LikeService;
import com.news.service.NewsService;
import com.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baoxu on 2017/8/24.
 */
@Controller
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    NewsService newsService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId,offset,limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<ViewObject> vos = new ArrayList<>();
        for(News news : newsList){
            ViewObject vo = new ViewObject();
            vo.set("news",news);
            vo.set("user", userService.getUser(news.getUserId()));
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }


    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    private String index(Model model){
        List<ViewObject> vos = getNews(0,0,10);
        model.addAttribute("vos",vos);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    private String userIndex(@PathVariable("userId") int userId, Model model){
        List<ViewObject> vos = getNews(userId,0,10);
        model.addAttribute("vos",vos);
        return "home";
    }

}
