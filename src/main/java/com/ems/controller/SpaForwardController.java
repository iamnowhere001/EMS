package com.ems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/employee",
            "/organization",
            "/dashboard",
            "/attendance",
            "/workflow",
            "/salary",
            "/leave",
            "/personal",
            "/system",
            "/role-manage"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
