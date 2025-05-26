-- Rehber Hoca Database Schema
-- MySQL 8.0+ compatible

-- Drop tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS ogrenci_program;
DROP TABLE IF EXISTS ogrenciler;
DROP TABLE IF EXISTS programlar;

-- Create programlar table
CREATE TABLE programlar (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ad VARCHAR(200) NOT NULL,
    aciklama TEXT,
    kategori VARCHAR(100) DEFAULT 'Genel',
    seviye VARCHAR(50) DEFAULT 'Başlangıç',
    sure INTEGER,
    kapasite INTEGER DEFAULT 0,
    baslangic_tarihi DATE,
    bitis_tarihi DATE,
    durum VARCHAR(50) DEFAULT 'Aktif',
    aktif BOOLEAN DEFAULT TRUE,
    olusturma_tarihi DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_kategori (kategori),
    INDEX idx_seviye (seviye),
    INDEX idx_durum (durum),
    INDEX idx_aktif (aktif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create ogrenciler table
CREATE TABLE ogrenciler (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ad VARCHAR(100) NOT NULL,
    soyad VARCHAR(100) NOT NULL,
    ad_soyad VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefon VARCHAR(20),
    aktif BOOLEAN DEFAULT TRUE,
    kayit_tarihi DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_email (email),
    INDEX idx_ad_soyad (ad_soyad),
    INDEX idx_aktif (aktif),
    INDEX idx_kayit_tarihi (kayit_tarihi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create ogrenci_program table (junction table)
CREATE TABLE ogrenci_program (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ogrenci_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    kayit_tarihi DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    durum VARCHAR(50) DEFAULT 'Aktif',
    notlar TEXT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ogrenci_program (ogrenci_id, program_id),
    INDEX idx_ogrenci_id (ogrenci_id),
    INDEX idx_program_id (program_id),
    INDEX idx_kayit_tarihi (kayit_tarihi),
    INDEX idx_durum (durum),
    FOREIGN KEY (ogrenci_id) REFERENCES ogrenciler(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programlar(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better performance
CREATE INDEX idx_ogrenciler_email ON ogrenciler(email);
CREATE INDEX idx_ogrenciler_aktif ON ogrenciler(aktif);
CREATE INDEX idx_programlar_aktif ON programlar(aktif);
CREATE INDEX idx_programlar_kategori ON programlar(kategori);
CREATE INDEX idx_ogrenci_program_ogrenci ON ogrenci_program(ogrenci_id);
CREATE INDEX idx_ogrenci_program_program ON ogrenci_program(program_id);
CREATE INDEX idx_ogrenci_program_durum ON ogrenci_program(durum);
