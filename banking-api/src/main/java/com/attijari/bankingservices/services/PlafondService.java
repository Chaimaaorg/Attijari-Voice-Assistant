package com.attijari.bankingservices.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

public interface PlafondService {
    void updateCardLimitByCardNum(Long cardNum, BigDecimal limit,String typeLimit, Optional<Timestamp> duration,String status);
    void updateCardLimitByCardType(Long userID,String cardType,BigDecimal limit,String typeLimit,Optional<Timestamp> duration,String status);
}
