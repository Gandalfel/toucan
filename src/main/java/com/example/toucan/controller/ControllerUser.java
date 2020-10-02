package com.example.toucan.controller;

import com.example.toucan.service.ServiceUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/toucan/user")
public class ControllerUser {

    private final ServiceUser serviceUser;

    public ControllerUser(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @PutMapping("/resetpassword")
    @PreAuthorize("permitAll()")
    public void changePassword(String email) {
        //todo create change password mechanism
        serviceUser.changePassword(email);
    }
}
