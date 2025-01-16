package com.trade.TradingPlatform.repository;

import com.trade.TradingPlatform.modal.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgetPasswordRepository  extends JpaRepository<ForgetPasswordToken,String>{
    ForgetPasswordToken findByUserId(Long userId);
}
