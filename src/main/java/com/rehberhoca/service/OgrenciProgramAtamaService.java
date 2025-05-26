package com.rehberhoca.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.Program;
import com.rehberhoca.entity.OgrenciProgramAtama;
import com.rehberhoca.entity.OgrenciProgramAtama.AtamaDurum;
import com.rehberhoca.repository.OgrenciProgramAtamaRepository;
import com.rehberhoca.repository.OgrenciRepository;
import com.rehberhoca.repository.ProgramRepository;

/**
 * Öğrenci-Program Atama Service
 * Yeni temiz atama sistemi
 */
@Service
@Transactional
public class OgrenciProgramAtamaService {

    private static final Logger logger = LoggerFactory.getLogger(OgrenciProgramAtamaService.class);

    @Autowired
    private OgrenciProgramAtamaRepository atamaRepository;

    @Autowired
    private OgrenciRepository ogrenciRepository;

    @Autowired
    private ProgramRepository programRepository;

    // Temel CRUD İşlemleri

    public OgrenciProgramAtama save(OgrenciProgramAtama atama) {
        return atamaRepository.save(atama);
    }

    public Optional<OgrenciProgramAtama> findById(Long id) {
        return atamaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> findAll() {
        try {
            List<OgrenciProgramAtama> atamalar = atamaRepository.findAll();
            // Lazy loading için entity'leri initialize et
            for (OgrenciProgramAtama atama : atamalar) {
                if (atama.getOgrenci() != null) {
                    atama.getOgrenci().getAdSoyad(); // Lazy loading'i tetikle
                }
                if (atama.getProgram() != null) {
                    atama.getProgram().getAd(); // Lazy loading'i tetikle
                }
            }
            return atamalar;
        } catch (Exception e) {
            logger.error("Tüm atamaları getirme hatası: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void delete(OgrenciProgramAtama atama) {
        atamaRepository.delete(atama);
    }

    public void deleteById(Long id) {
        atamaRepository.deleteById(id);
    }

    // Ana Atama İşlemleri

    /**
     * Öğrenciyi programa ata
     */
    @Transactional
    public OgrenciProgramAtama ogrenciyiProgramaAta(Long ogrenciId, Long programId) {
        return ogrenciyiProgramaAta(ogrenciId, programId, AtamaDurum.Aktif, null, "Sistem");
    }

    /**
     * Öğrenciyi programa ata (detaylı)
     */
    @Transactional
    public OgrenciProgramAtama ogrenciyiProgramaAta(Long ogrenciId, Long programId, AtamaDurum durum, String notlar, String olusturan) {
        try {
            // Çift kayıt kontrolü
            if (atamaRepository.existsByOgrenciIdAndProgramId(ogrenciId, programId)) {
                throw new RuntimeException("Bu öğrenci zaten bu programa atanmış!");
            }

            // Öğrenci ve program kontrolü
            Optional<Ogrenci> ogrenciOpt = ogrenciRepository.findById(ogrenciId);
            Optional<Program> programOpt = programRepository.findById(programId);

            if (!ogrenciOpt.isPresent()) {
                throw new RuntimeException("Öğrenci bulunamadı: " + ogrenciId);
            }

            if (!programOpt.isPresent()) {
                throw new RuntimeException("Program bulunamadı: " + programId);
            }

            // Yeni atama oluştur
            OgrenciProgramAtama atama = new OgrenciProgramAtama();
            atama.setOgrenci(ogrenciOpt.get());
            atama.setProgram(programOpt.get());
            atama.setDurum(durum != null ? durum : AtamaDurum.Aktif);
            atama.setNotlar(notlar);
            atama.setOlusturan(olusturan != null ? olusturan : "Sistem");
            atama.setAtamaTarihi(LocalDateTime.now());

            OgrenciProgramAtama savedAtama = atamaRepository.save(atama);

            logger.info("Öğrenci programa başarıyla atandı: {} -> {}",
                       ogrenciOpt.get().getAdSoyad(), programOpt.get().getAd());

            return savedAtama;

        } catch (Exception e) {
            logger.error("Öğrenci programa atama hatası: {}", e.getMessage(), e);
            throw new RuntimeException("Öğrenci programa atanamadı: " + e.getMessage());
        }
    }

    /**
     * Öğrenciyi programdan çıkar
     */
    @Transactional
    public boolean ogrenciyiProgramdanCikar(Long ogrenciId, Long programId) {
        try {
            Optional<OgrenciProgramAtama> atamaOpt = atamaRepository.findByOgrenciIdAndProgramId(ogrenciId, programId);

            if (atamaOpt.isPresent()) {
                atamaRepository.delete(atamaOpt.get());
                logger.info("Öğrenci programdan başarıyla çıkarıldı: {} -> {}",
                           atamaOpt.get().getOgrenciAdSoyad(), atamaOpt.get().getProgramAd());
                return true;
            } else {
                logger.warn("Çıkarılacak atama bulunamadı: ogrenciId={}, programId={}", ogrenciId, programId);
                return false;
            }

        } catch (Exception e) {
            logger.error("Öğrenci programdan çıkarma hatası: {}", e.getMessage(), e);
            throw new RuntimeException("Öğrenci programdan çıkarılamadı: " + e.getMessage());
        }
    }

    /**
     * Atama durumunu güncelle
     */
    @Transactional
    public boolean atamaDurumGuncelle(Long atamaId, AtamaDurum yeniDurum) {
        try {
            int updated = atamaRepository.updateDurum(atamaId, yeniDurum);
            if (updated > 0) {
                logger.info("Atama durumu güncellendi: atamaId={}, yeniDurum={}", atamaId, yeniDurum);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Atama durumu güncelleme hatası: {}", e.getMessage(), e);
            throw new RuntimeException("Atama durumu güncellenemedi: " + e.getMessage());
        }
    }

    // Sorgulama İşlemleri

    /**
     * Öğrencinin atamalarını getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getOgrenciAtamalari(Long ogrenciId) {
        return atamaRepository.findByOgrenciId(ogrenciId);
    }

    /**
     * Öğrencinin aktif atamalarını getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getOgrenciAktifAtamalari(Long ogrenciId) {
        return atamaRepository.findAktifAtamalarByOgrenciId(ogrenciId);
    }

    /**
     * Programın atamalarını getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getProgramAtamalari(Long programId) {
        return atamaRepository.findByProgramId(programId);
    }

    /**
     * Programın aktif atamalarını getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getProgramAktifAtamalari(Long programId) {
        return atamaRepository.findAktifAtamalarByProgramId(programId);
    }

    /**
     * Tüm aktif atamaları getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getTumAktifAtamalar() {
        return atamaRepository.findAktifAtamalar();
    }

    // Kontrol İşlemleri

    /**
     * Öğrenci programa atanmış mı kontrol et
     */
    public boolean isOgrenciProgramaAtanmis(Long ogrenciId, Long programId) {
        return atamaRepository.existsByOgrenciIdAndProgramId(ogrenciId, programId);
    }

    /**
     * Öğrencinin aktif atama sayısı
     */
    public long getOgrenciAktifAtamaSayisi(Long ogrenciId) {
        return atamaRepository.countAktifAtamalarByOgrenciId(ogrenciId);
    }

    /**
     * Programın aktif atama sayısı
     */
    public long getProgramAktifAtamaSayisi(Long programId) {
        return atamaRepository.countAktifAtamalarByProgramId(programId);
    }

    // İstatistik İşlemleri

    /**
     * Toplam atama sayısı
     */
    public long getTotalAtamaSayisi() {
        return atamaRepository.count();
    }

    /**
     * Aktif atama sayısı
     */
    public long getAktifAtamaSayisi() {
        return atamaRepository.countAktifAtamalar();
    }

    /**
     * Durum bazlı istatistikler
     */
    public List<Object[]> getDurumIstatistikleri() {
        return atamaRepository.getDurumIstatistikleri();
    }

    // Arama İşlemleri

    /**
     * Genel arama
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> genelArama(String arama) {
        return atamaRepository.findByGenelArama(arama);
    }

    /**
     * Bugün atananları getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getBugunAtananlar() {
        try {
            List<OgrenciProgramAtama> atamalar = atamaRepository.findBugunAtananlar();
            // Lazy loading için entity'leri initialize et
            for (OgrenciProgramAtama atama : atamalar) {
                if (atama.getOgrenci() != null) {
                    atama.getOgrenci().getAdSoyad(); // Lazy loading'i tetikle
                }
                if (atama.getProgram() != null) {
                    atama.getProgram().getAd(); // Lazy loading'i tetikle
                }
            }
            return atamalar;
        } catch (Exception e) {
            logger.error("Bugün atananları getirme hatası: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Program ID'ye göre atamaları getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> findByProgramId(Long programId) {
        try {
            List<OgrenciProgramAtama> atamalar = atamaRepository.findByProgramId(programId);
            // Lazy loading için entity'leri initialize et
            for (OgrenciProgramAtama atama : atamalar) {
                if (atama.getOgrenci() != null) {
                    atama.getOgrenci().getAdSoyad(); // Lazy loading'i tetikle
                }
                if (atama.getProgram() != null) {
                    atama.getProgram().getAd(); // Lazy loading'i tetikle
                }
            }
            return atamalar;
        } catch (Exception e) {
            logger.error("Program ID'ye göre atamaları getirme hatası: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Öğrenci ID'ye göre atamaları getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> findByOgrenciId(Long ogrenciId) {
        try {
            List<OgrenciProgramAtama> atamalar = atamaRepository.findByOgrenciId(ogrenciId);
            // Lazy loading için entity'leri initialize et
            for (OgrenciProgramAtama atama : atamalar) {
                if (atama.getOgrenci() != null) {
                    atama.getOgrenci().getAdSoyad(); // Lazy loading'i tetikle
                }
                if (atama.getProgram() != null) {
                    atama.getProgram().getAd(); // Lazy loading'i tetikle
                }
            }
            return atamalar;
        } catch (Exception e) {
            logger.error("Öğrenci ID'ye göre atamaları getirme hatası: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Son N gün içinde atananları getir
     */
    @Transactional(readOnly = true)
    public List<OgrenciProgramAtama> getSonGunlerdeAtananlar(int gunSayisi) {
        try {
            LocalDateTime baslangic = LocalDateTime.now().minusDays(gunSayisi);
            List<OgrenciProgramAtama> atamalar = atamaRepository.findSonGunlerdeAtananlar(baslangic);
            // Lazy loading için entity'leri initialize et
            for (OgrenciProgramAtama atama : atamalar) {
                if (atama.getOgrenci() != null) {
                    atama.getOgrenci().getAdSoyad(); // Lazy loading'i tetikle
                }
                if (atama.getProgram() != null) {
                    atama.getProgram().getAd(); // Lazy loading'i tetikle
                }
            }
            return atamalar;
        } catch (Exception e) {
            logger.error("Son günlerde atananları getirme hatası: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
