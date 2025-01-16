package com.trade.TradingPlatform.controller;


import com.trade.TradingPlatform.request.ForgetPasswordTokenRequest;
import com.trade.TradingPlatform.domain.VerificationType;
import com.trade.TradingPlatform.modal.ForgetPasswordToken;
import com.trade.TradingPlatform.modal.User;
import com.trade.TradingPlatform.modal.VerificationCode;
import com.trade.TradingPlatform.request.ResetPasswordRequest;
import com.trade.TradingPlatform.response.ApiResponse;
import com.trade.TradingPlatform.response.AuthResponse;
import com.trade.TradingPlatform.service.EmailService;
import com.trade.TradingPlatform.service.ForgetPasswordService;
import com.trade.TradingPlatform.service.UserService;
import com.trade.TradingPlatform.service.VerificationCodeService;
import com.trade.TradingPlatform.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService ;

    @Autowired
    private EmailService emailService;


    @Autowired
    private ForgetPasswordService forgetPasswordService;


    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt ,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode == null) {
           verificationCode = verificationCodeService.sendVerificationCode(user, verificationType );

        }

        if (verificationType.equals(VerificationType.EMAIl)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());

        }



        return new ResponseEntity<String>("verification OPT send SuccessFully", HttpStatus.OK);
    }


    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIl) ?
                verificationCode.getEmail() : verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if (isVerified) {
            User updatedUser = userService.enableTwoFactorAuthentication(
                    verificationCode.getVerificationType(), sendTo, user);

            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        throw  new Exception("Wrong OTP");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgetPasswordOtp(
            @RequestBody ForgetPasswordTokenRequest req) throws Exception {

          User user  = userService.findUserByEmail(req.getSendTo());
          String otp = OtpUtils.generateOTP();
          UUID  uuid = UUID.randomUUID();
          String id = uuid.toString();

        ForgetPasswordToken token =  forgetPasswordService.findByUser(user.getId());
        if (token == null){
            token = forgetPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
        }

        if (req.getVerificationType().equals(VerificationType.EMAIl)){
            emailService.sendVerificationOtpEmail(user.getEmail(),token.getOtp());


        }
        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password Reset OTP send Successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
           @RequestParam String id,
            @RequestBody ResetPasswordRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {



        ForgetPasswordToken forgetPasswordToken = forgetPasswordService.findById(id);

        boolean isVerified = forgetPasswordToken.getOtp().equals(req.getOtp());

        if (isVerified) {
            userService.updatePassword(forgetPasswordToken.getUser(), req.getPassword());

            ApiResponse res = new ApiResponse();
            res.setMessage("Password Updated Succesfully");
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        throw  new Exception("Wrong OTP");


    }





}
