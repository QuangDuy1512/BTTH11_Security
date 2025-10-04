package vn.iotstar.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo1")
public class Demo1Controller {

    @GetMapping("/hello")
    public String hello() {
        return "Hello public (no authentication required)";
    }

    @GetMapping("/customers")
    public List<String> customers() {
        // đây là dữ liệu cứng để test .authenticated()
        return List.of("Customer A", "Customer B", "Customer C");
    }

    @GetMapping("/admin")
    public String adminOnly() {
        return "Admin only area (ROLE_ADMIN)";
    }
}

