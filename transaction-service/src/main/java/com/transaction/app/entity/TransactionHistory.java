package com.transaction.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String status;
    private Double amount;
    private Integer lot;
    private Long custId;
    private Long productId;
    private Long goalId;
    private Date createdDate;
    private String notes;

    @ManyToOne
    private Transaction transaction;
}
