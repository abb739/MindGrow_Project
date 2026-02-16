package utils;

import entities.Utilisateur;

/**
 * Gestionnaire de session pour l'utilisateur connecté
 */
public class SessionManager {

    private static Utilisateur utilisateurConnecte;

    // Variables pour stocker l'abonnement sélectionné
    private static String selectedSubscriptionType;
    private static double selectedSubscriptionAmount;

    /**
     * Connecter un utilisateur
     */
    public static void login(Utilisateur user) {
        utilisateurConnecte = user;
        System.out.println("👤 Utilisateur connecté: " + user.getPrenom() + " " + user.getNom());
        System.out.println("🔑 Rôles: " + user.getRoles());
    }

    /**
     * Déconnecter l'utilisateur
     */
    public static void logout() {
        if (utilisateurConnecte != null) {
            System.out.println("👋 Déconnexion de: " + utilisateurConnecte.getPrenom());
        }
        utilisateurConnecte = null;
        // Réinitialiser les données d'abonnement
        clearSelectedSubscription();
    }

    /**
     * Récupérer l'utilisateur connecté
     */
    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    public static boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    /**
     * Vérifier si l'utilisateur connecté est un admin
     */
    public static boolean isAdmin() {
        if (utilisateurConnecte == null) return false;
        String roles = utilisateurConnecte.getRoles();
        return roles != null && (roles.contains("ROLE_ADMIN") || roles.contains("admin"));
    }

    /**
     * Vérifier si l'utilisateur connecté est un membre
     */
    public static boolean isMembre() {
        if (utilisateurConnecte == null) return false;
        String roles = utilisateurConnecte.getRoles();
        return roles != null && (roles.contains("ROLE_MEMBRE") || roles.contains("membre"));
    }

    /**
     * Vérifier si l'utilisateur connecté est un coach
     */
    public static boolean isCoach() {
        if (utilisateurConnecte == null) return false;
        String roles = utilisateurConnecte.getRoles();
        return roles != null && (roles.contains("ROLE_COACH") || roles.contains("coach"));
    }

    /**
     * Obtenir l'ID de l'utilisateur connecté
     */
    public static int getUserId() {
        return utilisateurConnecte != null ? utilisateurConnecte.getId() : -1;
    }

    /**
     * Obtenir le nom complet de l'utilisateur
     */
    public static String getNomComplet() {
        if (utilisateurConnecte == null) return "Invité";
        return utilisateurConnecte.getPrenom() + " " + utilisateurConnecte.getNom();
    }

    /**
     * Définir le type d'abonnement sélectionné
     */
    public static void setSelectedSubscriptionType(String type) {
        selectedSubscriptionType = type;
        System.out.println("📌 Abonnement sélectionné: " + type);
    }

    /**
     * Définir le montant de l'abonnement sélectionné
     */
    public static void setSelectedSubscriptionAmount(double amount) {
        selectedSubscriptionAmount = amount;
        System.out.println("💰 Montant: " + amount + " €");
    }

    /**
     * Récupérer le type d'abonnement sélectionné
     */
    public static String getSelectedSubscriptionType() {
        return selectedSubscriptionType;
    }

    /**
     * Récupérer le montant de l'abonnement sélectionné
     */
    public static double getSelectedSubscriptionAmount() {
        return selectedSubscriptionAmount;
    }

    /**
     * Vérifier si un abonnement a été sélectionné
     */
    public static boolean hasSelectedSubscription() {
        return selectedSubscriptionType != null && selectedSubscriptionAmount > 0;
    }

    /**
     * Effacer l'abonnement sélectionné
     */
    public static void clearSelectedSubscription() {
        selectedSubscriptionType = null;
        selectedSubscriptionAmount = 0.0;
        System.out.println("🧹 Abonnement sélectionné effacé");
    }
}