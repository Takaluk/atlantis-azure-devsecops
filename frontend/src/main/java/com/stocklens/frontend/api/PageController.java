package com.stocklens.frontend.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "forward:/home.html";
    }

    @GetMapping("/home")
    public String home() {
        return "forward:/home.html";
    }

    @GetMapping("/search")
    public String search() {
        return "forward:/search.html";
    }

    @GetMapping("/watch")
    public String watch() {
        return "forward:/watch.html";
    }

    @GetMapping("/stock")
    public String stock() {
        return "forward:/stock.html";
    }
}
