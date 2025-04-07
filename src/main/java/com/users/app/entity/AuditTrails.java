package com.users.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
    private String request;
    private String response;

}
