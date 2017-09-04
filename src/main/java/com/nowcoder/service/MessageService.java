package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Baoxu on 2017/8/30.
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    public void addMessage(Message message){
        messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversionId,int offset,int limit){
        return messageDAO.getConversationDetail(conversionId,offset,limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        // conversation的总条数存在id里
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }

    public void deleteMessage(int id){
        messageDAO.deleteMessage(id);
    }
}
