package com.transaction.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String trxNumber;
    private String status;
    private Double amount;
    private Integer lot;
    private Long custId;
    private Long productId;
    private Double productPrice;
    private Long goalId;
    private Date createdDate;
    private Date updateDate;
}
