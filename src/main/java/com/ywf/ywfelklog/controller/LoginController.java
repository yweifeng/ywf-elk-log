package com.ywf.ywfelklog.controller;

import com.ywf.ywfelklog.annotation.SystemLog;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:ywf
 */
@RestController
public class LoginController {

    @RequestMapping("/login")
    @SystemLog
    public String login(@RequestParam String username, @RequestParam String pwd) {
        return "success";
    }

    @RequestMapping("/logout")
    @SystemLog
    public String logout(@RequestParam String username) {
        return "success";
    }
}
