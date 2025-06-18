package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Operation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OperationService {
    List<Operation> getTransactionByUserId(Long userId);

    List<Operation> getTransactionByUserIdEntities(Long userId, Map<String, Object> entitiesDict);

    void addTransactionByAccountType(Long userId, String accountType, String ribBeneficiare, BigDecimal amount, String note, String type);

    void addTransactionByAccountNum(Long userId, String accountNum, String ribBeneficiare, BigDecimal amount, String note, String type);

}
