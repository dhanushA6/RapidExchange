package com.trade.TradingPlatform.request;


import com.trade.TradingPlatform.domain.VerificationType;
import lombok.Data;

@Data
public class ForgetPasswordTokenRequest {
    private  String sendTo;
    private VerificationType verificationType;

}
