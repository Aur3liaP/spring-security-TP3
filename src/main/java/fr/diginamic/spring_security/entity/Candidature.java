package fr.diginamic.spring_security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "candidature")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lettre_motivation", nullable = false, columnDefinition = "TEXT")
    private String lettreMotivation;

    @ManyToOne()
    @JoinColumn(name = "user_app_id", nullable = false)
    private UserApp userApp;

    @ManyToOne()
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_app_id", "annonce_id"})
    })
    public static class CandidatureConstraint {}

}
