package fr.diginamic.spring_security.dto;

import lombok.Data;

@Data
public class AnnonceDto {
    private Integer id;
    private String titre;
    private String description;
    private String emailRecruteur;

    public AnnonceDto(Integer id, String titre, String description, String emailRecruteur) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.emailRecruteur = emailRecruteur;
    }
}
