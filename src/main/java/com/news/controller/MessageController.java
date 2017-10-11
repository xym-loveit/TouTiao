package com.news.controller;

import com.news.model.HostHolder;
import com.news.model.Message;
import com.news.model.User;
import com.news.model.ViewObject;
import com.news.service.MessageService;
import com.news.service.UserService;
import com.news.util.ToutiaoUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Baoxu on 2017/8/30.
 */
@Controller
public class MessageController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("targetId", targetId);
                vo.set("totalCount", msg.getId());
                vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            return "letter";
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }
    /**
     * 单个对话的详细展示
     * @param conversationId
     * @param model
     * @return
     */
    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String conversationDetail(@RequestParam("conversationId") String conversationId, Model model){
        try {
            List<Message> conversionList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            ViewObject vo = new ViewObject();
            for (Message message : conversionList) {
                vo.set("message",message);
                User user = userService.getUser(message.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userName",user.getName());
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        } catch (Exception e) {
            logger.error("获取站内信列表失败"+e.getMessage());
        } finally {
            return "letterDetail";
        }
    }

    /**
     * 发送站内信
     * @param fromId
     * @param toId
     * @param content
     * @return
     */
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setContent(content);
        message.setCreatedDate(new Date());
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) :
                String.format("%d_%d", toId, fromId));
        messageService.addMessage(message);
        return ToutiaoUtil.getJSONString(message.getId());
    }

    /**
     * 删除单个对话当中的某一条消息
     * @param messageId
     * @return
     */
    @RequestMapping(path = {"/msg/deleteMessage"},method = {RequestMethod.GET})
    public String deleteMessage(@RequestParam("messageId")int messageId,
                                @RequestParam("conversationId")String  conversationId){
        try {
            System.out.println("get in");
            messageService.deleteMessage(messageId);
        } catch (Exception e) {
            logger.error("删除信息失败"+e.getMessage());
        } finally {
            return "redirect:/msg/detail?conversationId="+conversationId;
        }
    }
}
