package fr.diginamic.spring_security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class JwtService {

    private final String SECRET;
    private final SecretKey key;
    private final long EXPIRATION_TIME;
    private String COOKIE_NAME;
    private int COOKIE_MAX_AGE;

    public JwtService(@Value("${jwt.secret}") String SECRET, @Value("${jwt.expiration}") long EXPIRATION_TIME, @Value("${auth.cookie.name}") String COOKIE_NAME, @Value("${auth.cookie.expiration}") int COOKIE_MAX_AGE) {
        this.SECRET = SECRET;
        System.out.println("Secret Key: " + SECRET);
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        this.EXPIRATION_TIME = EXPIRATION_TIME;
        this.COOKIE_NAME = COOKIE_NAME;
        this.COOKIE_MAX_AGE = COOKIE_MAX_AGE;
    }

    public ResponseCookie generateToken(String email, String role) {
        String token = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expiré");
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println("Format du token non supporté");
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("Token malformé");
            return false;
        } catch (SignatureException e) {
            System.out.println("Signature invalide");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Token vide ou null");
            return false;
        }catch (Exception e) {
            System.out.println("C'est pas normal ça....");
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    };


    public String getEmailFromToken(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            String token = Stream.of(cookies)
                    .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (token != null) {
                return getSubject(token);

            }
        }
        throw new Exception("Nothing found with cookie");
    }

}