package com.users.app.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Data

public class AuditTrails {

    @Id
    @GeneratedValue
    private Long id;
    private String action;
    private String description;
    private Date date;
    @Column(columnDefinition = "TEXT")
    private String request;
    @Column(columnDefinition = "TEXT")
    private String response;

}
