-- MindGrow Database Migration
-- Adds reset_token columns needed for forgot password feature
-- Run this against your 'mindgrow' database

ALTER TABLE utilisateur
ADD COLUMN IF NOT EXISTS reset_token VARCHAR(10) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS reset_token_expiry DATETIME DEFAULT NULL;
