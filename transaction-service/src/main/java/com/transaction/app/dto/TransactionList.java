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

    private String productName;
    private Double productPrice;
    private String goalName;

    public TransactionList(String trxNumber, Long id, Double amount, String status, Long custId,
                           Integer lot, Long productId, Long goalId, String notes, Date createdDate) {
        this.trxNumber = trxNumber;
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.custId = custId;
        this.lot = lot;
        this.productId = productId;
        this.goalId = goalId;
        this.notes = notes;
        this.createdDate = createdDate;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
}
