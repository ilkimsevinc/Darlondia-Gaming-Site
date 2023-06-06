package com.Darlondia.EfelerProject.Controllers;

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
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String resetCode = requestBody.get("resetCode");
        String newPassword = requestBody.get("newPassword");
        String confirmPassword = requestBody.get("confirmPassword");

        // Add necessary validation checks and the userService methods implementation here
        if (resetCode == null || resetCode.isEmpty()) {
            return new ResponseEntity<>("Reset code is required", HttpStatus.BAD_REQUEST);
        }

        if (!userService.isResetCodeValid(resetCode)) {
            return new ResponseEntity<>("Invalid or expired reset code", HttpStatus.BAD_REQUEST);
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return new ResponseEntity<>("New password is required", HttpStatus.BAD_REQUEST);
        }

        if (!userService.isPasswordValid(newPassword)) {
            return new ResponseEntity<>("Invalid new password", HttpStatus.BAD_REQUEST);
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return new ResponseEntity<>("Confirm password is required", HttpStatus.BAD_REQUEST);
        }

        if (!newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>("New password and confirm password do not match", HttpStatus.BAD_REQUEST);
        }

        userService.updatePassword(resetCode, newPassword);
        return new ResponseEntity<>("Password has been updated", HttpStatus.OK);
    }
}
