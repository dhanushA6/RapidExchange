package com.trade.TradingPlatform.service;

import com.trade.TradingPlatform.domain.VerificationType;
import com.trade.TradingPlatform.modal.ForgetPasswordToken;
import com.trade.TradingPlatform.modal.User;

public interface ForgetPasswordService {


     ForgetPasswordToken createToken(User user,
                                     String id,
                                     String otp,
                                     VerificationType verificationType,
                                     String sendTo
                                     );
     ForgetPasswordToken findById(String id);

     ForgetPasswordToken findByUser(Long userId);

     void deleteToken(ForgetPasswordToken token);

}
