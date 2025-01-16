package com.trade.TradingPlatform.repository;

import com.trade.TradingPlatform.modal.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP , String> {
    TwoFactorOTP findByUserId(Long userId);

}
