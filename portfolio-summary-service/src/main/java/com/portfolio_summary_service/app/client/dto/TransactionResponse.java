package com.portfolio_summary_service.app.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransactionResponse {
    private String status;
    private Double amount;
    private Long custId;
    private Long productId;
    private Long goalId;
    private Double productPrice;
    private Integer lot;
    private Date createdDate;
    private Date updateDate; //updateDate kan berarti transaksinya dibayar di tanggal ini.
}
