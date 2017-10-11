package com.news.async.handler;

import com.news.async.EventHandler;
import com.news.async.EventModel;
import com.news.async.EventType;
import com.news.model.Message;
import com.news.model.User;
import com.news.service.MessageService;
import com.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Baoxu on 2017/9/4.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {

        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        int toId = model.getEntityOwnerId();
        int fromId = 999;
        message.setToId(toId);
        message.setContent("用户" + user.getName() +
                " 赞了你的资讯,http://localhost:8080/news/"
                + String.valueOf(model.getEntityId()));
        // SYSTEM ACCOUNT
        message.setFromId(fromId);
        message.setConversationId(fromId < toId? String.format("%d_%d", fromId, toId) :
                String.format("%d_%d", toId, fromId));
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
