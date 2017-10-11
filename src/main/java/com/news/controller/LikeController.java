package com.news.controller;

import com.news.async.EventModel;
import com.news.async.EventProducer;
import com.news.async.EventType;
import com.news.model.EntityType;
import com.news.model.HostHolder;
import com.news.service.LikeService;
import com.news.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Baoxu on 2017/9/3.
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like/{newsId}"},method = {RequestMethod.GET, RequestMethod.POST})
    public String like(@PathVariable("newsId") int newsId){
        if (hostHolder.getUser() != null) {
            long likeCount = likeService.like(hostHolder.getUser().getId(),EntityType.ENTITY_NEWS,newsId);
            // 更新喜欢数
            newsService.updateLikeCount(newsId, (int) likeCount);
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setEntityOwnerId(newsService.getNews(newsId).getUserId())
                    .setActorId(hostHolder.getUser().getId()).setEntityId(newsId)
                    .setEntityType(EntityType.ENTITY_NEWS));
        }
        return "redirect:/";
    }

    @RequestMapping(path = {"/dislike/{newsId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String dislike(@PathVariable("newsId") int newsId) {
        if (hostHolder.getUser() != null) {
            long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
            // 更新喜欢数
            newsService.updateLikeCount(newsId, (int) likeCount);
        }
        return "redirect:/";
    }

}
