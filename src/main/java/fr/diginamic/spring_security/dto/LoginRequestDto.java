package fr.diginamic.spring_security.dto;

import fr.diginamic.spring_security.entity.Role;
import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private String entreprise;
    private Role role;

    public LoginRequestDto() {}

    public LoginRequestDto(String email, String entreprise, Role role) {
        this.email = email;
        this.entreprise = entreprise;
        this.role = role;
    }
}