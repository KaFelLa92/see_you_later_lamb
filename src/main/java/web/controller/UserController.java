package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seeyoulaterlamb/user")
public class UserController {
    // [*] DI
    private final UserService userService;




}
