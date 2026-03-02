package org.example.services;

import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {
    private Connection connection;

    public UtilisateurService() {
        this.connection = MyDataBase.getConnection();
    }

    // ==========================================
    // MÉTHODES CRUD CLASSIQUES
    // ==========================================

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        String query = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            
            // Hachage du mot de passe avec BCrypt
            String hashedPassword = BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
            ps.setString(4, hashedPassword);
            
            ps.setString(5, utilisateur.getRole());
            
            ps.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès!");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
        }
    }

    public List<Utilisateur> afficherUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getTimestamp("date_inscription"),
                        rs.getString("role")
                );
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }

    public Utilisateur getUtilisateurById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id_utilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getTimestamp("date_inscription"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        }
        return null;
    }

    public void modifierUtilisateur(Utilisateur utilisateur) {
        String query = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=? WHERE id_utilisateur=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            
            // Pour la modification, on vérifie d'abord si le mot de passe a déjà été hashé (il commence par $2a$)
            // Sinon on le hash. (Pratique si l'utilisateur met à jour son mdp depuis le profil)
            String pw = utilisateur.getMotDePasse();
            if (!pw.startsWith("$2a$")) {
                pw = BCrypt.hashpw(pw, BCrypt.gensalt());
            }
            ps.setString(4, pw);
            
            ps.setString(5, utilisateur.getRole());
            ps.setInt(6, utilisateur.getIdUtilisateur());
            
            ps.executeUpdate();
            System.out.println("Utilisateur modifié avec succès!");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur: " + e.getMessage());
        }
    }

    public boolean modifierMotDePasseParEmail(String email, String nouveauMdpClair) {
        String query = "UPDATE utilisateur SET mot_de_passe=? WHERE email=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Hachage du nouveau mot de passe
            String hash = BCrypt.hashpw(nouveauMdpClair, BCrypt.gensalt());
            ps.setString(1, hash);
            ps.setString(2, email);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du mot de passe par email: " + e.getMessage());
            return false;
        }
    }

    public void supprimerUtilisateur(int id) {
        String query = "DELETE FROM utilisateur WHERE id_utilisateur=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès!");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTHODES SPÉCIFIQUES (SIGN UP / SIGN IN)
    // ==========================================

    /**
     * Méthode pour l'inscription (Sign Up)
     * Renvoie true si l'inscription a réussi, false si l'email existe déjà.
     */
    public boolean inscrire(Utilisateur utilisateur) {
        // 1. Vérifier si l'email existe déjà
        if (emailExiste(utilisateur.getEmail())) {
            System.err.println("Cet email est déjà utilisé !");
            return false;
        }

        // 2. Si l'email n'existe pas, on ajoute l'utilisateur
        // Par défaut, un nouvel inscrit via le front est un 'client' (à adapter selon les besoins)
        if (utilisateur.getRole() == null || utilisateur.getRole().isEmpty()) {
            utilisateur.setRole("client");
        }
        
        ajouterUtilisateur(utilisateur);
        return true;
    }

    /**
     * Méthode pour vérifier si un email est déjà enregistré
     */
    public boolean emailExiste(String email) {
        String query = "SELECT count(*) FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Méthode pour la connexion (Sign In)
     * Renvoie l'objet Utilisateur si les identifiants sont corrects, sinon null.
     */
    public Utilisateur authentifier(String email, String motDePasseClair) {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashEnBase = rs.getString("mot_de_passe");
                
                // On vérifie le mot de passe clair avec le hash de la BDD
                boolean isValid = false;
                try {
                    isValid = BCrypt.checkpw(motDePasseClair, hashEnBase);
                } catch (IllegalArgumentException e) {
                    // Si le hash n'est pas un hash BCrypt valide (anciens mots de passe en clair), 
                    // on fait une comparaison basique en attendant qu'il soit mis à jour
                    isValid = hashEnBase.equals(motDePasseClair);
                }
                
                if (isValid) {
                    return new Utilisateur(
                            rs.getInt("id_utilisateur"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            hashEnBase,
                            rs.getTimestamp("date_inscription"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
        }
        return null; // Échec de l'authentification (email inexistant ou mot de passe incorrect)
    }
}
