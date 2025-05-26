package com.rehberhoca.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.OgrenciProgram;
import com.rehberhoca.entity.Program;
import com.rehberhoca.repository.OgrenciRepository;
import com.rehberhoca.repository.OgrenciProgramRepository;
import com.rehberhoca.repository.ProgramRepository;

@Service
@Transactional
public class OgrenciService {

    private static final Logger logger = LoggerFactory.getLogger(OgrenciService.class);

    @Autowired
    private OgrenciRepository ogrenciRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OgrenciProgramRepository ogrenciProgramRepository;

    // Tüm öğrencileri getir
    public List<Ogrenci> tumOgrencileriGetir() {
        return ogrenciRepository.findAll();
    }

    // Öğrenci kaydet
    public Ogrenci ogrenciKaydet(Ogrenci ogrenci) {
        // Email kontrolü
        if (ogrenciRepository.findByEmail(ogrenci.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor!");
        }
        return ogrenciRepository.save(ogrenci);
    }

    // Öğrenci güncelle
    public Ogrenci ogrenciGuncelle(Ogrenci ogrenci) {
        return ogrenciRepository.save(ogrenci);
    }

    // Öğrenci sil
    public void ogrenciSil(Long id) {
        Ogrenci ogrenci = ogrenciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı!"));
        ogrenciRepository.delete(ogrenci);
    }

    // ID ile öğrenci getir
    public Ogrenci ogrenciGetir(Long id) {
        return ogrenciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı!"));
    }

    // findById metodu
    public java.util.Optional<Ogrenci> findById(Long id) {
        return ogrenciRepository.findById(id);
    }

    // Ad soyad ile arama
    public List<Ogrenci> ogrenciAra(String adSoyad) {
        return ogrenciRepository.findByAdSoyadContainingIgnoreCase(adSoyad);
    }

    // Öğrenciyi programa kaydet
    public void ogrenciyiProgramaKaydet(Long ogrenciId, Long programId) {
        try {
            // Önce çift kayıt kontrolü yap
            if (ogrenciProgramRepository.existsByOgrenciIdAndProgramId(ogrenciId, programId)) {
                throw new RuntimeException("Bu öğrenci zaten bu programa kayıtlı!");
            }

            // Öğrenci ve program varlığını kontrol et
            Ogrenci ogrenci = ogrenciGetir(ogrenciId);
            Program program = programRepository.findById(programId)
                    .orElseThrow(() -> new RuntimeException("Program bulunamadı!"));

            // OgrenciProgram entity'si oluştur ve kaydet
            OgrenciProgram ogrenciProgram = new OgrenciProgram(ogrenci, program, "Aktif", "Sistem tarafından atandı");
            ogrenciProgramRepository.save(ogrenciProgram);

        } catch (Exception e) {
            logger.error("Öğrenci programa kaydetme hatası: {}", e.getMessage());
            throw new RuntimeException("Atama işlemi başarısız: " + e.getMessage());
        }
    }

    // Öğrenciyi programdan çıkar
    public void ogrenciyiProgramdanCikar(Long ogrenciId, Long programId) {
        Ogrenci ogrenci = ogrenciGetir(ogrenciId);
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program bulunamadı!"));

        ogrenci.getProgramlar().remove(program);
        ogrenciRepository.save(ogrenci);
    }

    @Transactional(readOnly = true)
    public List<Ogrenci> tumOgrencileriProgramlariIle() {
        List<Ogrenci> ogrenciler = ogrenciRepository.findAll();
        // Lazy loading'i zorla - Hibernate session açıkken programları yükle
        for (Ogrenci ogrenci : ogrenciler) {
            ogrenci.getProgramlar().size(); // Bu satır koleksiyonu yükler
        }
        return ogrenciler;
    }

    /**
     * Belirtilen programa kayıtlı öğrenci sayısını döndürür
     * @param programId Program ID'si
     * @return Kayıtlı öğrenci sayısı
     */
    @Transactional(readOnly = true)
    public int programaKayitliOgrenciSayisi(Long programId) {
        try {
            // Many-to-Many ilişkisi üzerinden sorgu
            String jpql = "SELECT COUNT(o) FROM Ogrenci o JOIN o.programlar p WHERE p.id = :programId";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("programId", programId);
            Long result = query.getSingleResult();
            return result != null ? result.intValue() : 0;

        } catch (Exception e) {
            logger.error("Program {} için öğrenci sayısı alınırken hata: {}", programId, e.getMessage());
            return 0; // Hata durumunda 0 döndür
        }
    }

    /**
     * Öğrencinin kayıtlı olduğu programları getir
     * @param ogrenciId Öğrenci ID'si
     * @return Program listesi
     */
    @Transactional(readOnly = true)
    public List<Program> ogrencininProgramlari(Long ogrenciId) {
        try {
            Ogrenci ogrenci = ogrenciGetir(ogrenciId);
            // Lazy loading'i zorla
            ogrenci.getProgramlar().size();
            return new ArrayList<>(ogrenci.getProgramlar());
        } catch (Exception e) {
            logger.error("Öğrenci {} için programlar alınırken hata: {}", ogrenciId, e.getMessage());
            return List.of(); // Boş liste döndür
        }
    }

    /**
     * Programa kayıtlı öğrencileri getir
     * @param programId Program ID'si
     * @return Öğrenci listesi
     */
    @Transactional(readOnly = true)
    public List<Ogrenci> programinOgrencileri(Long programId) {
        try {
            String jpql = "SELECT o FROM Ogrenci o JOIN o.programlar p WHERE p.id = :programId ORDER BY o.adSoyad";
            TypedQuery<Ogrenci> query = entityManager.createQuery(jpql, Ogrenci.class);
            query.setParameter("programId", programId);
            return query.getResultList();

        } catch (Exception e) {
            logger.error("Program {} için öğrenciler alınırken hata: {}", programId, e.getMessage());
            return List.of(); // Boş liste döndür
        }
    }

    /**
     * E-posta adresine sahip öğrenci sayısını döndürür
     * @return E-posta adresli öğrenci sayısı
     */
    @Transactional(readOnly = true)
    public long emailSahipOgrenciSayisi() {
        try {
            String jpql = "SELECT COUNT(o) FROM Ogrenci o WHERE o.email IS NOT NULL AND o.email != ''";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("E-posta sahip öğrenci sayısı alınırken hata: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Telefon numarasına sahip öğrenci sayısını döndürür
     * @return Telefon numaralı öğrenci sayısı
     */
    @Transactional(readOnly = true)
    public long telefonSahipOgrenciSayisi() {
        try {
            String jpql = "SELECT COUNT(o) FROM Ogrenci o WHERE o.telefon IS NOT NULL AND o.telefon != ''";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Telefon sahip öğrenci sayısı alınırken hata: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Son N gün içinde kayıt olan öğrenci sayısını döndürür
     * @param days Gün sayısı
     * @return Son N günde kayıt olan öğrenci sayısı
     */
    @Transactional(readOnly = true)
    public long sonGunlerdeKayitOlanOgrenciSayisi(int days) {
        try {
            String jpql = "SELECT COUNT(o) FROM Ogrenci o WHERE o.kayitTarihi >= :tarih";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("tarih", java.time.LocalDateTime.now().minusDays(days));
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Son {} günde kayıt olan öğrenci sayısı alınırken hata: {}", days, e.getMessage());
            return 0;
        }
    }

    /**
     * Gelişmiş öğrenci arama (ad, email, telefon)
     * @param searchText Arama metni
     * @return Bulunan öğrenciler
     */
    @Transactional(readOnly = true)
    public List<Ogrenci> gelismisOgrenciArama(String searchText) {
        try {
            String jpql = "SELECT o FROM Ogrenci o WHERE " +
                         "LOWER(o.adSoyad) LIKE LOWER(:searchText) OR " +
                         "LOWER(o.email) LIKE LOWER(:searchText) OR " +
                         "o.telefon LIKE :searchText " +
                         "ORDER BY o.adSoyad";

            TypedQuery<Ogrenci> query = entityManager.createQuery(jpql, Ogrenci.class);
            query.setParameter("searchText", "%" + searchText + "%");
            return query.getResultList();

        } catch (Exception e) {
            logger.error("Gelişmiş öğrenci aramasında hata: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Programa kayıtlı olmayan öğrencileri getir
     * @param programId Program ID'si
     * @return Programa kayıtlı olmayan öğrenciler
     */
    @Transactional(readOnly = true)
    public List<Ogrenci> programaKayitliOlmayanOgrenciler(Long programId) {
        try {
            String jpql = "SELECT o FROM Ogrenci o WHERE o.id NOT IN " +
                         "(SELECT o2.id FROM Ogrenci o2 JOIN o2.programlar p WHERE p.id = :programId) " +
                         "ORDER BY o.adSoyad";

            TypedQuery<Ogrenci> query = entityManager.createQuery(jpql, Ogrenci.class);
            query.setParameter("programId", programId);
            return query.getResultList();

        } catch (Exception e) {
            logger.error("Programa kayıtlı olmayan öğrenciler alınırken hata: {}", e.getMessage());
            return List.of();
        }
    }

    // İstatistik metodları
    public long getTotalCount() {
        return ogrenciRepository.count();
    }

    public long getAktifOgrenciSayisi() {
        return ogrenciRepository.countByAktifTrue();
    }

    // Tarih bazlı metodlar
    public List<Ogrenci> findByKayitTarihiBetween(java.time.LocalDateTime baslangic, java.time.LocalDateTime bitis) {
        return ogrenciRepository.findByKayitTarihiBetween(baslangic, bitis);
    }

    public List<Ogrenci> getBugunKayitOlanlar() {
        return ogrenciRepository.findBugunKayitOlanlar();
    }

    public List<Ogrenci> getBuHaftaKayitOlanlar() {
        return ogrenciRepository.findBuHaftaKayitOlanlar();
    }

    public List<Ogrenci> getBuAyKayitOlanlar() {
        return ogrenciRepository.findBuAyKayitOlanlar();
    }
}