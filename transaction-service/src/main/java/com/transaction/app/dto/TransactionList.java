package com.transaction.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TransactionList {
    private String trxNumber;
    private Long id;
    private Double amount;
    private String status;
    private Long custId;
    private Integer lot;
    private Long productId;
    private Long goalId;
    private String notes;
    private Date createdDate;
}
