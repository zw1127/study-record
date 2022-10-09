package cn.javastudy.disruptor.basic.controller;

import cn.javastudy.disruptor.basic.service.BasicEventService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicEventController {

    @Autowired
    private BasicEventService basicEventService;

    @RequestMapping(value = "/{value}", method = RequestMethod.GET)
    public String publish(@PathVariable("value") String value) {
        basicEventService.publish(value);
        return "success, " + LocalDateTime.now();
    }
}
