package com.Darlondia.EfelerProject.Controllers;

import com.Darlondia.EfelerProject.Services.EmailService;
import com.Darlondia.EfelerProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
        }

        if (!userService.isEmailValid(email)) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if (userService.isEmailExists(email)) { // Updated condition
            String resetCode = userService.generateResetCode();
            userService.saveResetCode(email, resetCode);
            emailService.sendResetCode(email, resetCode);
            return new ResponseEntity<>("Reset code sent to your email", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
        }
    }
}
