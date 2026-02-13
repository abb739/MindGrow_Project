import entities.Abonnement;
import entities.Paiement;
import services.AbonnementService;
import services.PaiementService;

import java.time.LocalDateTime;
import java.util.List;

public class TestServices {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       TEST DES SERVICES CRUD           ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        testAbonnementService();
        System.out.println("\n" + "=".repeat(50) + "\n");
        testPaiementService();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         TESTS TERMINÉS                 ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void testAbonnementService() {
        System.out.println("📋 TEST SERVICE ABONNEMENT");
        System.out.println("-".repeat(50));

        AbonnementService service = new AbonnementService();

        // Test READ
        System.out.println("\n1️⃣ Test READ - Récupérer tous les abonnements");
        List<Abonnement> abonnements = service.getAllAbonnements();
        System.out.println("   ✅ " + abonnements.size() + " abonnement(s) trouvé(s)");

        if (!abonnements.isEmpty()) {
            Abonnement premier = abonnements.get(0);
            System.out.println("   📄 Premier abonnement:");
            System.out.println("      - ID: " + premier.getId());
            System.out.println("      - Type: " + premier.getType());
            System.out.println("      - Montant: " + premier.getMontant() + "€");
            System.out.println("      - Statut: " + premier.getStatut());
        }

        // Test statistiques
        System.out.println("\n2️⃣ Test STATISTIQUES");
        int actifs = service.getAbonnementsActifs().size();
        double revenu = service.getRevenuTotal();
        System.out.println("   📊 Abonnements actifs: " + actifs);
        System.out.println("   💰 Revenu total: " + String.format("%.2f€", revenu));

        // Test CREATE (optionnel - décommenter si vous voulez tester)
        /*
        System.out.println("\n3️⃣ Test CREATE - Ajouter un abonnement de test");
        Abonnement test = new Abonnement();
        test.setUtilisateurId(1);
        test.setType("MENSUEL");
        test.setMontant(19.99);
        test.setDateDebut(LocalDateTime.now());
        test.setDateExpiration(LocalDateTime.now().plusMonths(1));
        test.setStatut("ACTIF");

        if (service.ajouterAbonnement(test)) {
            System.out.println("   ✅ Abonnement de test ajouté");
        }
        */
    }

    private static void testPaiementService() {
        System.out.println("💳 TEST SERVICE PAIEMENT");
        System.out.println("-".repeat(50));

        PaiementService service = new PaiementService();

        // Test READ
        System.out.println("\n1️⃣ Test READ - Récupérer tous les paiements");
        List<Paiement> paiements = service.getAllPaiements();
        System.out.println("   ✅ " + paiements.size() + " paiement(s) trouvé(s)");

        if (!paiements.isEmpty()) {
            Paiement premier = paiements.get(0);
            System.out.println("   📄 Premier paiement:");
            System.out.println("      - ID: " + premier.getId());
            System.out.println("      - Montant: " + premier.getMontant() + "€");
            System.out.println("      - Transaction: " + premier.getTransactionId());
            System.out.println("      - Statut: " + premier.getStatut());
        }

        // Test statistiques
        System.out.println("\n2️⃣ Test STATISTIQUES");
        int valides = service.countByStatut("VALIDE");
        double encaisse = service.getTotalEncaisse();
        System.out.println("   📊 Paiements validés: " + valides);
        System.out.println("   💰 Total encaissé: " + String.format("%.2f€", encaisse));
    }
}
