package controllers;

import entities.Paiement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.PaiementService;

import java.time.LocalDateTime;

/**
 * Controller pour la gestion des paiements
 */
public class PaiementController {

    // ========== Champs du formulaire ==========
    @FXML private TextField abonnementIdField;
    @FXML private TextField montantField;
    @FXML private ComboBox<String> modePaiementCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField factureUrlField;
    @FXML private Label transactionLabel;

    // ========== TableView et colonnes ==========
    @FXML private TableView<Paiement> tableView;
    @FXML private TableColumn<Paiement, Integer> colId;
    @FXML private TableColumn<Paiement, String> colUtilisateur;
    @FXML private TableColumn<Paiement, String> colTypeAbo;
    @FXML private TableColumn<Paiement, Double> colMontant;
    @FXML private TableColumn<Paiement, String> colDate;
    @FXML private TableColumn<Paiement, String> colMode;
    @FXML private TableColumn<Paiement, String> colTransaction;
    @FXML private TableColumn<Paiement, String> colStatut;

    // ========== Labels de statistiques ==========
    @FXML private Label totalLabel;
    @FXML private Label validesLabel;
    @FXML private Label encaisseLabel;

    // ========== Service et données ==========
    private final PaiementService service = new PaiementService();
    private final ObservableList<Paiement> paiements = FXCollections.observableArrayList();

    /**
     * Initialisation du controller
     */
    @FXML
    public void initialize() {
        System.out.println("🎬 Initialisation du controller Paiement...");

        // Initialiser les ComboBox
        initializeComboBoxes();

        // Configurer les colonnes
        configureTableColumns();

        // Lier la TableView aux données
        tableView.setItems(paiements);

        // Charger les données
        rafraichir();

        // Gérer la sélection
        setupTableSelection();

        System.out.println("✅ Controller Paiement initialisé avec succès!");
    }

    /**
     * Initialiser les ComboBox
     */
    private void initializeComboBoxes() {
        // Modes de paiement
        modePaiementCombo.setItems(FXCollections.observableArrayList(
                "CARTE_BANCAIRE",
                "PAYPAL",
                "VIREMENT",
                "CHEQUE",
                "ESPECES"
        ));

        // Statuts possibles
        statutCombo.setItems(FXCollections.observableArrayList(
                "EN_ATTENTE",
                "VALIDE",
                "ECHOUE",
                "REMBOURSE"
        ));
    }

    /**
     * Configurer les colonnes de la TableView
     */
    private void configureTableColumns() {
        // Colonne ID
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Utilisateur
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));

        // Colonne Type abonnement
        colTypeAbo.setCellValueFactory(new PropertyValueFactory<>("typeAbonnement"));

        // Colonne Montant (formaté)
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

        // Colonne Date (formatée)
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDatePaiementFormatted()
                )
        );

        // Colonne Mode de paiement
        colMode.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));

        // Colonne Transaction ID
        colTransaction.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        // Colonne Statut (avec couleur)
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
                        case "REMBOURSE":
                            setStyle("-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Gérer la sélection dans la table
     */
    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirFormulaire(newSelection);
            }
        });
    }

    /**
     * AJOUTER un paiement
     */
    @FXML
    private void ajouterPaiement() {
        System.out.println("➕ Tentative d'ajout d'un paiement...");

        // Valider les champs
        if (!validerChamps()) {
            return;
        }

        try {
            // Créer un nouveau paiement
            Paiement paiement = new Paiement();
            paiement.setAbonnementId(Integer.parseInt(abonnementIdField.getText().trim()));
            paiement.setMontant(Double.parseDouble(montantField.getText().trim()));
            paiement.setDatePaiement(LocalDateTime.now());
            paiement.setModePaiement(modePaiementCombo.getValue());
            paiement.setStatut(statutCombo.getValue());
            paiement.setFactureUrl(factureUrlField.getText().trim());

            // Le Transaction ID sera auto-généré dans le service

            // Appeler le service
            if (service.ajouterPaiement(paiement)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Paiement enregistré!",
                        "Le paiement a été enregistré avec l'ID de transaction: " + paiement.getTransactionId());
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible d'ajouter le paiement",
                        "Une erreur est survenue lors de l'enregistrement.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de format",
                    "Données invalides",
                    "Veuillez vérifier que l'ID abonnement et le montant sont des nombres valides.");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur inattendue",
                    "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * MODIFIER un paiement
     */
    @FXML
    private void modifierPaiement() {
        System.out.println("✏️ Tentative de modification d'un paiement...");

        // Vérifier qu'un paiement est sélectionné
        Paiement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection",
                    "Veuillez sélectionner un paiement à modifier.");
            return;
        }

        // Valider les champs
        if (!validerChamps()) {
            return;
        }

        try {
            // Mettre à jour le paiement sélectionné
            selected.setAbonnementId(Integer.parseInt(abonnementIdField.getText().trim()));
            selected.setMontant(Double.parseDouble(montantField.getText().trim()));
            selected.setModePaiement(modePaiementCombo.getValue());
            selected.setStatut(statutCombo.getValue());
            selected.setFactureUrl(factureUrlField.getText().trim());

            // Appeler le service
            if (service.modifierPaiement(selected)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Paiement modifié!",
                        "Les modifications ont été enregistrées.");
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de modifier le paiement",
                        "Une erreur est survenue lors de la modification.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de format",
                    "Données invalides",
                    "Veuillez vérifier que les données saisies sont correctes.");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur inattendue",
                    "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * SUPPRIMER un paiement
     */
    @FXML
    private void supprimerPaiement() {
        System.out.println("🗑️ Tentative de suppression d'un paiement...");

        // Vérifier qu'un paiement est sélectionné
        Paiement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection",
                    "Veuillez sélectionner un paiement à supprimer.");
            return;
        }

        // Demander confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le paiement #" + selected.getId() + " ?");
        confirmation.setContentText("Transaction ID: " + selected.getTransactionId() + "\nCette action est irréversible.");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (service.supprimerPaiement(selected.getId())) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Paiement supprimé!",
                        "Le paiement a été supprimé de la base de données.");
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de supprimer le paiement",
                        "Une erreur est survenue lors de la suppression.");
            }
        }
    }

    /**
     * RAFRAÎCHIR les données
     */
    @FXML
    public void rafraichir() {
        System.out.println("🔄 Rafraîchissement des données...");

        // Vider la liste actuelle
        paiements.clear();

        // Récupérer les nouvelles données
        paiements.addAll(service.getAllPaiements());

        // Mettre à jour les statistiques
        mettreAJourStatistiques();

        System.out.println("✅ Données rafraîchies: " + paiements.size() + " paiement(s)");
    }

    /**
     * VIDER le formulaire
     */
    @FXML
    public void viderFormulaire() {
        abonnementIdField.clear();
        montantField.clear();
        modePaiementCombo.setValue(null);
        statutCombo.setValue(null);
        factureUrlField.clear();
        transactionLabel.setText("🔑 Transaction ID: [Auto-généré]");

        // Désélectionner dans la table
        tableView.getSelectionModel().clearSelection();

        System.out.println("🧹 Formulaire vidé");
    }

    /**
     * Remplir le formulaire avec un paiement
     */
    private void remplirFormulaire(Paiement paiement) {
        abonnementIdField.setText(String.valueOf(paiement.getAbonnementId()));
        montantField.setText(String.valueOf(paiement.getMontant()));
        modePaiementCombo.setValue(paiement.getModePaiement());
        statutCombo.setValue(paiement.getStatut());
        factureUrlField.setText(paiement.getFactureUrl());
        transactionLabel.setText("🔑 Transaction ID: " + paiement.getTransactionId());

        System.out.println("📝 Formulaire rempli avec le paiement #" + paiement.getId());
    }

    /**
     * Valider les champs du formulaire
     */
    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();

        // Vérifier l'ID abonnement
        if (abonnementIdField.getText().trim().isEmpty()) {
            erreurs.append("• L'ID abonnement est obligatoire\n");
        } else {
            try {
                int id = Integer.parseInt(abonnementIdField.getText().trim());
                if (id <= 0) {
                    erreurs.append("• L'ID abonnement doit être supérieur à 0\n");
                }
            } catch (NumberFormatException e) {
                erreurs.append("• L'ID abonnement doit être un nombre entier\n");
            }
        }

        // Vérifier le montant
        if (montantField.getText().trim().isEmpty()) {
            erreurs.append("• Le montant est obligatoire\n");
        } else {
            try {
                double montant = Double.parseDouble(montantField.getText().trim());
                if (montant <= 0) {
                    erreurs.append("• Le montant doit être supérieur à 0\n");
                }
            } catch (NumberFormatException e) {
                erreurs.append("• Le montant doit être un nombre décimal (ex: 29.99)\n");
            }
        }

        // Vérifier le mode de paiement
        if (modePaiementCombo.getValue() == null) {
            erreurs.append("• Le mode de paiement est obligatoire\n");
        }

        // Vérifier le statut
        if (statutCombo.getValue() == null) {
            erreurs.append("• Le statut est obligatoire\n");
        }

        // Afficher les erreurs s'il y en a
        if (erreurs.length() > 0) {
            afficherAlerte(Alert.AlertType.WARNING, "Validation",
                    "Champs invalides ou manquants",
                    erreurs.toString());
            return false;
        }

        return true;
    }

    /**
     * Mettre à jour les statistiques
     */
    private void mettreAJourStatistiques() {
        // Nombre total de paiements
        int total = paiements.size();
        totalLabel.setText(String.valueOf(total));

        // Nombre de paiements validés
        int valides = service.countByStatut("VALIDE");
        validesLabel.setText(String.valueOf(valides));

        // Total encaissé
        double encaisse = service.getTotalEncaisse();
        encaisseLabel.setText(String.format("%.2f €", encaisse));

        System.out.println("📊 Statistiques mises à jour: " + total + " total, " + valides + " validés, " + encaisse + "€");
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