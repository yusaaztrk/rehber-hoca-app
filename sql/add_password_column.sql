-- Öğrenci tablosuna şifre sütunu ekleme
-- Bu SQL kodunu veritabanınızda çalıştırın

-- 1. Şifre sütununu ekle (önce nullable olarak)
ALTER TABLE ogrenciler 
ADD COLUMN sifre VARCHAR(255);

-- 2. Mevcut öğrenciler için varsayılan şifre ata (güvenlik için değiştirilmeli)
UPDATE ogrenciler 
SET sifre = '123456' 
WHERE sifre IS NULL;

-- 3. Şifre sütununu NOT NULL yap
ALTER TABLE ogrenciler 
MODIFY COLUMN sifre VARCHAR(255) NOT NULL;

-- 4. Şifre sütunu için index ekle (performans için)
CREATE INDEX idx_ogrenciler_sifre ON ogrenciler(sifre);

-- 5. Kontrol sorgusu - Tüm öğrencilerin şifresi olduğunu doğrula
SELECT 
    id,
    ad_soyad,
    email,
    CASE 
        WHEN sifre IS NOT NULL AND LENGTH(sifre) >= 6 THEN 'Şifre OK'
        ELSE 'Şifre Eksik/Kısa'
    END as sifre_durumu
FROM ogrenciler
ORDER BY id;

-- 6. İstatistik sorgusu
SELECT 
    COUNT(*) as toplam_ogrenci,
    COUNT(CASE WHEN sifre IS NOT NULL AND LENGTH(sifre) >= 6 THEN 1 END) as gecerli_sifre,
    COUNT(CASE WHEN sifre IS NULL OR LENGTH(sifre) < 6 THEN 1 END) as gecersiz_sifre
FROM ogrenciler;

-- 7. Güvenlik notu
-- UYARI: Gerçek uygulamada şifreler hash'lenmiş olarak saklanmalıdır!
-- Bu örnek sadece geliştirme amaçlıdır.
-- Üretim ortamında BCrypt, Argon2 gibi güvenli hash algoritmaları kullanın.

-- 8. Örnek güvenli şifre güncelleme (opsiyonel)
-- UPDATE ogrenciler 
-- SET sifre = SHA2(CONCAT(email, '_', '123456'), 256)
-- WHERE id = 1;

-- 9. Şifre politikası kontrolü için view oluştur (opsiyonel)
CREATE OR REPLACE VIEW v_ogrenci_sifre_kontrol AS
SELECT 
    id,
    ad_soyad,
    email,
    LENGTH(sifre) as sifre_uzunlugu,
    CASE 
        WHEN LENGTH(sifre) >= 8 AND sifre REGEXP '[A-Z]' AND sifre REGEXP '[a-z]' AND sifre REGEXP '[0-9]' THEN 'Güçlü'
        WHEN LENGTH(sifre) >= 6 THEN 'Orta'
        ELSE 'Zayıf'
    END as sifre_gucu,
    kayit_tarihi
FROM ogrenciler
ORDER BY 
    CASE 
        WHEN LENGTH(sifre) >= 8 AND sifre REGEXP '[A-Z]' AND sifre REGEXP '[a-z]' AND sifre REGEXP '[0-9]' THEN 1
        WHEN LENGTH(sifre) >= 6 THEN 2
        ELSE 3
    END,
    ad_soyad;

-- 10. Şifre güncelleme trigger'ı (opsiyonel - şifre değişiklik tarihini takip için)
DELIMITER //
CREATE TRIGGER tr_ogrenci_sifre_guncelleme
    BEFORE UPDATE ON ogrenciler
    FOR EACH ROW
BEGIN
    -- Şifre değiştirildiğinde kayıt tarihi güncellenmez, sadece şifre değişikliği loglanır
    IF OLD.sifre != NEW.sifre THEN
        -- Burada şifre değişiklik logu tutulabilir
        INSERT INTO sifre_degisiklik_log (ogrenci_id, eski_sifre_hash, yeni_sifre_hash, degisiklik_tarihi)
        VALUES (NEW.id, SHA2(OLD.sifre, 256), SHA2(NEW.sifre, 256), NOW());
    END IF;
END//
DELIMITER ;

-- 11. Şifre değişiklik log tablosu (opsiyonel)
CREATE TABLE IF NOT EXISTS sifre_degisiklik_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ogrenci_id BIGINT NOT NULL,
    eski_sifre_hash VARCHAR(64),
    yeni_sifre_hash VARCHAR(64),
    degisiklik_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ogrenci_id) REFERENCES ogrenciler(id) ON DELETE CASCADE
);

-- 12. Test sorguları
-- Şifre uzunluğu dağılımı
SELECT 
    CASE 
        WHEN LENGTH(sifre) < 6 THEN 'Çok Kısa (<6)'
        WHEN LENGTH(sifre) BETWEEN 6 AND 7 THEN 'Kısa (6-7)'
        WHEN LENGTH(sifre) BETWEEN 8 AND 12 THEN 'Orta (8-12)'
        ELSE 'Uzun (>12)'
    END as sifre_kategori,
    COUNT(*) as ogrenci_sayisi
FROM ogrenciler
GROUP BY 
    CASE 
        WHEN LENGTH(sifre) < 6 THEN 'Çok Kısa (<6)'
        WHEN LENGTH(sifre) BETWEEN 6 AND 7 THEN 'Kısa (6-7)'
        WHEN LENGTH(sifre) BETWEEN 8 AND 12 THEN 'Orta (8-12)'
        ELSE 'Uzun (>12)'
    END
ORDER BY ogrenci_sayisi DESC;

-- 13. Şifre güvenlik raporu
SELECT 
    'Toplam Öğrenci' as metrik,
    COUNT(*) as deger
FROM ogrenciler
UNION ALL
SELECT 
    'Güçlü Şifre (8+ karakter, büyük/küçük harf, rakam)',
    COUNT(*)
FROM ogrenciler 
WHERE LENGTH(sifre) >= 8 
    AND sifre REGEXP '[A-Z]' 
    AND sifre REGEXP '[a-z]' 
    AND sifre REGEXP '[0-9]'
UNION ALL
SELECT 
    'Orta Şifre (6+ karakter)',
    COUNT(*)
FROM ogrenciler 
WHERE LENGTH(sifre) >= 6 
    AND LENGTH(sifre) < 8
UNION ALL
SELECT 
    'Zayıf Şifre (<6 karakter)',
    COUNT(*)
FROM ogrenciler 
WHERE LENGTH(sifre) < 6;

-- NOTLAR:
-- 1. Bu SQL kodları MySQL/MariaDB için yazılmıştır
-- 2. PostgreSQL için REGEXP yerine ~ operatörü kullanın
-- 3. Gerçek uygulamada şifreler mutlaka hash'lenmelidir
-- 4. Trigger ve log tablosu opsiyoneldir, ihtiyaca göre kullanın
-- 5. Şifre politikası view'ı performans için dikkatli kullanın
