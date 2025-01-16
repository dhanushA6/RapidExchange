package com.trade.TradingPlatform.service;

import com.trade.TradingPlatform.modal.TwoFactorOTP;
import com.trade.TradingPlatform.modal.User;
import com.trade.TradingPlatform.repository.TwoFactorOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class TwoFactorOtpServiceImpl  implements  TwoFactorOtpService{

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;
    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();

        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);
        twoFactorOtpRepository.save(twoFactorOTP);
        return null;
    }

    @Override
    public TwoFactorOTP findbyUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findbyId(String id) {
        Optional<TwoFactorOTP> otp = twoFactorOtpRepository.findById(id);

        return otp.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOPT(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        twoFactorOtpRepository.delete(twoFactorOTP);

    }
}
