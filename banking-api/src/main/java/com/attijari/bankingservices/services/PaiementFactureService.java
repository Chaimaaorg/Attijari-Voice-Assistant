package com.attijari.bankingservices.services;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface PaiementFactureService {
    void addInvoiceTransactionByAccountType(Long userId,String invoiceNum, String accountType);

    void addInvoiceTransactionByAccountNum(String invoiceNum, String accountNum);

}
