-- Rehber Hoca Database Fix Script
-- Bu script'i MySQL'de çalıştırarak veritabanı sorunlarını çözebilirsiniz

-- Veritabanını seç
USE rehber_hoca_db;

-- Mevcut tabloları kontrol et ve gerekirse düzelt
-- Önce foreign key constraint'leri kaldır
SET FOREIGN_KEY_CHECKS = 0;

-- Tabloları yeniden oluştur (eğer sorun varsa)
DROP TABLE IF EXISTS ogrenci_program;
DROP TABLE IF EXISTS ogrenciler;
DROP TABLE IF EXISTS programlar;

-- Programlar tablosunu oluştur
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

-- Öğrenciler tablosunu oluştur
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

-- Öğrenci-Program ilişki tablosunu oluştur
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

-- Foreign key constraint'leri tekrar aktif et
SET FOREIGN_KEY_CHECKS = 1;

-- Örnek veri ekle
INSERT INTO programlar (ad, aciklama, kategori, seviye, sure, kapasite, baslangic_tarihi, bitis_tarihi, durum, aktif, olusturma_tarihi) VALUES
('Java Programlama', 'Temel Java programlama eğitimi', 'Yazılım', 'Başlangıç', 12, 20, '2025-05-26', '2025-08-18', 'Aktif', TRUE, NOW()),
('Python Veri Analizi', 'Python ile veri analizi ve görselleştirme', 'Veri Bilimi', 'Orta', 8, 25, '2025-06-02', '2025-07-28', 'Aktif', TRUE, NOW()),
('Web Tasarım', 'HTML, CSS, JavaScript ile web tasarımı', 'Web', 'Başlangıç', 10, 30, '2025-06-09', '2025-08-18', 'Aktif', TRUE, NOW()),
('Mobil Uygulama', 'Android ve iOS uygulama geliştirme', 'Mobil', 'İleri', 16, 15, '2025-06-16', '2025-10-06', 'Aktif', TRUE, NOW()),
('Veritabanı Yönetimi', 'MySQL ve PostgreSQL veritabanı yönetimi', 'Veritabanı', 'Orta', 6, 20, '2025-06-23', '2025-08-04', 'Aktif', TRUE, NOW());

INSERT INTO ogrenciler (ad, soyad, ad_soyad, email, telefon, aktif, kayit_tarihi) VALUES
('Ahmet', 'Yılmaz', 'Ahmet Yılmaz', 'ahmet.yilmaz@email.com', '0532 123 4567', TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('Ayşe', 'Demir', 'Ayşe Demir', 'ayse.demir@email.com', '0533 234 5678', TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('Mehmet', 'Kaya', 'Mehmet Kaya', 'mehmet.kaya@email.com', '0534 345 6789', TRUE, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('Fatma', 'Öz', 'Fatma Öz', 'fatma.oz@email.com', '0535 456 7890', TRUE, DATE_SUB(NOW(), INTERVAL 28 DAY)),
('Ali', 'Çelik', 'Ali Çelik', 'ali.celik@email.com', '0536 567 8901', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO ogrenci_program (ogrenci_id, program_id, kayit_tarihi, durum, notlar) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Aktif', 'İlk programlama deneyimi'),
(1, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Aktif', 'Web tasarımına ilgi duyuyor'),
(2, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Aktif', 'Matematik geçmişi güçlü'),
(3, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Aktif', 'Yazılım geliştirme hedefi'),
(4, 3, DATE_SUB(NOW(), INTERVAL 27 DAY), 'Tamamlandı', 'Başarıyla tamamladı');

-- Veritabanı durumunu kontrol et
SELECT 'Veritabanı düzeltme işlemi tamamlandı!' as Mesaj;
SELECT COUNT(*) as 'Toplam Öğrenci' FROM ogrenciler;
SELECT COUNT(*) as 'Toplam Program' FROM programlar;
SELECT COUNT(*) as 'Toplam Atama' FROM ogrenci_program;
