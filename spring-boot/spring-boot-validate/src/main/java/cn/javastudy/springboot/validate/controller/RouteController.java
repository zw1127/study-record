package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.route.Ipv4RouteEntry;
import cn.javastudy.springboot.validate.domain.route.Ipv6RouteEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
@Tag(name = "route-controller")
public class RouteController {

    @PostMapping("/add-ipv4-route")
    @DynamicValidation(pojoName = "route-ipv4")
    public String addIpv4Route(@RequestBody Ipv4RouteEntry ipv4RouteEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-ipv6-route")
    @DynamicValidation(pojoName = "route-ipv6")
    public String addIpv6Route(@RequestBody Ipv6RouteEntry ipv6RouteEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
