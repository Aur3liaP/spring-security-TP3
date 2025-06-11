package fr.diginamic.spring_security.controller;

import fr.diginamic.spring_security.dto.AnnonceDto;
import fr.diginamic.spring_security.entity.Annonce;
import fr.diginamic.spring_security.service.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/annonce")
public class AnnonceController {

    @Autowired
    private AnnonceService annonceService;

    @GetMapping("/get-all")
    public ResponseEntity<List<AnnonceDto>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces();
        List<AnnonceDto> dtoList = annonces.stream()
                .map(annonceService::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/get-my-annonce-list")
    public ResponseEntity<List<AnnonceDto>> getMyAnnonceList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<Annonce> annonces = annonceService.getMyAnnonceList(email);
        List<AnnonceDto> dtoList = annonces.stream()
                .map(annonceService::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('RECRUTEUR')")
    public ResponseEntity<String> createAnnonce(@RequestBody Annonce annonce, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Annonce createdAnnonce = annonceService.createAnnonce(annonce, email);
        return ResponseEntity.ok("Annonce créée");
    }
}
