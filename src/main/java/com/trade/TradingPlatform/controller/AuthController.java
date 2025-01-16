package com.trade.TradingPlatform.controller;

import com.trade.TradingPlatform.config.JwtProvider;
import com.trade.TradingPlatform.modal.TwoFactorOTP;
import com.trade.TradingPlatform.modal.User;
import com.trade.TradingPlatform.repository.UserRepository;

import com.trade.TradingPlatform.response.AuthResponse;
import com.trade.TradingPlatform.service.CustomUserDetailsService;
import com.trade.TradingPlatform.service.EmailService;
import com.trade.TradingPlatform.service.TwoFactorOtpService;
import com.trade.TradingPlatform.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
        // Create a new User object


        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist !=null){
            throw  new Exception(("Email Already Exists"));
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setPassword(user.getPassword());
        newUser.setRole(user.getRole());

        // Save the new user to the repository
        User savedUser = userRepository.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()

        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);




        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("register success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password  = user.getPassword();

        Authentication auth = authenticate(userName, password);


        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);
        User authuser = userRepository.findByEmail(user.getEmail());

        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse  res = new AuthResponse();
            res.setMessage("Two Factor auth is enabled ");
            res.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findbyUser(authuser.getId());

            if (oldTwoFactorOTP != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }

            TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(authuser, otp, jwt);

             emailService.sendVerificationOtpEmail(userName, otp);


             res.setSession(newTwoFactorOtp.getId());

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid UserName");
        }
        if (!password.equals(userDetails.getPassword())){
            throw  new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigInOTP(
            @PathVariable String otp,
            @RequestParam String id) throws Exception {

        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findbyId(id);

        if (twoFactorOtpService.verifyTwoFactorOPT(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two Factor authentication Verified ");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        throw new Exception("invalid otp");


    }
}
