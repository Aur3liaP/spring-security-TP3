package fr.diginamic.spring_security.repository;

import fr.diginamic.spring_security.entity.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByUserAppId(Long userAppId);
    List<Candidature> findByAnnonceId(Long annonceId);
    Optional<Candidature> findByUserAppIdAndAnnonceId(Long userAppId, Long annonceId);
    boolean existsByUserAppIdAndAnnonceId(Long userAppId, Long annonceId);
}