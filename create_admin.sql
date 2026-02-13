-- Add Default Admin User
-- Inserts a default admin if not exists (email: admin@mindgrow.com, pass: admin123)
-- The application will automatically hash the plain text password on first login

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, roles, telephone, date_naissance, date_inscription)
SELECT * FROM (SELECT 'Admin', 'System', 'admin@mindgrow.com', 'admin123', '["ROLE_ADMIN"]', '00000000', '2000-01-01', NOW()) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM utilisateur WHERE email = 'admin@mindgrow.com'
) LIMIT 1;
