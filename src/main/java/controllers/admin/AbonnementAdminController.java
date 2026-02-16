package controllers.admin;

import entities.Abonnement;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.AbonnementService;
import services.UtilisateurService;
import utils.ExcelExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller pour la gestion des abonnements côté admin
 * MODIFIÉ : Ajout de la colonne "Statut Paiement" et fonction d'export Excel
 */
public class AbonnementAdminController {

    // ========== Champs du formulaire ==========
    @FXML private ComboBox<String> utilisateurCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField montantField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private ComboBox<String> statutCombo;

    // ========== Nouveaux champs de recherche et filtre ==========
    @FXML private TextField rechercheField;
    @FXML private ComboBox<String> filtreTypeCombo;
    @FXML private ComboBox<String> filtreStatutCombo;
    @FXML private Label tauxConversionLabel;

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
    @FXML private TableColumn<Abonnement, String> colStatutPaiement;

    // ========== Labels de statistiques ==========
    @FXML private Label totalLabel;
    @FXML private Label actifsLabel;
    @FXML private Label revenuLabel;

    // ========== Services et données ==========
    private final AbonnementService abonnementService = new AbonnementService();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<Abonnement> abonnements = FXCollections.observableArrayList();
    private List<Utilisateur> tousLesUtilisateurs;

    /**
     * Initialisation du controller
     */
    @FXML
    public void initialize() {
        System.out.println("🎬 Initialisation du controller Abonnement Admin...");

        // Charger les utilisateurs
        chargerUtilisateurs();

        // Initialiser les ComboBox
        initializeComboBoxes();

        // Configurer les colonnes de la TableView
        configureTableColumns();

        // Lier la TableView aux données
        tableView.setItems(abonnements);

        // Charger les données initiales
        rafraichir();

        // Gérer la sélection dans la table
        setupTableSelection();

        // Initialiser les filtres
        initialiserFiltres();

        // Calculer le taux de conversion
        calculerTauxConversion();

        System.out.println("✅ Controller Abonnement Admin initialisé avec succès!");
    }

    /**
     * Charger la liste des utilisateurs dans le ComboBox
     */
    private void chargerUtilisateurs() {
        tousLesUtilisateurs = utilisateurService.getAllUtilisateurs();
        ObservableList<String> utilisateursDisplay = FXCollections.observableArrayList();

        for (Utilisateur user : tousLesUtilisateurs) {
            String display = String.format("ID: %d - %s %s (%s)",
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail());
            utilisateursDisplay.add(display);
        }

        utilisateurCombo.setItems(utilisateursDisplay);
        System.out.println("✅ " + tousLesUtilisateurs.size() + " utilisateurs chargés");
    }

    /**
     * Obtenir l'ID de l'utilisateur sélectionné dans le ComboBox
     */
    private Integer getSelectedUserId() {
        String selected = utilisateurCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            return null;
        }

        try {
            String idPart = selected.substring(selected.indexOf("ID: ") + 4, selected.indexOf(" - "));
            return Integer.parseInt(idPart.trim());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction de l'ID utilisateur");
            return null;
        }
    }

    /**
     * Initialiser les ComboBox
     */
    private void initializeComboBoxes() {
        typeCombo.setItems(FXCollections.observableArrayList(
                "MENSUEL",
                "TRIMESTRIEL",
                "ANNUEL"
        ));

        statutCombo.setItems(FXCollections.observableArrayList(
                "ACTIF",
                "EXPIRE",
                "SUSPENDU",
                "ANNULE"
        ));

        if (filtreTypeCombo != null) {
            filtreTypeCombo.setItems(FXCollections.observableArrayList(
                    "TOUS",
                    "MENSUEL",
                    "TRIMESTRIEL",
                    "ANNUEL"
            ));
            filtreTypeCombo.setValue("TOUS");
        }

        if (filtreStatutCombo != null) {
            filtreStatutCombo.setItems(FXCollections.observableArrayList(
                    "TOUS",
                    "ACTIF",
                    "EXPIRE",
                    "SUSPENDU",
                    "ANNULE"
            ));
            filtreStatutCombo.setValue("TOUS");
        }
    }

    /**
     * Configurer les colonnes de la TableView
     */
    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colUtilisateur.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getNomUtilisateur();
            String prenom = cellData.getValue().getPrenomUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(nom + " " + prenom);
        });

        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailUtilisateur"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

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

        colDateDebut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateDebutFormatted()
                )
        );

        colDateExpiration.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateExpirationFormatted()
                )
        );

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

        // Configuration de la colonne Statut Paiement
        colStatutPaiement.setCellValueFactory(new PropertyValueFactory<>("statutPaiement"));
        colStatutPaiement.setCellFactory(column -> new TableCell<Abonnement, String>() {
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
                            setStyle("-fx-background-color: #d1f2eb; -fx-text-fill: #0c5460; -fx-font-weight: bold;");
                            break;
                        case "EN_ATTENTE":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                            break;
                        case "ECHOUE":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                            break;
                        case "REMBOURSE":
                            setStyle("-fx-background-color: #d6d8db; -fx-text-fill: #1b1e21; -fx-font-weight: bold;");
                            break;
                        case "AUCUN":
                            setStyle("-fx-background-color: #e2e3e5; -fx-text-fill: #6c757d; -fx-font-style: italic;");
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
                chargerDansFormulaire(newSelection);
            }
        });
    }

    /**
     * Charger un abonnement sélectionné dans le formulaire
     */
    private void chargerDansFormulaire(Abonnement ab) {
        for (int i = 0; i < tousLesUtilisateurs.size(); i++) {
            if (tousLesUtilisateurs.get(i).getId() == ab.getUtilisateurId()) {
                utilisateurCombo.getSelectionModel().select(i);
                break;
            }
        }

        typeCombo.setValue(ab.getType());
        montantField.setText(String.valueOf(ab.getMontant()));
        dateDebutPicker.setValue(ab.getDateDebut().toLocalDate());
        statutCombo.setValue(ab.getStatut());
    }

    /**
     * Vider le formulaire
     */
    @FXML
    private void vider() {
        utilisateurCombo.getSelectionModel().clearSelection();
        typeCombo.getSelectionModel().clearSelection();
        montantField.clear();
        dateDebutPicker.setValue(null);
        statutCombo.getSelectionModel().clearSelection();
        tableView.getSelectionModel().clearSelection();
        System.out.println("🧹 Formulaire vidé");
    }

    /**
     * Ajouter un nouvel abonnement
     */
    @FXML
    private void ajouter() {
        try {
            Integer userId = getSelectedUserId();
            if (userId == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Utilisateur requis",
                        "Veuillez sélectionner un utilisateur.");
                return;
            }

            String type = typeCombo.getValue();
            String montantStr = montantField.getText();
            LocalDate dateDebut = dateDebutPicker.getValue();
            String statut = statutCombo.getValue();

            if (type == null || montantStr.isEmpty() || dateDebut == null || statut == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Champs manquants",
                        "Tous les champs marqués d'un * sont obligatoires.");
                return;
            }

            double montant = Double.parseDouble(montantStr);

            Abonnement nouvelAb = new Abonnement();
            nouvelAb.setUtilisateurId(userId);
            nouvelAb.setType(type);
            nouvelAb.setMontant(montant);
            nouvelAb.setDateDebut(LocalDateTime.of(dateDebut, LocalTime.now()));
            nouvelAb.setStatut(statut);

            if (abonnementService.ajouterAbonnement(nouvelAb)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement créé", "L'abonnement a été créé avec succès.");
                rafraichir();
                vider();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Échec de création", "Impossible de créer l'abonnement.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Montant invalide",
                    "Le montant doit être un nombre valide.");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue",
                    "Une erreur est survenue: " + e.getMessage());
        }
    }

    /**
     * Modifier un abonnement existant
     */
    @FXML
    private void modifier() {
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection", "Veuillez sélectionner un abonnement à modifier.");
            return;
        }

        try {
            Integer userId = getSelectedUserId();
            if (userId == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Utilisateur requis",
                        "Veuillez sélectionner un utilisateur.");
                return;
            }

            String type = typeCombo.getValue();
            String montantStr = montantField.getText();
            LocalDate dateDebut = dateDebutPicker.getValue();
            String statut = statutCombo.getValue();

            if (type == null || montantStr.isEmpty() || dateDebut == null || statut == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Champs manquants",
                        "Tous les champs sont obligatoires.");
                return;
            }

            double montant = Double.parseDouble(montantStr);

            selected.setUtilisateurId(userId);
            selected.setType(type);
            selected.setMontant(montant);
            selected.setDateDebut(LocalDateTime.of(dateDebut, LocalTime.now()));
            selected.setStatut(statut);

            if (abonnementService.modifierAbonnement(selected)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement modifié", "Les modifications ont été enregistrées.");
                rafraichir();
                vider();
            }

        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Montant invalide",
                    "Le montant doit être un nombre valide.");
        }
    }

    /**
     * Supprimer un abonnement
     */
    @FXML
    private void supprimer() {
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection", "Veuillez sélectionner un abonnement à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'abonnement #" + selected.getId() + " ?");
        confirmation.setContentText("Cette action est irréversible.");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (abonnementService.supprimerAbonnement(selected.getId())) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement supprimé", "L'abonnement a été supprimé avec succès.");
                rafraichir();
                vider();
            }
        }
    }

    /**
     * Rafraîchir la liste
     */
    @FXML
    private void rafraichir() {
        abonnements.clear();
        List<Abonnement> liste = abonnementService.getAllAbonnements();
        abonnements.addAll(liste);
        mettreAJourStatistiques();
        System.out.println("🔄 Liste rafraîchie: " + liste.size() + " abonnement(s)");
    }

    /**
     * Mettre à jour les statistiques
     */
    private void mettreAJourStatistiques() {
        int total = abonnements.size();
        totalLabel.setText(String.valueOf(total));

        int actifs = abonnementService.getAbonnementsActifs().size();
        actifsLabel.setText(String.valueOf(actifs));

        double revenu = abonnementService.getRevenuTotal();
        revenuLabel.setText(String.format("%.2f €", revenu));

        if (tauxConversionLabel != null && total > 0) {
            double taux = (actifs * 100.0) / total;
            tauxConversionLabel.setText(String.format("%.1f%%", taux));
        }

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

    /**
     * Initialiser les filtres de recherche
     */
    private void initialiserFiltres() {
        if (filtreTypeCombo != null) {
            filtreTypeCombo.setItems(FXCollections.observableArrayList(
                    "Tous", "MENSUEL", "TRIMESTRIEL", "ANNUEL"
            ));
        }

        if (filtreStatutCombo != null) {
            filtreStatutCombo.setItems(FXCollections.observableArrayList(
                    "Tous", "ACTIF", "EXPIRE", "SUSPENDU", "ANNULE"
            ));
        }
    }

    /**
     * Rechercher des abonnements selon les filtres
     */
    @FXML
    private void rechercherAbonnements() {
        String recherche = rechercheField != null ? rechercheField.getText().trim().toLowerCase() : "";
        String typeFiltre = filtreTypeCombo != null ? filtreTypeCombo.getValue() : null;
        String statutFiltre = filtreStatutCombo != null ? filtreStatutCombo.getValue() : null;

        List<Abonnement> resultats = abonnementService.getAllAbonnements().stream()
                .filter(ab -> {
                    boolean matchRecherche = recherche.isEmpty() ||
                            ab.getNomUtilisateur().toLowerCase().contains(recherche) ||
                            ab.getPrenomUtilisateur().toLowerCase().contains(recherche) ||
                            ab.getEmailUtilisateur().toLowerCase().contains(recherche);

                    boolean matchType = typeFiltre == null || typeFiltre.equals("Tous") ||
                            ab.getType().equals(typeFiltre);

                    boolean matchStatut = statutFiltre == null || statutFiltre.equals("Tous") ||
                            ab.getStatut().equals(statutFiltre);

                    return matchRecherche && matchType && matchStatut;
                })
                .collect(Collectors.toList());

        abonnements.clear();
        abonnements.addAll(resultats);
        mettreAJourStatistiques();
    }

    /**
     * Réinitialiser tous les filtres
     */
    @FXML
    private void reinitialiserFiltres() {
        if (rechercheField != null) {
            rechercheField.clear();
        }
        if (filtreTypeCombo != null) {
            filtreTypeCombo.setValue(null);
        }
        if (filtreStatutCombo != null) {
            filtreStatutCombo.setValue(null);
        }
        rafraichir();
    }

    /**
     * Suspendre un abonnement
     */
    @FXML
    private void suspendreAbonnement() {
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection", "Veuillez sélectionner un abonnement.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Suspendre l'abonnement");
        confirmation.setHeaderText("Suspendre l'abonnement #" + selected.getId() + " ?");
        confirmation.setContentText("Cette action suspendra l'abonnement temporairement.");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            selected.setStatut("SUSPENDU");
            if (abonnementService.modifierAbonnement(selected)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement suspendu", "L'abonnement a été suspendu avec succès.");
                rafraichir();
            }
        }
    }

    /**
     * Activer un abonnement
     */
    @FXML
    private void activerAbonnement() {
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection", "Veuillez sélectionner un abonnement.");
            return;
        }

        selected.setStatut("ACTIF");
        if (abonnementService.modifierAbonnement(selected)) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "Abonnement activé", "L'abonnement est maintenant actif.");
            rafraichir();
        }
    }

    /**
     * Prolonger un abonnement
     */
    @FXML
    private void prolongerAbonnement() {
        Abonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucune sélection", "Veuillez sélectionner un abonnement.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("30");
        dialog.setTitle("Prolonger l'abonnement");
        dialog.setHeaderText("Prolonger de combien de jours ?");
        dialog.setContentText("Nombre de jours:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(jours -> {
            try {
                int nb = Integer.parseInt(jours);
                if (nb <= 0) {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                            "Valeur invalide", "Le nombre de jours doit être positif.");
                    return;
                }

                LocalDateTime nouvelleExp = selected.getDateExpiration().plusDays(nb);
                selected.setDateExpiration(nouvelleExp);

                if (abonnementService.modifierAbonnement(selected)) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                            "Abonnement prolongé",
                            "L'abonnement a été prolongé de " + nb + " jour(s).\n" +
                                    "Nouvelle date d'expiration: " + selected.getDateExpirationFormatted());
                    rafraichir();
                }
            } catch (NumberFormatException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Valeur invalide", "Veuillez entrer un nombre entier valide.");
            }
        });
    }

    /**
     * Exporter les abonnements vers Excel
     * NOUVELLE FONCTIONNALITÉ COMPLÈTE
     */
    @FXML
    private void exporterExcel() {
        try {
            // Vérifier s'il y a des données à exporter
            if (abonnements.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Attention",
                        "Aucune donnée à exporter",
                        "Il n'y a aucun abonnement à exporter.");
                return;
            }

            // Vérifier si un abonnement est sélectionné
            Abonnement selected = tableView.getSelectionModel().getSelectedItem();

            // Créer le dialogue de sélection de fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel");

            // Définir le nom du fichier par défaut
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            if (selected != null) {
                // Si un abonnement est sélectionné, exporter uniquement celui-ci
                String nomUtilisateur = selected.getNomUtilisateur().replaceAll("[^a-zA-Z0-9]", "_");
                fileChooser.setInitialFileName("Abonnement_" + nomUtilisateur + "_" + timestamp + ".xlsx");
            } else {
                // Sinon, exporter tous les abonnements affichés
                fileChooser.setInitialFileName("Abonnements_Export_" + timestamp + ".xlsx");
            }

            // Définir le filtre d'extension
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Fichiers Excel (*.xlsx)", "*.xlsx");
            fileChooser.getExtensionFilters().add(extFilter);

            // Obtenir le répertoire initial (Documents de l'utilisateur)
            String userHome = System.getProperty("user.home");
            File initialDirectory = new File(userHome, "Documents");
            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }

            // Afficher la boîte de dialogue
            Stage stage = (Stage) tableView.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            // Si l'utilisateur a choisi un fichier
            if (file != null) {
                boolean success;

                if (selected != null) {
                    // Exporter l'abonnement sélectionné
                    success = ExcelExporter.exporterAbonnement(selected, file.getAbsolutePath());

                    if (success) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Export réussi",
                                "Abonnement exporté",
                                "L'abonnement a été exporté avec succès vers:\n" + file.getAbsolutePath());
                    }
                } else {
                    // Exporter tous les abonnements affichés
                    List<Abonnement> listeAExporter = abonnements.stream().collect(Collectors.toList());
                    success = ExcelExporter.exporterAbonnements(listeAExporter, file.getAbsolutePath());

                    if (success) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Export réussi",
                                "Abonnements exportés",
                                abonnements.size() + " abonnement(s) ont été exportés avec succès vers:\n" +
                                        file.getAbsolutePath());
                    }
                }

                if (!success) {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur d'export",
                            "Échec de l'export",
                            "Une erreur est survenue lors de l'export Excel.\n" +
                                    "Vérifiez les logs pour plus de détails.");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'export Excel!");
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur d'export",
                    "Erreur inattendue",
                    "Une erreur est survenue: " + e.getMessage());
        }
    }

    /**
     * Calculer le taux de conversion
     */
    private void calculerTauxConversion() {
        int total = abonnements.size();
        int actifs = (int) abonnements.stream()
                .filter(ab -> ab.getStatut().equals("ACTIF"))
                .count();

        double taux = total > 0 ? (actifs * 100.0 / total) : 0;

        if (tauxConversionLabel != null) {
            tauxConversionLabel.setText(String.format("%.1f%%", taux));
        }
    }
}