package services;

import models.Specialite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SpecialiteServiceTest {

    private SpecialiteService service;
    // Un nom unique pour repérer nos données de test
    private final String TEST_NAME = "Spec_Test_Unit_XYZ";

    @BeforeEach
    void setUp() {
        service = new SpecialiteService();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // NETTOYAGE : On cherche notre spécialité de test et on la supprime
        List<Specialite> list = service.afficher();
        for (Specialite s : list) {
            if (s.getNom().equals(TEST_NAME) || s.getNom().equals("Spec_Modified")) {
                service.supprimer(s.getId());
            }
        }
    }

    @Test
    void ajouter() throws SQLException {
        Specialite s = new Specialite(0, TEST_NAME, "Description test");
        service.ajouter(s);

        // Vérification
        boolean found = service.afficher().stream()
                .anyMatch(spec -> spec.getNom().equals(TEST_NAME));
        assertTrue(found, "La spécialité devrait être ajoutée.");
    }

    @Test
    void modifier() throws SQLException {
        // 1. Ajouter
        service.ajouter(new Specialite(0, TEST_NAME, "Desc"));

        // 2. Trouver l'ID
        int id = service.afficher().stream()
                .filter(s -> s.getNom().equals(TEST_NAME))
                .findFirst().get().getId();

        // 3. Modifier
        service.modifier(new Specialite(id, "Spec_Modified", "New Desc"));

        // 4. Vérifier
        Specialite updated = service.afficher().stream()
                .filter(s -> s.getId() == id)
                .findFirst().orElse(null);

        assertNotNull(updated);
        assertEquals("Spec_Modified", updated.getNom());
    }

    @Test
    void supprimer() throws SQLException {
        // 1. Ajouter
        service.ajouter(new Specialite(0, TEST_NAME, "Desc"));

        // 2. Trouver l'ID
        int id = service.afficher().stream()
                .filter(s -> s.getNom().equals(TEST_NAME))
                .findFirst().get().getId();

        // 3. Supprimer
        service.supprimer(id);

        // 4. Vérifier
        boolean found = service.afficher().stream().anyMatch(s -> s.getId() == id);
        assertFalse(found, "La spécialité ne devrait plus exister.");
    }

    @Test
    void afficher() throws SQLException {
        assertNotNull(service.afficher());
    }
}