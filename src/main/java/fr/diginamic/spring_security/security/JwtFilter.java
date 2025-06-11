package fr.diginamic.spring_security.security;

import fr.diginamic.spring_security.entity.UserApp;
import fr.diginamic.spring_security.service.JwtService;
import fr.diginamic.spring_security.service.UserAppService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${auth.cookie.name}")
    private String TOKEN_COOKIE;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserAppService userAppService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getCookies() != null) {
            Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(TOKEN_COOKIE))
                    .map(Cookie::getValue)
                    .forEach(token -> {
                        try {
                            if (jwtService.isTokenValid(token)) {
                                String email = jwtService.getEmailFromToken(token);
                                UserApp userApp = userAppService.getUserApp(email);

                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(
                                                email,
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_" + userApp.getRole().name()))
                                        );
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors du parsing du JWT : " + e.getMessage());
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }
}
