package com.trade.TradingPlatform.service;

import com.trade.TradingPlatform.domain.VerificationType;
import com.trade.TradingPlatform.modal.User;
import com.trade.TradingPlatform.modal.VerificationCode;

public interface VerificationCodeService {

      VerificationCode sendVerificationCode(User user , VerificationType verificationType);

      VerificationCode getVerificationCodeById(Long id ) throws Exception;

      VerificationCode getVerificationCodeByUser(Long userId);

      void deleteVerificationCodeById(VerificationCode verificationCode);




}
