-- ÖĞRENCİ TABLOSUNA ŞİFRE SÜTUNU EKLEME
-- Bu SQL kodunu MySQL/MariaDB veritabanınızda çalıştırın

-- 1. Şifre sütununu ekle
ALTER TABLE ogrenciler 
ADD COLUMN sifre VARCHAR(255);

-- 2. Mevcut öğrenciler için varsayılan şifre ata
UPDATE ogrenciler 
SET sifre = '123456' 
WHERE sifre IS NULL;

-- 3. Şifre sütununu zorunlu yap
ALTER TABLE ogrenciler 
MODIFY COLUMN sifre VARCHAR(255) NOT NULL;

-- 4. Kontrol sorgusu
SELECT id, ad_soyad, email, sifre, kayit_tarihi 
FROM ogrenciler 
ORDER BY id;

-- 5. Şifre istatistikleri
SELECT 
    COUNT(*) as toplam_ogrenci,
    COUNT(CASE WHEN LENGTH(sifre) >= 6 THEN 1 END) as gecerli_sifre_sayisi,
    COUNT(CASE WHEN LENGTH(sifre) < 6 THEN 1 END) as gecersiz_sifre_sayisi
FROM ogrenciler;

-- NOTLAR:
-- - Bu basit SQL kodu geliştirme amaçlıdır
-- - Gerçek uygulamada şifreler hash'lenmelidir
-- - Varsayılan şifre '123456' güvenlik için değiştirilmelidir
-- - Tüm öğrenciler şifrelerini ilk girişte değiştirmelidir
