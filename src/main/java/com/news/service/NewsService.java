package com.news.service;

import com.news.dao.NewsDAO;
import com.news.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//import java.io.File;

/**
 * Created by Baoxu on 2017/8/24.
 */
@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public void addNews(News news){
        newsDAO.addNews(news);
    }

    public News getNews(int id){
        return newsDAO.getById(id);
    }
    public void updateCommentCount(int commentCount,int id){
        newsDAO.updateCommentCount(commentCount,id);
    }

    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);
    }

}
