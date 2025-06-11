package fr.diginamic.spring_security.service;

import fr.diginamic.spring_security.dto.AnnonceDto;
import fr.diginamic.spring_security.entity.Annonce;
import fr.diginamic.spring_security.entity.Role;
import fr.diginamic.spring_security.entity.UserApp;
import fr.diginamic.spring_security.repository.AnnonceRepository;
import fr.diginamic.spring_security.repository.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public List<Annonce> getMyAnnonceList(String email) {
        UserApp userApp = userAppRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (userApp.getRole() == Role.CANDIDAT) {
            return annonceRepository.findByCandidaturesUserAppId(userApp.getId());
        } else if (userApp.getRole() == Role.RECRUTEUR) {
            return annonceRepository.findByUserAppId(userApp.getId());
        } else {
            throw new RuntimeException("Rôle invalide");
        }
    }

    public Annonce createAnnonce(Annonce annonce, String email) {
        UserApp userApp = userAppRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (userApp.getRole() != Role.RECRUTEUR) {
            throw new RuntimeException("Seul le recruteur peut créer une annonce");
        }

        annonce.setUserApp(userApp);
        return annonceRepository.save(annonce);
    }

    public AnnonceDto mapToDto(Annonce annonce) {
        return new AnnonceDto(
                annonce.getId(),
                annonce.getTitre(),
                annonce.getDescription(),
                annonce.getUserApp().getEmail()
        );
    }
}
