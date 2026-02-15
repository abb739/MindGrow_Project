package controllers.user;

import entities.Paiement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.PaiementService;
import services.AbonnementService;
import utils.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Controller pour la gestion des paiements côté UTILISATEUR
 */
public class PaiementUserController {

    // ========== Labels d'information ==========
    @FXML private Label bienvenuLabel;
    @FXML private Label totalPayeLabel;
    @FXML private Label nombrePaiementsLabel;

    // ========== Labels pour l'abonnement sélectionné ==========
    @FXML private Label selectedTypeLabel;
    @FXML private Label selectedMontantLabel;

    // ========== Champs de paiement ==========
    @FXML private ComboBox<String> modePaiementCombo;
    @FXML private TextField numeroCarteField;
    @FXML private TextField nomCarteField;
    @FXML private TextField cvvField;
    @FXML private TextField expirationField;

    // ========== Boutons ==========
    @FXML private Button btnValiderPaiement;
    @FXML private Button btnRetourAbonnements;

    // ========== TableView ==========
    @FXML private TableView<Paiement> tableView;
    @FXML private TableColumn<Paiement, Integer> colId;
    @FXML private TableColumn<Paiement, String> colTypeAbo;
    @FXML private TableColumn<Paiement, Double> colMontant;
    @FXML private TableColumn<Paiement, String> colDate;
    @FXML private TableColumn<Paiement, String> colMode;
    @FXML private TableColumn<Paiement, String> colTransaction;
    @FXML private TableColumn<Paiement, String> colStatut;

    // ========== Services et données ==========
    private final PaiementService paiementService = new PaiementService();
    private final AbonnementService abonnementService = new AbonnementService();
    private final ObservableList<Paiement> mesPaiements = FXCollections.observableArrayList();

    /**
     * Initialisation
     */
    @FXML
    public void initialize() {
        System.out.println("🎬 Initialisation du controller Paiement User...");

        // Vérifier connexion
        if (!SessionManager.isConnecte()) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Non connecté",
                    "Vous devez être connecté.");
            return;
        }

        // Afficher le nom
        if (bienvenuLabel != null) {
            bienvenuLabel.setText("Paiement de " + SessionManager.getNomComplet());
        }

        // Initialiser le ComboBox des modes de paiement
        if (modePaiementCombo != null) {
            modePaiementCombo.setItems(FXCollections.observableArrayList(
                    "CARTE_BANCAIRE", "PAYPAL", "VIREMENT", "ESPECES"
            ));
            modePaiementCombo.setValue("CARTE_BANCAIRE");
        }

        // Afficher l'abonnement sélectionné
        afficherAbonnementSelectionne();

        // Configurer les colonnes si le tableau existe
        if (tableView != null) {
            configureTableColumns();
            tableView.setItems(mesPaiements);
            chargerMesPaiements();
        }

        System.out.println("✅ Controller Paiement User initialisé!");
    }

    /**
     * Afficher l'abonnement sélectionné
     */
    private void afficherAbonnementSelectionne() {
        if (SessionManager.hasSelectedSubscription()) {
            String type = SessionManager.getSelectedSubscriptionType();
            double montant = SessionManager.getSelectedSubscriptionAmount();

            if (selectedTypeLabel != null) {
                selectedTypeLabel.setText(type);
            }
            if (selectedMontantLabel != null) {
                selectedMontantLabel.setText(String.format("%.2f €", montant));
            }

            System.out.println("✅ Abonnement sélectionné affiché: " + type + " - " + montant + " €");
        } else {
            System.out.println("⚠️ Aucun abonnement sélectionné");
            if (selectedTypeLabel != null) {
                selectedTypeLabel.setText("Aucun");
            }
            if (selectedMontantLabel != null) {
                selectedMontantLabel.setText("0.00 €");
            }
        }
    }

    /**
     * Valider le paiement
     */
    @FXML
    private void validerPaiement() {
        System.out.println("💳 Validation du paiement...");

        // Vérifier qu'un abonnement est sélectionné
        if (!SessionManager.hasSelectedSubscription()) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucun abonnement sélectionné",
                    "Veuillez d'abord sélectionner un abonnement.");
            return;
        }

        // Valider le mode de paiement
        if (modePaiementCombo == null || modePaiementCombo.getValue() == null || modePaiementCombo.getValue().isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Mode de paiement requis",
                    "Veuillez sélectionner un mode de paiement.");
            return;
        }

        // Validation des champs pour carte bancaire
        if ("CARTE_BANCAIRE".equals(modePaiementCombo.getValue())) {
            if (numeroCarteField == null || numeroCarteField.getText().trim().isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Attention",
                        "Numéro de carte requis",
                        "Veuillez entrer votre numéro de carte.");
                return;
            }
            if (nomCarteField == null || nomCarteField.getText().trim().isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Attention",
                        "Nom sur la carte requis",
                        "Veuillez entrer le nom sur la carte.");
                return;
            }
            if (cvvField == null || cvvField.getText().trim().isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Attention",
                        "CVV requis",
                        "Veuillez entrer le code CVV.");
                return;
            }
            if (expirationField == null || expirationField.getText().trim().isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Attention",
                        "Date d'expiration requise",
                        "Veuillez entrer la date d'expiration (MM/YY).");
                return;
            }
        }

        // Récupérer les données
        String type = SessionManager.getSelectedSubscriptionType();
        double montant = SessionManager.getSelectedSubscriptionAmount();
        int userId = SessionManager.getUserId();
        String modePaiement = modePaiementCombo.getValue();

        try {
            // 1. Mettre à jour l'abonnement
            boolean abonnementMisAJour = abonnementService.upgraderAbonnement(userId, type, montant);

            if (abonnementMisAJour) {
                // 2. Récupérer l'ID de l'abonnement mis à jour
                var abonnement = abonnementService.getAbonnementByUserId(userId);

                if (abonnement != null) {
                    // 3. Créer le paiement
                    Paiement paiement = new Paiement();
                    paiement.setAbonnementId(abonnement.getId());
                    paiement.setMontant(montant);
                    paiement.setDatePaiement(LocalDateTime.now());
                    paiement.setModePaiement(modePaiement);
                    paiement.setStatut("VALIDE");
                    // Le transaction_id sera généré automatiquement par PaiementService

                    boolean paiementCree = paiementService.ajouterPaiement(paiement);

                    if (paiementCree) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                                "Paiement validé!",
                                "Votre paiement a été traité avec succès.\n" +
                                        "Votre abonnement " + type + " est maintenant actif!\n" +
                                        "Transaction ID: " + paiement.getTransactionId());

                        // Effacer l'abonnement sélectionné
                        SessionManager.clearSelectedSubscription();

                        // Retourner à la page des abonnements
                        retourAbonnements();
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                                "Erreur d'enregistrement",
                                "Le paiement n'a pas pu être enregistré.");
                    }
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                            "Erreur de récupération",
                            "Impossible de récupérer l'abonnement.");
                }
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de l'activation",
                        "L'abonnement n'a pas pu être activé.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du paiement: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur système",
                    "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }

    /**
     * Retourner à la page des abonnements
     */
    @FXML
    private void retourAbonnements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/abonnement_user_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourAbonnements.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("MindGrow - Mes Abonnements");
            stage.show();

            System.out.println("✅ Retour à la page des abonnements");

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur de navigation",
                    "Impossible de retourner à la page des abonnements.");
        }
    }

    /**
     * Configurer les colonnes de la table
     */
    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTypeAbo.setCellValueFactory(new PropertyValueFactory<>("typeAbonnement"));

        // Montant formaté
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colMontant.setCellFactory(column -> new TableCell<Paiement, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        // Date formatée
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDatePaiementFormatted()
                )
        );

        colMode.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
        colTransaction.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        // Statut avec couleur
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Paiement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "VALIDE":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                            break;
                        case "EN_ATTENTE":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                            break;
                        case "ECHOUE":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Charger les paiements de l'utilisateur connecté
     */
    @FXML
    public void chargerMesPaiements() {
        System.out.println("🔄 Chargement des paiements...");

        mesPaiements.clear();

        int userId = SessionManager.getUserId();
        mesPaiements.addAll(paiementService.getPaiementsByUserId(userId));

        // Mettre à jour les statistiques
        mettreAJourStatistiques();

        System.out.println("✅ " + mesPaiements.size() + " paiement(s) chargé(s)");
    }

    /**
     * Mettre à jour les statistiques
     */
    private void mettreAJourStatistiques() {
        int userId = SessionManager.getUserId();

        // Nombre de paiements
        if (nombrePaiementsLabel != null) {
            nombrePaiementsLabel.setText(String.valueOf(mesPaiements.size()));
        }

        // Total payé
        double total = paiementService.getTotalPayeByUserId(userId);
        if (totalPayeLabel != null) {
            totalPayeLabel.setText(String.format("%.2f €", total));
        }
    }

    /**
     * Télécharger une facture (simulation)
     */
    @FXML
    private void telechargerFacture() {
        Paiement selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection",
                    "Veuillez sélectionner un paiement pour télécharger la facture.");
            return;
        }

        // Simulation du téléchargement
        String factureUrl = selected.getFactureUrl();
        if (factureUrl != null && !factureUrl.isEmpty()) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Téléchargement",
                    "Facture disponible",
                    "URL: " + factureUrl + "\n\n(Fonctionnalité de téléchargement à implémenter)");
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Facture non disponible",
                    "Aucune facture n'est associée à ce paiement.");
        }
    }

    /**
     * Afficher une alerte
     */
    private void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}