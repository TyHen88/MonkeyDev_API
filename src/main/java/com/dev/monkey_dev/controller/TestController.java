// package com.dev.monkey_dev.controller;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/test")
// public class TestController {

// @GetMapping("/oauth2")
// public String testOAuth2() {
// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
// return "OAuth2 Test - Authentication: " + (auth != null ? auth.getName() :
// "null");
// }
// }
