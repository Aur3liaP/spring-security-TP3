package fr.diginamic.spring_security.repository;

import fr.diginamic.spring_security.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findAll();
    List<Annonce> findByUserAppId(Integer userAppId);
    @Query("SELECT a FROM Annonce a JOIN a.candidatures c WHERE c.userApp.id = :userAppId")
    List<Annonce> findByCandidaturesUserAppId(@Param("userAppId") Integer userAppId);
}
