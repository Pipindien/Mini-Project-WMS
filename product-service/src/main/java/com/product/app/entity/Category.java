package com.product.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private Long categoryId;
    private String categoryType;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    private Date createdDate;
    private Date updateDate;

}