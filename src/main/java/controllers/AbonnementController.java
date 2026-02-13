package controllers;

import entities.Abonnement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.AbonnementService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller pour la gestion des abonnements
 */
public class AbonnementController {

    // ========== Champs du formulaire ==========
    @FXML private TextField utilisateurIdField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField montantField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateExpirationPicker;
    @FXML private ComboBox<String> statutCombo;

    // ========== TableView et colonnes ==========
    @FXML private TableView<Abonnement> tableView;
    @FXML private TableColumn<Abonnement, Integer> colId;
    @FXML private TableColumn<Abonnement, String> colUtilisateur;
    @FXML private TableColumn<Abonnement, String> colEmail;
    @FXML private TableColumn<Abonnement, String> colType;
    @FXML private TableColumn<Abonnement, Double> colMontant;
    @FXML private TableColumn<Abonnement, String> colDateDebut;
    @FXML private TableColumn<Abonnement, String> colDateExpiration;
    @FXML private TableColumn<Abonnement, String> colStatut;

    // ========== Labels de statistiques ==========
    @FXML private Label totalLabel;
    @FXML private Label actifsLabel;
    @FXML private Label revenuLabel;

    // ========== Service et données ==========
    private final AbonnementService service = new AbonnementService();
    private final ObservableList<Abonnement> abonnements = FXCollections.observableArrayList();

    /**
     * Initialisation du controller
     * Appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        System.out.println("🎬 Initialisation du controller Abonnement...");

        // Initialiser les ComboBox avec les valeurs possibles
        initializeComboBoxes();

        // Configurer les colonnes de la TableView
        configureTableColumns();

        // Lier la TableView aux données
        tableView.setItems(abonnements);

        // Charger les données initiales
        rafraichir();

        // Gérer la sélection dans la table
        setupTableSelection();

        System.out.println("✅ Controller Abonnement initialisé avec succès!");
    }

    /**
     * Initialiser les ComboBox
     */
    private void initializeComboBoxes() {
        // Types d'abonnement
        typeCombo.setItems(FXCollections.observableArrayList(
                "MENSUEL",
                "TRIMESTRIEL",
                "ANNUEL"
        ));

        // Statuts possibles
        statutCombo.setItems(FXCollections.observableArrayList(
                "ACTIF",
                "EXPIRE",
                "SUSPENDU",
                "ANNULE"
        ));
    }

    /**
     * Configurer les colonnes de la TableView
     */
    private void configureTableColumns() {
        // Colonne ID
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Utilisateur (nom + prénom)
        colUtilisateur.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getNomUtilisateur();
            String prenom = cellData.getValue().getPrenomUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(nom + " " + prenom);
        });

        // Colonne Email
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailUtilisateur"));

        // Colonne Type
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Colonne Montant (formaté avec 2 décimales)
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colMontant.setCellFactory(column -> new TableCell<Abonnement, Double>() {
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

        // Colonne Date début (formatée)
        colDateDebut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateDebutFormatted()
                )
        );

        // Colonne Date expiration (formatée)
        colDateExpiration.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateExpirationFormatted()
                )
        );

        // Colonne Statut (avec couleur)
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Abonnement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Appliquer des couleurs selon le statut
                    switch (item) {
                        case "ACTIF":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                            break;
                        case "EXPIRE":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                            break;
                        case "SUSPENDU":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                            break;
                        case "ANNULE":
                            setStyle("-fx-background-color: #e2e3e5; -fx-text-fill: #383d41; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Gérer la sélection d'une ligne dans la table
     */
    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirFormulaire(newSelection);
            }
        });
    }

    /**
     * AJOUTER un abonnement
     */
    @FXML
    private void ajouterAbonnement() {
        System.out.println("➕ Tentative d'ajout d'un abonnement...");

        // Valider les champs
        if (!validerChamps()) {
            return;
        }

        try {
            // Créer un nouvel abonnement
            Abonnement abonnement = new Abonnement();
            abonnement.setUtilisateurId(Integer.parseInt(utilisateurIdField.getText().trim()));
            abonnement.setType(typeCombo.getValue());
            abonnement.setMontant(Double.parseDouble(montantField.getText().trim()));

            // Convertir les dates
            LocalDate dateDebut = dateDebutPicker.getValue();
            abonnement.setDateDebut(LocalDateTime.of(dateDebut, LocalTime.now()));

            LocalDate dateExpiration = dateExpirationPicker.getValue();
            abonnement.setDateExpiration(LocalDateTime.of(dateExpiration, LocalTime.now()));

            abonnement.setStatut(statutCombo.getValue());

            // Appeler le service
            if (service.ajouterAbonnement(abonnement)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement ajouté avec succès!",
                        "L'abonnement a été enregistré dans la base de données.");
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible d'ajouter l'abonnement",
                        "Une erreur est survenue lors de l'enregistrement.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de format",
                    "Données invalides",
                    "Veuillez vérifier que l'ID utilisateur et le montant sont des nombres valides.");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur inattendue",
                    "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * MODIFIER un abonnement
     */
    @FXML
    private void modifierAbonnement() {
        System.out.println("✏️ Tentative de modification d'un abonnement...");

        // Vérifier qu'un abonnement est sélectionné
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection",
                    "Veuillez sélectionner un abonnement à modifier.");
            return;
        }

        // Valider les champs
        if (!validerChamps()) {
            return;
        }

        try {
            // Mettre à jour l'abonnement sélectionné
            selected.setUtilisateurId(Integer.parseInt(utilisateurIdField.getText().trim()));
            selected.setType(typeCombo.getValue());
            selected.setMontant(Double.parseDouble(montantField.getText().trim()));

            LocalDate dateDebut = dateDebutPicker.getValue();
            selected.setDateDebut(LocalDateTime.of(dateDebut, LocalTime.now()));

            LocalDate dateExpiration = dateExpirationPicker.getValue();
            selected.setDateExpiration(LocalDateTime.of(dateExpiration, LocalTime.now()));

            selected.setStatut(statutCombo.getValue());

            // Appeler le service
            if (service.modifierAbonnement(selected)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement modifié avec succès!",
                        "Les modifications ont été enregistrées.");
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de modifier l'abonnement",
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
     * SUPPRIMER un abonnement
     */
    @FXML
    private void supprimerAbonnement() {
        System.out.println("🗑️ Tentative de suppression d'un abonnement...");

        // Vérifier qu'un abonnement est sélectionné
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection",
                    "Veuillez sélectionner un abonnement à supprimer.");
            return;
        }

        // Demander confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'abonnement #" + selected.getId() + " ?");
        confirmation.setContentText("Cette action est irréversible. Voulez-vous continuer ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (service.supprimerAbonnement(selected.getId())) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement supprimé!",
                        "L'abonnement a été supprimé de la base de données.");
                rafraichir();
                viderFormulaire();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible de supprimer l'abonnement",
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
        abonnements.clear();

        // Récupérer les nouvelles données
        abonnements.addAll(service.getAllAbonnements());

        // Mettre à jour les statistiques
        mettreAJourStatistiques();

        System.out.println("✅ Données rafraîchies: " + abonnements.size() + " abonnement(s)");
    }

    /**
     * VIDER le formulaire
     */
    @FXML
    public void viderFormulaire() {
        utilisateurIdField.clear();
        typeCombo.setValue(null);
        montantField.clear();
        dateDebutPicker.setValue(null);
        dateExpirationPicker.setValue(null);
        statutCombo.setValue(null);

        // Désélectionner dans la table
        tableView.getSelectionModel().clearSelection();

        System.out.println("🧹 Formulaire vidé");
    }

    /**
     * Remplir le formulaire avec un abonnement
     */
    private void remplirFormulaire(Abonnement abonnement) {
        utilisateurIdField.setText(String.valueOf(abonnement.getUtilisateurId()));
        typeCombo.setValue(abonnement.getType());
        montantField.setText(String.valueOf(abonnement.getMontant()));
        dateDebutPicker.setValue(abonnement.getDateDebut().toLocalDate());
        dateExpirationPicker.setValue(abonnement.getDateExpiration().toLocalDate());
        statutCombo.setValue(abonnement.getStatut());

        System.out.println("📝 Formulaire rempli avec l'abonnement #" + abonnement.getId());
    }

    /**
     * Valider les champs du formulaire
     */
    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();

        // Vérifier l'ID utilisateur
        if (utilisateurIdField.getText().trim().isEmpty()) {
            erreurs.append("• L'ID utilisateur est obligatoire\n");
        } else {
            try {
                int id = Integer.parseInt(utilisateurIdField.getText().trim());
                if (id <= 0) {
                    erreurs.append("• L'ID utilisateur doit être supérieur à 0\n");
                }
            } catch (NumberFormatException e) {
                erreurs.append("• L'ID utilisateur doit être un nombre entier\n");
            }
        }

        // Vérifier le type
        if (typeCombo.getValue() == null) {
            erreurs.append("• Le type d'abonnement est obligatoire\n");
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

        // Vérifier les dates
        if (dateDebutPicker.getValue() == null) {
            erreurs.append("• La date de début est obligatoire\n");
        }
        if (dateExpirationPicker.getValue() == null) {
            erreurs.append("• La date d'expiration est obligatoire\n");
        }

        // Vérifier que la date d'expiration est après la date de début
        if (dateDebutPicker.getValue() != null && dateExpirationPicker.getValue() != null) {
            if (dateExpirationPicker.getValue().isBefore(dateDebutPicker.getValue())) {
                erreurs.append("• La date d'expiration doit être après la date de début\n");
            }
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
     * Mettre à jour les statistiques affichées
     */
    private void mettreAJourStatistiques() {
        // Nombre total d'abonnements
        int total = abonnements.size();
        totalLabel.setText(String.valueOf(total));

        // Nombre d'abonnements actifs
        int actifs = service.getAbonnementsActifs().size();
        actifsLabel.setText(String.valueOf(actifs));

        // Revenu total
        double revenu = service.getRevenuTotal();
        revenuLabel.setText(String.format("%.2f €", revenu));

        System.out.println("📊 Statistiques mises à jour: " + total + " total, " + actifs + " actifs, " + revenu + "€");
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