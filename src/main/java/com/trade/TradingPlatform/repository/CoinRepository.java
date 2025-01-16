package com.trade.TradingPlatform.repository;

import com.trade.TradingPlatform.modal.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository  extends JpaRepository<Coin, String> {


}
