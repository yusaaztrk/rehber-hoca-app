package com.rehberhoca.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.OgrenciProgram;
import com.rehberhoca.entity.Program;
import com.rehberhoca.repository.OgrenciProgramRepository;

/**
 * OgrenciProgram entity'si için service sınıfı
 */
@Service
@Transactional
public class OgrenciProgramService {

    @Autowired
    private OgrenciProgramRepository ogrenciProgramRepository;

    @Autowired
    private OgrenciService ogrenciService;

    @Autowired
    private ProgramService programService;

    // CRUD İşlemleri

    public List<OgrenciProgram> findAll() {
        return ogrenciProgramRepository.findAll();
    }

    public Optional<OgrenciProgram> findById(Long id) {
        return ogrenciProgramRepository.findById(id);
    }

    public OgrenciProgram save(OgrenciProgram ogrenciProgram) {
        return ogrenciProgramRepository.save(ogrenciProgram);
    }

    public void deleteById(Long id) {
        ogrenciProgramRepository.deleteById(id);
    }

    public void delete(OgrenciProgram ogrenciProgram) {
        ogrenciProgramRepository.delete(ogrenciProgram);
    }

    // Özel İşlemler

    /**
     * Öğrenciyi programa kaydet
     */
    public OgrenciProgram ogrenciyiProgramaKaydet(Long ogrenciId, Long programId) {
        return ogrenciyiProgramaKaydet(ogrenciId, programId, "Aktif", null);
    }

    /**
     * Öğrenciyi programa kaydet (detaylı)
     */
    @Transactional
    public OgrenciProgram ogrenciyiProgramaKaydet(Long ogrenciId, Long programId, String durum, String notlar) {
        try {
            // Çift kayıt kontrolü
            if (ogrenciProgramRepository.existsByOgrenciIdAndProgramId(ogrenciId, programId)) {
                throw new RuntimeException("Bu öğrenci zaten bu programa kayıtlı!");
            }

            Optional<Ogrenci> ogrenci = ogrenciService.findById(ogrenciId);
            Optional<Program> program = programService.findById(programId);

            if (!ogrenci.isPresent()) {
                throw new RuntimeException("Öğrenci bulunamadı: " + ogrenciId);
            }

            if (!program.isPresent()) {
                throw new RuntimeException("Program bulunamadı: " + programId);
            }

            OgrenciProgram ogrenciProgram = new OgrenciProgram();
            ogrenciProgram.setOgrenci(ogrenci.get());
            ogrenciProgram.setProgram(program.get());
            ogrenciProgram.setDurum(durum != null ? durum : "Aktif");
            ogrenciProgram.setNotlar(notlar);
            ogrenciProgram.setKayitTarihi(LocalDateTime.now());

            return ogrenciProgramRepository.save(ogrenciProgram);

        } catch (Exception e) {
            throw new RuntimeException("Öğrenci programa kayıt edilemedi: " + e.getMessage(), e);
        }
    }

    /**
     * Öğrencinin programlarını getir
     */
    public List<OgrenciProgram> getOgrenciProgramlari(Long ogrenciId) {
        return ogrenciProgramRepository.findByOgrenciId(ogrenciId);
    }

    /**
     * Öğrencinin aktif programlarını getir
     */
    public List<OgrenciProgram> getOgrenciAktifProgramlari(Long ogrenciId) {
        return ogrenciProgramRepository.findAktifProgramlarByOgrenciId(ogrenciId);
    }

    /**
     * Programın öğrencilerini getir
     */
    public List<OgrenciProgram> getProgramOgrencileri(Long programId) {
        return ogrenciProgramRepository.findByProgramId(programId);
    }

    /**
     * Programın aktif öğrencilerini getir
     */
    public List<OgrenciProgram> getProgramAktifOgrencileri(Long programId) {
        return ogrenciProgramRepository.findAktifOgrencilerByProgramId(programId);
    }

    /**
     * Durum güncelle
     */
    public boolean durumGuncelle(Long id, String yeniDurum) {
        return ogrenciProgramRepository.updateDurum(id, yeniDurum) > 0;
    }

    /**
     * Öğrencinin tüm programlarının durumunu güncelle
     */
    public int ogrenciProgramDurumGuncelle(Long ogrenciId, String yeniDurum) {
        return ogrenciProgramRepository.updateDurumByOgrenciId(ogrenciId, yeniDurum);
    }

    /**
     * Programın tüm öğrencilerinin durumunu güncelle
     */
    public int programOgrenciDurumGuncelle(Long programId, String yeniDurum) {
        return ogrenciProgramRepository.updateDurumByProgramId(programId, yeniDurum);
    }

    // Arama İşlemleri

    public List<OgrenciProgram> findByDurum(String durum) {
        return ogrenciProgramRepository.findByDurum(durum);
    }

    public List<OgrenciProgram> findAktifIliskiler() {
        return ogrenciProgramRepository.findAktifIliskiler();
    }

    public List<OgrenciProgram> findTamamlananIliskiler() {
        return ogrenciProgramRepository.findTamamlananIliskiler();
    }

    public List<OgrenciProgram> findByOgrenciAdContaining(String arama) {
        return ogrenciProgramRepository.findByOgrenciAdContaining(arama);
    }

    public List<OgrenciProgram> findByProgramAdContaining(String arama) {
        return ogrenciProgramRepository.findByProgramAdContaining(arama);
    }

    public List<OgrenciProgram> findByGenelArama(String arama) {
        return ogrenciProgramRepository.findByGenelArama(arama);
    }

    public List<OgrenciProgram> findByKayitTarihiBetween(LocalDateTime baslangic, LocalDateTime bitis) {
        return ogrenciProgramRepository.findByKayitTarihiBetween(baslangic, bitis);
    }

    // İstatistik İşlemleri

    public long getTotalCount() {
        return ogrenciProgramRepository.count();
    }

    public long getAktifIliskilerCount() {
        return ogrenciProgramRepository.countAktifIliskiler();
    }

    public long getTamamlananIliskilerCount() {
        return ogrenciProgramRepository.countTamamlananIliskiler();
    }

    public long getOgrenciProgramSayisi(Long ogrenciId) {
        return ogrenciProgramRepository.countByOgrenciId(ogrenciId);
    }

    public long getProgramOgrenciSayisi(Long programId) {
        return ogrenciProgramRepository.countByProgramId(programId);
    }

    public long getOgrenciAktifProgramSayisi(Long ogrenciId) {
        return ogrenciProgramRepository.countAktifProgramlarByOgrenciId(ogrenciId);
    }

    public long getProgramAktifOgrenciSayisi(Long programId) {
        return ogrenciProgramRepository.countAktifOgrencilerByProgramId(programId);
    }

    // Tarih Bazlı Raporlar

    public List<OgrenciProgram> getBugunKayitOlanlar() {
        return ogrenciProgramRepository.findBugunKayitOlanlar();
    }

    public List<OgrenciProgram> getBuHaftaKayitOlanlar() {
        return ogrenciProgramRepository.findBuHaftaKayitOlanlar();
    }

    public List<OgrenciProgram> getBuAyKayitOlanlar() {
        return ogrenciProgramRepository.findBuAyKayitOlanlar();
    }

    // Kontrol İşlemleri

    public boolean isOgrenciProgramaKayitli(Long ogrenciId, Long programId) {
        return ogrenciProgramRepository.existsByOgrenciIdAndProgramId(ogrenciId, programId);
    }

    public boolean canDeleteOgrenci(Long ogrenciId) {
        return !ogrenciProgramRepository.existsByOgrenciId(ogrenciId);
    }

    public boolean canDeleteProgram(Long programId) {
        return !ogrenciProgramRepository.existsByProgramId(programId);
    }

    /**
     * Öğrenciyi programdan çıkar
     */
    public boolean ogrenciyiProgramdanCikar(Long ogrenciId, Long programId) {
        Optional<OgrenciProgram> iliski = ogrenciProgramRepository.findByOgrenciIdAndProgramId(ogrenciId, programId);
        if (iliski.isPresent()) {
            delete(iliski.get());
            return true;
        }
        return false;
    }

    /**
     * Öğrencinin tüm program kayıtlarını sil
     */
    public void ogrenciTumProgramlardanCikar(Long ogrenciId) {
        List<OgrenciProgram> iliskiler = getOgrenciProgramlari(ogrenciId);
        for (OgrenciProgram iliski : iliskiler) {
            delete(iliski);
        }
    }

    /**
     * Programın tüm öğrenci kayıtlarını sil
     */
    public void programTumOgrencileriCikar(Long programId) {
        List<OgrenciProgram> iliskiler = getProgramOgrencileri(programId);
        for (OgrenciProgram iliski : iliskiler) {
            delete(iliski);
        }
    }
}
