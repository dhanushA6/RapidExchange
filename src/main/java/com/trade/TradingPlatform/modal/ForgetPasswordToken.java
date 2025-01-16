package com.trade.TradingPlatform.modal;


import com.trade.TradingPlatform.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ForgetPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  String id;

    @OneToOne
    private  User user;

    private  String otp;

    private VerificationType verificationType;

    private  String sendTo;


}
