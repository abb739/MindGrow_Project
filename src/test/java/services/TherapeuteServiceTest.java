package services;

import models.Specialite;
import models.Therapeute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TherapeuteServiceTest {

    private TherapeuteService tService;
    private SpecialiteService sService;
    private int specIdValide; // ID d'une spécialité temporaire pour le test

    private final String TEST_EMAIL = "test_unit_therapeute@gmail.com";

    @BeforeEach
    void setUp() throws SQLException {
        tService = new TherapeuteService();
        sService = new SpecialiteService();

        // Créer une spécialité temporaire pour lier le thérapeute
        Specialite tempSpec = new Specialite(0, "SpecPourTherapeuteTest", "Temp");
        sService.ajouter(tempSpec);

        // Récupérer son ID
        specIdValide = sService.afficher().stream()
                .filter(s -> s.getNom().equals("SpecPourTherapeuteTest"))
                .findFirst().get().getId();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // 1. Supprimer le thérapeute de test
        List<Therapeute> list = tService.afficher();
        for (Therapeute t : list) {
            if (t.getEmail().equals(TEST_EMAIL) || t.getEmail().equals("modified_" + TEST_EMAIL)) {
                tService.supprimer(t.getId());
            }
        }

        // 2. Supprimer la spécialité temporaire
        sService.supprimer(specIdValide);
    }

    @Test
    void ajouter() throws SQLException {
        Therapeute t = new Therapeute(
                0, "NomTest", "PrenomTest", TEST_EMAIL,
                specIdValide, null, "default.png", false
        );

        tService.ajouter(t);

        // Vérification
        boolean found = tService.afficher().stream()
                .anyMatch(th -> th.getEmail().equals(TEST_EMAIL));
        assertTrue(found, "Le thérapeute devrait être présent dans la base.");
    }

    @Test
    void modifier() throws SQLException {
        // Ajouter
        Therapeute t = new Therapeute(
                0, "NomTest", "PrenomTest", TEST_EMAIL,
                specIdValide, null, "default.png", false
        );
        tService.ajouter(t);

        // Récupérer ID
        int id = tService.afficher().stream()
                .filter(th -> th.getEmail().equals(TEST_EMAIL))
                .findFirst().get().getId();

        // Modifier
        Therapeute modif = new Therapeute(
                id, "NomModifie", "PrenomModifie", "modified_" + TEST_EMAIL,
                specIdValide, null, "new_photo.png", true
        );
        tService.modifier(modif);

        // Vérifier
        Therapeute updated = tService.afficher().stream()
                .filter(th -> th.getId() == id)
                .findFirst().orElse(null);

        assertNotNull(updated);
        assertEquals("NomModifie", updated.getNom());
        assertEquals("modified_" + TEST_EMAIL, updated.getEmail());
        assertTrue(updated.isEstVerifie());
    }

    @Test
    void supprimer() throws SQLException {
        // Ajouter
        Therapeute t = new Therapeute(
                0, "NomTest", "PrenomTest", TEST_EMAIL,
                specIdValide, null, "default.png", false
        );
        tService.ajouter(t);

        // Récupérer ID
        int id = tService.afficher().stream()
                .filter(th -> th.getEmail().equals(TEST_EMAIL))
                .findFirst().get().getId();

        // Supprimer
        tService.supprimer(id);

        // Vérifier
        boolean found = tService.afficher().stream()
                .anyMatch(th -> th.getId() == id);
        assertFalse(found, "Le thérapeute ne devrait plus exister.");
    }

    @Test
    void afficher() throws SQLException {
        // On ajoute un juste pour être sûr qu'il y en a au moins un
        Therapeute t = new Therapeute(
                0, "NomList", "PrenomList", TEST_EMAIL,
                specIdValide, null, "default.png", false
        );
        tService.ajouter(t);

        List<Therapeute> list = tService.afficher();
        assertNotNull(list);
        assertFalse(list.isEmpty());

        // Vérifier que la jointure fonctionne (spec_name n'est pas null)
        // Note : Cela dépend si votre méthode afficher() remplit bien le champ specialiteNom
        Therapeute monTherapeute = list.stream()
                .filter(th -> th.getEmail().equals(TEST_EMAIL))
                .findFirst().orElse(null);

        assertNotNull(monTherapeute);
        // Si votre service fait le JOIN correctement, ceci devrait être vrai :
        // assertNotNull(monTherapeute.getSpecialiteNom());
    }
}