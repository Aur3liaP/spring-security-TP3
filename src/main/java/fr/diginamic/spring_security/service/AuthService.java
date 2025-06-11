package fr.diginamic.spring_security.service;

import fr.diginamic.spring_security.dto.LoginRequestDto;
import fr.diginamic.spring_security.entity.Role;
import fr.diginamic.spring_security.entity.UserApp;
import fr.diginamic.spring_security.exception.AuthException;
import fr.diginamic.spring_security.repository.UserAppRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Value("${auth.cookie.name}")
    private String COOKIE_NAME;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder bcrypt;

    public void registerUser(LoginRequestDto request) throws AuthException {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());

        if (userAppService.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Un utilisateur avec cet email existe déjà");
        }

        try {
            userAppService.createUser(request);
        } catch (Exception e) {
            throw new AuthException("Erreur lors de la création de l'utilisateur");
        }
    }


    public void loginUser(String email, String password, HttpServletResponse response) throws AuthException {
        if (email == null || password == null) {
            throw new AuthException("Email et mot de passe requis");
        }

        try {
            Optional<UserApp> userOpt = userAppRepository.findByEmail(email);

            if (userOpt.isEmpty() || !bcrypt.matches(password, userOpt.get().getPassword())) {
                throw new AuthException("Identifiants invalides");
            }

            ResponseCookie jwtCookie = jwtService.generateToken(userOpt.get().getEmail(), userOpt.get().getRole().name());
            response.addHeader("Set-Cookie", jwtCookie.toString());

        } catch (Exception e) {
            throw new AuthException("Erreur lors de la connexion");
        }
    }

    public void logoutUser(HttpServletResponse response) throws AuthException {
        try {
            removeJwtCookie(response);
        } catch (Exception e) {
            throw new AuthException("Erreur lors de la déconnexion");
        }
    }

    private void validateEmail(String email) throws AuthException {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthException("L'email est requis");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new AuthException("Format d'email invalide");
        }
    }

    private void validatePassword(String password) throws AuthException {
        if (password == null || password.length() < 6) {
            throw new AuthException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }


    private void removeJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(COOKIE_NAME, "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }
}
