package fr.diginamic.spring_security.controller;

import fr.diginamic.spring_security.dto.LoginRequestDto;
import fr.diginamic.spring_security.entity.Role;
import fr.diginamic.spring_security.entity.UserApp;
import fr.diginamic.spring_security.exception.AuthException;
import fr.diginamic.spring_security.service.AuthService;
import fr.diginamic.spring_security.service.UserAppService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserAppService userAppService;

    @PostMapping("/create-candidat")
    public ResponseEntity<String> createCandidat(@RequestBody LoginRequestDto request) {
        try {
            request.setRole(Role.CANDIDAT);
            authService.registerUser(request);
            return ResponseEntity.ok("Candidat créé");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create-recruteur")
    public ResponseEntity<String> createRecruteur(@RequestBody LoginRequestDto request) {
        try {
            request.setRole(Role.RECRUTEUR);
            authService.registerUser(request);
            return ResponseEntity.ok("Recruteur créé");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createAdmin(@RequestBody LoginRequestDto request) {
        try {
            request.setRole(Role.ADMIN);
            authService.registerUser(request);
            return ResponseEntity.ok("Admin créé");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-admin-by-id/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteAdminById(@PathVariable Integer id) {
        try {
            userAppService.deleteUserAppById(id);
            return ResponseEntity.ok("Admin supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserApp userApp, HttpServletResponse response) {
        try {
            authService.loginUser(userApp.getEmail(), userApp.getPassword(), response);
            return ResponseEntity.ok("Connexion réussie");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        try {
            authService.logoutUser(response);
            return ResponseEntity.ok("Déconnexion réussie");
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

