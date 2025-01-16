package com.trade.TradingPlatform.modal;


import com.trade.TradingPlatform.domain.VerificationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    @Enumerated(EnumType.STRING)
    private VerificationType sendTo;
}
