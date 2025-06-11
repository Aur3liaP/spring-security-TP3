package fr.diginamic.spring_security.service;

import fr.diginamic.spring_security.dto.LoginRequestDto;
import fr.diginamic.spring_security.entity.Role;
import fr.diginamic.spring_security.entity.UserApp;
import fr.diginamic.spring_security.repository.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAppService implements UserDetailsService {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder bcrypt;

    public void createUser(LoginRequestDto request) {
        Optional<UserApp> existingUser = userAppRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà");
        }

        String encodedPassword = bcrypt.encode(request.getPassword());
        UserApp newUser = UserApp.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .entreprise(request.getEntreprise())
                .role(request.getRole())
                .build();

        userAppRepository.save(newUser);
    }

    public Optional<UserApp> findByEmail(String email) {
        return userAppRepository.findByEmail(email);
    }

    public UserApp getUserApp(String email) {
        return userAppRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public UserApp getUserAppById(Integer userId) {
        return userAppRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void deleteUserAppById(Integer id) {
        userAppRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(email);
        UserApp user = userAppRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("JWT invalide ou utilisateur inexistant (peut être normal à l'inscription) :" + email));
        return new UserAppDetailsService(user);
    }
}
