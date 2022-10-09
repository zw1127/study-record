package cn.javastudy.disruptor.consumer.controller;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumeModeController {

	@Autowired
	@Qualifier("independentModeService")
    private ConsumeModeService oneConsumerService;


	@RequestMapping(value = "/{value}", method = RequestMethod.GET)
	public String publish(@PathVariable("value") String value) {
		oneConsumerService.publish(value);
		return "success, " + LocalDateTime.now().toString();
	}
}
