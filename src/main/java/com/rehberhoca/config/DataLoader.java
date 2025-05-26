package com.rehberhoca.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.OgrenciProgram;
import com.rehberhoca.entity.Program;
import com.rehberhoca.service.OgrenciService;
import com.rehberhoca.service.ProgramService;
import com.rehberhoca.repository.OgrenciProgramRepository;

/**
 * Uygulama başladığında örnek veri yükleyen sınıf
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private OgrenciService ogrenciService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private OgrenciProgramRepository ogrenciProgramRepository;

    @PostConstruct
    public void init() {
        logger.info("=== Örnek veri yükleniyor (@PostConstruct) ===");

        try {
            // Önce mevcut veri var mı kontrol et
            long ogrenciSayisi = ogrenciService.getTotalCount();
            logger.info("Mevcut öğrenci sayısı: {}", ogrenciSayisi);

            if (ogrenciSayisi == 0) {
                // Örnek programlar oluştur
                createSamplePrograms();

                // Örnek öğrenciler oluştur
                createSampleStudents();

                // Örnek öğrenci-program atamaları oluştur (sadece ilk kurulumda)
                createSampleOgrenciPrograms();
            }

            logger.info("=== Örnek veri başarıyla yüklendi ===");

        } catch (Exception e) {
            logger.error("Örnek veri yüklenirken hata oluştu: {}", e.getMessage(), e);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // CommandLineRunner interface'i için boş implementasyon
        // Asıl iş @PostConstruct'ta yapılıyor
    }

    private void createSamplePrograms() {
        logger.info("Örnek programlar oluşturuluyor...");

        Program[] programs = {
            new Program("Java Programlama", "Temel Java programlama eğitimi", 12),
            new Program("Python Veri Analizi", "Python ile veri analizi ve görselleştirme", 8),
            new Program("Web Tasarım", "HTML, CSS, JavaScript ile web tasarımı", 10),
            new Program("Mobil Uygulama", "Android ve iOS uygulama geliştirme", 16),
            new Program("Veritabanı Yönetimi", "MySQL ve PostgreSQL veritabanı yönetimi", 6)
        };

        String[] kategoriler = {"Yazılım", "Veri Bilimi", "Web", "Mobil", "Veritabanı"};
        String[] seviyeler = {"Başlangıç", "Orta", "Başlangıç", "İleri", "Orta"};

        for (int i = 0; i < programs.length; i++) {
            Program program = programs[i];
            program.setKategori(kategoriler[i]);
            program.setSeviye(seviyeler[i]);
            program.setDurum("Aktif");
            program.setKapasite(20 + (i * 5)); // 20, 25, 30, 35, 40
            program.setBaslangicTarihi(LocalDate.now().plusDays(i * 7));
            program.setBitisTarihi(LocalDate.now().plusDays((i * 7) + (program.getSure() * 7)));
            program.setAktif(true);

            programService.programKaydet(program);
            logger.info("Program oluşturuldu: {}", program.getAd());
        }
    }

    private void createSampleStudents() {
        logger.info("Örnek öğrenciler oluşturuluyor...");

        String[][] studentData = {
            {"Ahmet", "Yılmaz", "ahmet.yilmaz@email.com", "0532 123 4567"},
            {"Ayşe", "Demir", "ayse.demir@email.com", "0533 234 5678"},
            {"Mehmet", "Kaya", "mehmet.kaya@email.com", "0534 345 6789"},
            {"Fatma", "Öz", "fatma.oz@email.com", "0535 456 7890"},
            {"Ali", "Çelik", "ali.celik@email.com", "0536 567 8901"},
            {"Zeynep", "Arslan", "zeynep.arslan@email.com", "0537 678 9012"},
            {"Mustafa", "Şahin", "mustafa.sahin@email.com", "0538 789 0123"},
            {"Elif", "Koç", "elif.koc@email.com", "0539 890 1234"},
            {"Emre", "Aydın", "emre.aydin@email.com", "0540 901 2345"},
            {"Seda", "Güneş", "seda.gunes@email.com", "0541 012 3456"}
        };

        for (String[] data : studentData) {
            try {
                Ogrenci ogrenci = new Ogrenci();
                ogrenci.setAd(data[0]);
                ogrenci.setSoyad(data[1]);
                ogrenci.setEmail(data[2]);
                ogrenci.setTelefon(data[3]);
                ogrenci.setKayitTarihi(LocalDateTime.now().minusDays((int)(Math.random() * 30)));
                ogrenci.setAktif(true);

                ogrenciService.ogrenciKaydet(ogrenci);
                logger.info("Öğrenci oluşturuldu: {}", ogrenci.getAdSoyad());

            } catch (Exception e) {
                logger.warn("Öğrenci oluşturulamadı: {} {} - {}", data[0], data[1], e.getMessage());
            }
        }
    }

    private void createSampleOgrenciPrograms() {
        logger.info("Örnek öğrenci-program atamaları oluşturuluyor...");

        try {
            // Tüm öğrencileri ve programları al
            var ogrenciler = ogrenciService.tumOgrencileriGetir();
            var programlar = programService.tumProgramlariGetir();

            if (ogrenciler.isEmpty() || programlar.isEmpty()) {
                logger.warn("Öğrenci veya program bulunamadı, atama yapılamıyor.");
                return;
            }

            // Her öğrenciyi rastgele 1-3 programa ata
            for (Ogrenci ogrenci : ogrenciler) {
                int atamaSayisi = 1 + (int)(Math.random() * 3); // 1-3 arası

                for (int i = 0; i < atamaSayisi && i < programlar.size(); i++) {
                    Program program = programlar.get((int)(Math.random() * programlar.size()));

                    // Aynı öğrenci-program ataması var mı kontrol et
                    boolean mevcutAtama = ogrenciProgramRepository.findByOgrenciAndProgram(ogrenci, program).isPresent();

                    if (!mevcutAtama) {
                        OgrenciProgram ogrenciProgram = new OgrenciProgram();
                        ogrenciProgram.setOgrenci(ogrenci);
                        ogrenciProgram.setProgram(program);
                        ogrenciProgram.setKayitTarihi(LocalDateTime.now().minusDays((int)(Math.random() * 10)));
                        ogrenciProgram.setDurum("Aktif");
                        ogrenciProgram.setNotlar("Sistem tarafından otomatik atandı");

                        ogrenciProgramRepository.save(ogrenciProgram);
                        logger.info("Atama oluşturuldu: {} -> {}", ogrenci.getAdSoyad(), program.getAd());
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Öğrenci-program atamaları oluşturulurken hata: {}", e.getMessage(), e);
        }
    }
}
