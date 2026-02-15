package utils;

import entities.Abonnement;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe utilitaire pour exporter les abonnements vers Excel
 */
public class ExcelExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporter un abonnement unique vers Excel
     *
     * @param abonnement L'abonnement à exporter
     * @param filePath Le chemin du fichier Excel à créer
     * @return true si l'export a réussi, false sinon
     */
    public static boolean exporterAbonnement(Abonnement abonnement, String filePath) {
        if (abonnement == null) {
            System.err.println("❌ Impossible d'exporter un abonnement null");
            return false;
        }

        // Créer une liste contenant uniquement cet abonnement
        return exporterAbonnements(List.of(abonnement), filePath);
    }

    /**
     * Exporter une liste d'abonnements vers Excel
     *
     * @param abonnements La liste des abonnements à exporter
     * @param filePath Le chemin du fichier Excel à créer
     * @return true si l'export a réussi, false sinon
     */
    public static boolean exporterAbonnements(List<Abonnement> abonnements, String filePath) {
        if (abonnements == null || abonnements.isEmpty()) {
            System.err.println("❌ Aucun abonnement à exporter");
            return false;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // Créer une feuille
            Sheet sheet = workbook.createSheet("Abonnements");

            // Créer les styles
            CellStyle headerStyle = creerStyleEntete(workbook);
            CellStyle dateStyle = creerStyleDate(workbook);
            CellStyle montantStyle = creerStyleMontant(workbook);
            CellStyle statutStyle = creerStyleStatut(workbook);

            // Créer l'en-tête
            creerEntete(sheet, headerStyle);

            // Remplir les données
            int rowNum = 1;
            for (Abonnement ab : abonnements) {
                Row row = sheet.createRow(rowNum++);
                remplirLigneAbonnement(row, ab, dateStyle, montantStyle, statutStyle);
            }

            // Ajuster automatiquement la largeur des colonnes
            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
                // Ajouter un peu d'espace supplémentaire
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            // Écrire dans le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Export Excel réussi : " + filePath);
            System.out.println("   Nombre d'abonnements exportés : " + abonnements.size());
            return true;

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'export Excel!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Créer le style pour l'en-tête
     */
    private static CellStyle creerStyleEntete(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Couleur de fond
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Police en gras et blanc
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // Alignement
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Bordures
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    /**
     * Créer le style pour les dates
     */
    private static CellStyle creerStyleDate(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        // Bordures
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    /**
     * Créer le style pour les montants
     */
    private static CellStyle creerStyleMontant(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);

        // Format monétaire
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00 €"));

        // Bordures
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    /**
     * Créer le style pour les statuts
     */
    private static CellStyle creerStyleStatut(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        // Police en gras
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        // Bordures
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    /**
     * Créer l'en-tête du tableau
     */
    private static void creerEntete(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        String[] colonnes = {
                "ID",
                "Utilisateur",
                "Email",
                "Type",
                "Montant (€)",
                "Date Début",
                "Date Expiration",
                "Statut Abonnement",
                "Statut Paiement",
                "Jours Restants",
                "État"
        };

        for (int i = 0; i < colonnes.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colonnes[i]);
            cell.setCellStyle(headerStyle);
        }

        // Figer la première ligne
        sheet.createFreezePane(0, 1);
    }

    /**
     * Remplir une ligne avec les données d'un abonnement
     */
    private static void remplirLigneAbonnement(Row row, Abonnement ab,
                                               CellStyle dateStyle,
                                               CellStyle montantStyle,
                                               CellStyle statutStyle) {
        Workbook workbook = row.getSheet().getWorkbook();

        // ID
        Cell cellId = row.createCell(0);
        cellId.setCellValue(ab.getId());
        appliqueBordures(cellId, workbook);

        // Utilisateur
        Cell cellUser = row.createCell(1);
        String nomComplet = ab.getNomUtilisateur() + " " + ab.getPrenomUtilisateur();
        cellUser.setCellValue(nomComplet);
        appliqueBordures(cellUser, workbook);

        // Email
        Cell cellEmail = row.createCell(2);
        cellEmail.setCellValue(ab.getEmailUtilisateur());
        appliqueBordures(cellEmail, workbook);

        // Type
        Cell cellType = row.createCell(3);
        cellType.setCellValue(ab.getType());
        appliqueBordures(cellType, workbook);

        // Montant
        Cell cellMontant = row.createCell(4);
        cellMontant.setCellValue(ab.getMontant());
        cellMontant.setCellStyle(montantStyle);

        // Date Début
        Cell cellDateDebut = row.createCell(5);
        cellDateDebut.setCellValue(ab.getDateDebutFormatted());
        cellDateDebut.setCellStyle(dateStyle);

        // Date Expiration
        Cell cellDateExp = row.createCell(6);
        cellDateExp.setCellValue(ab.getDateExpirationFormatted());
        cellDateExp.setCellStyle(dateStyle);

        // Statut Abonnement
        Cell cellStatut = row.createCell(7);
        cellStatut.setCellValue(ab.getStatut());
        cellStatut.setCellStyle(statutStyle);
        appliquerCouleurStatutAbonnement(cellStatut, ab.getStatut(), workbook);

        // Statut Paiement
        Cell cellStatutPaiement = row.createCell(8);
        String statutPaiement = ab.getStatutPaiement() != null ? ab.getStatutPaiement() : "AUCUN";
        cellStatutPaiement.setCellValue(statutPaiement);
        cellStatutPaiement.setCellStyle(statutStyle);
        appliquerCouleurStatutPaiement(cellStatutPaiement, statutPaiement, workbook);

        // Jours Restants
        Cell cellJours = row.createCell(9);
        long joursRestants = java.time.Duration.between(
                java.time.LocalDateTime.now(),
                ab.getDateExpiration()
        ).toDays();
        cellJours.setCellValue(joursRestants);
        appliqueBordures(cellJours, workbook);

        // État (Actif/Expiré)
        Cell cellEtat = row.createCell(10);
        String etat = joursRestants > 0 ? "EN COURS" : "EXPIRÉ";
        cellEtat.setCellValue(etat);
        cellEtat.setCellStyle(statutStyle);
        if (joursRestants > 0) {
            cellEtat.getCellStyle().setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else {
            cellEtat.getCellStyle().setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        }
        cellEtat.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    /**
     * Appliquer les bordures à une cellule
     */
    private static void appliqueBordures(Cell cell, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        cell.setCellStyle(style);
    }

    /**
     * Appliquer une couleur selon le statut de l'abonnement
     */
    private static void appliquerCouleurStatutAbonnement(Cell cell, String statut, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());

        switch (statut) {
            case "ACTIF":
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                break;
            case "EXPIRE":
                style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                break;
            case "SUSPENDU":
                style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                break;
            case "ANNULE":
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                break;
        }

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    /**
     * Appliquer une couleur selon le statut de paiement
     */
    private static void appliquerCouleurStatutPaiement(Cell cell, String statut, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());

        switch (statut) {
            case "VALIDE":
                style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                break;
            case "EN_ATTENTE":
                style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                break;
            case "ECHOUE":
                style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                break;
            case "REMBOURSE":
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                break;
            case "AUCUN":
                style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                break;
        }

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }
}