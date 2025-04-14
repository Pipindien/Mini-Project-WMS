package com.users.app.dto;

import jakarta.validation.constraints.*;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {

    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    private String fullName;

    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    private String phone;

    @NotNull(message = "Umur harus diisi")
    @Min(value = 17, message = "Umur minimal 17 tahun")
    private Integer age;

    @NotNull(message = "Gaji harus diisi")
    @DecimalMin(value = "1000.0", message = "Gaji minimal 1000")
    private Double salary;

    // Token biasanya dikirim di header, tapi kalau tetap ingin validasi:
    private String token;
}
