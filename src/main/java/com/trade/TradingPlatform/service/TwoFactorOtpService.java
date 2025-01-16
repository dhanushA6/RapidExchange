package com.trade.TradingPlatform.service;

import com.trade.TradingPlatform.modal.TwoFactorOTP;
import com.trade.TradingPlatform.modal.User;

public interface TwoFactorOtpService {

    TwoFactorOTP createTwoFactorOtp( User user, String otp, String jwt);

    TwoFactorOTP findbyUser(Long userId);

    TwoFactorOTP findbyId(String id);

    boolean verifyTwoFactorOPT(TwoFactorOTP twoFactorOTP, String otp);

    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);



}
