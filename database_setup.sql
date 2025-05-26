-- Rehber Hoca Uygulaması Veritabanı Kurulum Script'i
-- Bu script'i MySQL'de çalıştırarak veritabanını hazırlayabilirsiniz

-- Veritabanını oluştur
CREATE DATABASE IF NOT EXISTS rehber_hoca_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE rehber_hoca_db;

-- Öğrenciler tablosu
CREATE TABLE IF NOT EXISTS ogrenciler (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ad VARCHAR(100) NOT NULL,
    soyad VARCHAR(100) NOT NULL,
    ad_soyad VARCHAR(200) GENERATED ALWAYS AS (CONCAT(ad, ' ', soyad)) STORED,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefon VARCHAR(20),
    kayit_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP,
    aktif BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_ad_soyad (ad_soyad),
    INDEX idx_kayit_tarihi (kayit_tarihi)
);

-- Programlar tablosu
CREATE TABLE IF NOT EXISTS programlar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ad VARCHAR(200) NOT NULL,
    aciklama TEXT,
    baslangic_tarihi DATE,
    bitis_tarihi DATE,
    kategori VARCHAR(100),
    seviye VARCHAR(50) DEFAULT 'Başlangıç',
    durum VARCHAR(50) DEFAULT 'Aktif',
    olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    aktif BOOLEAN DEFAULT TRUE,
    INDEX idx_kategori (kategori),
    INDEX idx_seviye (seviye),
    INDEX idx_durum (durum),
    INDEX idx_baslangic_tarihi (baslangic_tarihi)
);

-- Öğrenci-Program ilişki tablosu (Many-to-Many)
CREATE TABLE IF NOT EXISTS ogrenci_program (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ogrenci_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    kayit_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP,
    durum VARCHAR(50) DEFAULT 'Aktif',
    notlar TEXT,
    FOREIGN KEY (ogrenci_id) REFERENCES ogrenciler(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programlar(id) ON DELETE CASCADE,
    UNIQUE KEY unique_ogrenci_program (ogrenci_id, program_id),
    INDEX idx_ogrenci_id (ogrenci_id),
    INDEX idx_program_id (program_id),
    INDEX idx_kayit_tarihi (kayit_tarihi),
    INDEX idx_durum (durum)
);

-- Örnek veri ekleme
INSERT IGNORE INTO ogrenciler (ad, soyad, email, telefon) VALUES
('Ahmet', 'Yılmaz', 'ahmet.yilmaz@email.com', '0532 123 4567'),
('Ayşe', 'Demir', 'ayse.demir@email.com', '0533 234 5678'),
('Mehmet', 'Kaya', 'mehmet.kaya@email.com', '0534 345 6789'),
('Fatma', 'Öz', 'fatma.oz@email.com', '0535 456 7890'),
('Ali', 'Çelik', 'ali.celik@email.com', '0536 567 8901'),
('Zeynep', 'Arslan', 'zeynep.arslan@email.com', '0537 678 9012');

INSERT IGNORE INTO programlar (ad, aciklama, baslangic_tarihi, bitis_tarihi, kategori, seviye) VALUES
('Java Programlama', 'Temel Java programlama eğitimi', '2025-01-15', '2025-03-15', 'Yazılım', 'Başlangıç'),
('Python Veri Analizi', 'Python ile veri analizi ve görselleştirme', '2025-02-01', '2025-04-01', 'Veri Bilimi', 'Orta'),
('Web Tasarım', 'HTML, CSS, JavaScript ile web tasarımı', '2025-01-20', '2025-03-20', 'Web', 'Başlangıç'),
('Mobil Uygulama', 'Android ve iOS uygulama geliştirme', '2025-02-15', '2025-05-15', 'Mobil', 'İleri'),
('Veritabanı Yönetimi', 'MySQL ve PostgreSQL veritabanı yönetimi', '2025-01-10', '2025-02-28', 'Veritabanı', 'Orta');

-- Örnek öğrenci-program atamaları
INSERT IGNORE INTO ogrenci_program (ogrenci_id, program_id, durum, notlar) VALUES
(1, 1, 'Aktif', 'Düzenli katılım gösteriyor'),
(1, 3, 'Aktif', 'Web tasarımında yetenekli'),
(2, 2, 'Aktif', 'Python konusunda hızlı öğreniyor'),
(3, 1, 'Tamamlandı', 'Başarıyla tamamladı'),
(3, 4, 'Aktif', 'Mobil geliştirmede ilerleme kaydediyor'),
(4, 5, 'Aktif', 'Veritabanı konusunda deneyimli'),
(5, 1, 'Aktif', 'Java programlamada başarılı'),
(6, 2, 'Aktif', 'Veri analizi projelerinde aktif');

-- Veritabanı istatistikleri için view'lar
CREATE OR REPLACE VIEW ogrenci_istatistikleri AS
SELECT 
    COUNT(*) as toplam_ogrenci,
    COUNT(CASE WHEN aktif = TRUE THEN 1 END) as aktif_ogrenci,
    COUNT(CASE WHEN DATE(kayit_tarihi) = CURDATE() THEN 1 END) as bugun_kayit
FROM ogrenciler;

CREATE OR REPLACE VIEW program_istatistikleri AS
SELECT 
    COUNT(*) as toplam_program,
    COUNT(CASE WHEN aktif = TRUE THEN 1 END) as aktif_program,
    COUNT(CASE WHEN baslangic_tarihi <= CURDATE() AND bitis_tarihi >= CURDATE() THEN 1 END) as devam_eden_program
FROM programlar;

CREATE OR REPLACE VIEW atama_istatistikleri AS
SELECT 
    COUNT(*) as toplam_atama,
    COUNT(CASE WHEN durum = 'Aktif' THEN 1 END) as aktif_atama,
    COUNT(CASE WHEN durum = 'Tamamlandı' THEN 1 END) as tamamlanan_atama
FROM ogrenci_program;

-- Kullanışlı stored procedure'lar
DELIMITER //

CREATE PROCEDURE GetOgrenciProgramlari(IN ogrenci_id_param BIGINT)
BEGIN
    SELECT 
        p.id,
        p.ad,
        p.aciklama,
        p.kategori,
        p.seviye,
        op.durum,
        op.kayit_tarihi,
        op.notlar
    FROM programlar p
    INNER JOIN ogrenci_program op ON p.id = op.program_id
    WHERE op.ogrenci_id = ogrenci_id_param
    ORDER BY op.kayit_tarihi DESC;
END //

CREATE PROCEDURE GetProgramOgrencileri(IN program_id_param BIGINT)
BEGIN
    SELECT 
        o.id,
        o.ad,
        o.soyad,
        o.ad_soyad,
        o.email,
        o.telefon,
        op.durum,
        op.kayit_tarihi,
        op.notlar
    FROM ogrenciler o
    INNER JOIN ogrenci_program op ON o.id = op.ogrenci_id
    WHERE op.program_id = program_id_param
    ORDER BY op.kayit_tarihi DESC;
END //

DELIMITER ;

-- İndeksler ve performans optimizasyonu
ANALYZE TABLE ogrenciler, programlar, ogrenci_program;

-- Yetkilendirme (isteğe bağlı)
-- CREATE USER IF NOT EXISTS 'rehber_user'@'localhost' IDENTIFIED BY 'rehber_pass';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON rehber_hoca_db.* TO 'rehber_user'@'localhost';
-- FLUSH PRIVILEGES;

SELECT 'Veritabanı kurulumu tamamlandı!' as Mesaj;
