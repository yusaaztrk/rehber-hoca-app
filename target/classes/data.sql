-- Rehber Hoca Sample Data
-- MySQL 8.0+ compatible

-- Clear existing data (in correct order due to foreign keys)
DELETE FROM ogrenci_program;
DELETE FROM ogrenciler;
DELETE FROM programlar;

-- Reset auto increment
ALTER TABLE programlar AUTO_INCREMENT = 1;
ALTER TABLE ogrenciler AUTO_INCREMENT = 1;
ALTER TABLE ogrenci_program AUTO_INCREMENT = 1;

-- Insert sample programs
INSERT INTO programlar (ad, aciklama, kategori, seviye, sure, kapasite, baslangic_tarihi, bitis_tarihi, durum, aktif, olusturma_tarihi) VALUES
('Java Programlama', 'Temel Java programlama eğitimi', 'Yazılım', 'Başlangıç', 12, 20, '2025-05-26', '2025-08-18', 'Aktif', TRUE, NOW()),
('Python Veri Analizi', 'Python ile veri analizi ve görselleştirme', 'Veri Bilimi', 'Orta', 8, 25, '2025-06-02', '2025-07-28', 'Aktif', TRUE, NOW()),
('Web Tasarım', 'HTML, CSS, JavaScript ile web tasarımı', 'Web', 'Başlangıç', 10, 30, '2025-06-09', '2025-08-18', 'Aktif', TRUE, NOW()),
('Mobil Uygulama', 'Android ve iOS uygulama geliştirme', 'Mobil', 'İleri', 16, 15, '2025-06-16', '2025-10-06', 'Aktif', TRUE, NOW()),
('Veritabanı Yönetimi', 'MySQL ve PostgreSQL veritabanı yönetimi', 'Veritabanı', 'Orta', 6, 20, '2025-06-23', '2025-08-04', 'Aktif', TRUE, NOW()),
('React.js Geliştirme', 'Modern React.js ile frontend geliştirme', 'Web', 'Orta', 14, 18, '2025-07-01', '2025-09-30', 'Aktif', TRUE, NOW()),
('Machine Learning', 'Python ile makine öğrenmesi temelleri', 'Veri Bilimi', 'İleri', 20, 12, '2025-07-15', '2025-12-01', 'Aktif', TRUE, NOW()),
('DevOps Temelleri', 'Docker, Kubernetes ve CI/CD', 'DevOps', 'Orta', 10, 15, '2025-08-01', '2025-10-15', 'Aktif', TRUE, NOW());

-- Insert sample students
INSERT INTO ogrenciler (ad, soyad, ad_soyad, email, telefon, aktif, kayit_tarihi) VALUES
('Ahmet', 'Yılmaz', 'Ahmet Yılmaz', 'ahmet.yilmaz@email.com', '0532 123 4567', TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('Ayşe', 'Demir', 'Ayşe Demir', 'ayse.demir@email.com', '0533 234 5678', TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('Mehmet', 'Kaya', 'Mehmet Kaya', 'mehmet.kaya@email.com', '0534 345 6789', TRUE, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('Fatma', 'Öz', 'Fatma Öz', 'fatma.oz@email.com', '0535 456 7890', TRUE, DATE_SUB(NOW(), INTERVAL 28 DAY)),
('Ali', 'Çelik', 'Ali Çelik', 'ali.celik@email.com', '0536 567 8901', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('Zeynep', 'Arslan', 'Zeynep Arslan', 'zeynep.arslan@email.com', '0537 678 9012', TRUE, DATE_SUB(NOW(), INTERVAL 26 DAY)),
('Mustafa', 'Şahin', 'Mustafa Şahin', 'mustafa.sahin@email.com', '0538 789 0123', TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('Elif', 'Koç', 'Elif Koç', 'elif.koc@email.com', '0539 890 1234', TRUE, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('Emre', 'Aydın', 'Emre Aydın', 'emre.aydin@email.com', '0540 901 2345', TRUE, NOW()),
('Seda', 'Güneş', 'Seda Güneş', 'seda.gunes@email.com', '0541 012 3456', TRUE, DATE_SUB(NOW(), INTERVAL 29 DAY)),
('Can', 'Özkan', 'Can Özkan', 'can.ozkan@email.com', '0542 123 4567', TRUE, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('Deniz', 'Yıldız', 'Deniz Yıldız', 'deniz.yildiz@email.com', '0543 234 5678', TRUE, DATE_SUB(NOW(), INTERVAL 12 DAY));

-- Insert sample enrollments
INSERT INTO ogrenci_program (ogrenci_id, program_id, kayit_tarihi, durum, notlar) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Aktif', 'İlk programlama deneyimi'),
(1, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Aktif', 'Web tasarımına ilgi duyuyor'),
(2, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Aktif', 'Matematik geçmişi güçlü'),
(2, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Aktif', 'Veritabanı konusunda deneyimli'),
(3, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Aktif', 'Yazılım geliştirme hedefi'),
(4, 3, DATE_SUB(NOW(), INTERVAL 27 DAY), 'Tamamlandı', 'Başarıyla tamamladı'),
(4, 4, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Aktif', 'Mobil uygulama geliştirme ilgisi'),
(5, 2, NOW(), 'Aktif', 'Veri analizi alanında kariyer hedefi'),
(6, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), 'Aktif', 'Programlama yeni başlangıç'),
(7, 5, DATE_SUB(NOW(), INTERVAL 4 DAY), 'Aktif', 'Veritabanı yöneticisi olmak istiyor'),
(8, 3, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Aktif', 'Freelance web tasarımcı hedefi'),
(9, 4, NOW(), 'Aktif', 'Mobil oyun geliştirme ilgisi'),
(10, 2, DATE_SUB(NOW(), INTERVAL 28 DAY), 'Aktif', 'Akademik araştırma için veri analizi'),
(11, 6, DATE_SUB(NOW(), INTERVAL 14 DAY), 'Aktif', 'React.js öğrenmeye hevesli'),
(12, 7, DATE_SUB(NOW(), INTERVAL 11 DAY), 'Aktif', 'AI ve ML alanında uzmanlaşmak istiyor'),
(1, 6, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Aktif', 'Frontend geliştirme ilgisi'),
(3, 8, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Aktif', 'DevOps kariyeri hedefi'),
(5, 7, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Aktif', 'Makine öğrenmesi projesi için'),
(8, 6, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Aktif', 'Modern web teknolojileri');

-- Commit the transaction
COMMIT;
