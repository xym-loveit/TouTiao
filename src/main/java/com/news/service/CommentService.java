package com.news.service;

import com.news.dao.CommentDAO;
import com.news.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Baoxu on 2017/8/30.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    public List<Comment> getCommentsById(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }

    public void addComment(Comment comment){
        commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }
}
