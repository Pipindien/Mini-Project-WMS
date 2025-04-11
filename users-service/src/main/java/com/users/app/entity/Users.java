package com.users.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name ="users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long custId;

    private String fullName;

    @Email(message = "Format email tidak valid")
    private String email;
    private String password;
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Format nomor telepon tidak valid")
    private String phone;
    private String username;
    private Integer age;
    private Double salary;
    private String role;
}
