package cn.javastudy.springboot.web.controller;

import cn.javastudy.springboot.web.bean.OrderInfo;
import cn.javastudy.springboot.web.bean.User;
import cn.javastudy.springboot.web.bean.UserXml;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ResponseBodyController {

    @CrossOrigin
    @GetMapping(value = "/user/json/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getJsonUserInfo(@PathVariable("userId") @Size(min = 5, max = 8) String userId) {
        User user = new User("Java学习", 18);
        user.setId(Long.valueOf(userId));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/user/xml/{userId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<UserXml> getXmlUserInfo(@PathVariable("userId") String userId) {
        UserXml user = new UserXml();
        user.setName("学生1");
        user.setId(userId);

        List<OrderInfo> orderList = new ArrayList<>();
        OrderInfo orderInfo1 = new OrderInfo("123456001", 999, new Date());
        OrderInfo orderInfo2 = new OrderInfo("123456002", 777, new Date());
        OrderInfo orderInfo3 = new OrderInfo("123456003", 666, new Date());
        orderList.add(orderInfo1);
        orderList.add(orderInfo2);
        orderList.add(orderInfo3);
        user.setOrderList(orderList);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/user/save")
    public ResponseEntity<User> saveUser(@RequestBody @Validated User user) {
        user.setId(RandomUtils.nextLong());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/")
    public String index() {
        return "index page.";
    }

}
