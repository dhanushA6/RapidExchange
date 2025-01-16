package com.trade.TradingPlatform.request;


import lombok.Data;

@Data
public class ResetPasswordRequest {

    private  String otp;
    private  String password;


}
