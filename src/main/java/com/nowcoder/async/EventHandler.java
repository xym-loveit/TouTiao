package com.nowcoder.async;

import java.util.List;

/**
 * Created by Baoxu on 2017/9/4.
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
