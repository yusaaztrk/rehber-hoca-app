package com.rehberhoca.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.OgrenciProgram;
import com.rehberhoca.entity.Program;

/**
 * OgrenciProgram entity'si için repository interface'i
 */
@Repository
public interface OgrenciProgramRepository extends JpaRepository<OgrenciProgram, Long> {

    // Öğrenciye göre programları getir
    List<OgrenciProgram> findByOgrenciId(Long ogrenciId);

    // Programa göre öğrencileri getir
    List<OgrenciProgram> findByProgramId(Long programId);

    // Öğrenci ve programa göre ilişki bul
    Optional<OgrenciProgram> findByOgrenciIdAndProgramId(Long ogrenciId, Long programId);

    // Öğrenci ve program entity'leri ile ilişki bul
    Optional<OgrenciProgram> findByOgrenciAndProgram(Ogrenci ogrenci, Program program);

    // Duruma göre ilişkileri getir
    List<OgrenciProgram> findByDurum(String durum);

    // Aktif ilişkileri getir
    @Query("SELECT op FROM OgrenciProgram op WHERE op.durum = 'Aktif'")
    List<OgrenciProgram> findAktifIliskiler();

    // Tamamlanan ilişkileri getir
    @Query("SELECT op FROM OgrenciProgram op WHERE op.durum = 'Tamamlandı' OR op.durum = 'Tamamlandi'")
    List<OgrenciProgram> findTamamlananIliskiler();

    // Öğrencinin aktif programları
    @Query("SELECT op FROM OgrenciProgram op WHERE op.ogrenci.id = :ogrenciId AND op.durum = 'Aktif'")
    List<OgrenciProgram> findAktifProgramlarByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    // Programın aktif öğrencileri
    @Query("SELECT op FROM OgrenciProgram op WHERE op.program.id = :programId AND op.durum = 'Aktif'")
    List<OgrenciProgram> findAktifOgrencilerByProgramId(@Param("programId") Long programId);

    // Tarih aralığında kayıt olanlar
    @Query("SELECT op FROM OgrenciProgram op WHERE op.kayitTarihi BETWEEN :baslangic AND :bitis")
    List<OgrenciProgram> findByKayitTarihiBetween(@Param("baslangic") LocalDateTime baslangic,
                                                  @Param("bitis") LocalDateTime bitis);

    // Öğrenci adına göre arama
    @Query("SELECT op FROM OgrenciProgram op WHERE " +
           "LOWER(op.ogrenci.ad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.ogrenci.soyad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.ogrenci.adSoyad) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgram> findByOgrenciAdContaining(@Param("arama") String arama);

    // Program adına göre arama
    @Query("SELECT op FROM OgrenciProgram op WHERE " +
           "LOWER(op.program.ad) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgram> findByProgramAdContaining(@Param("arama") String arama);

    // Genel arama (öğrenci adı, program adı, durum)
    @Query("SELECT op FROM OgrenciProgram op WHERE " +
           "LOWER(op.ogrenci.ad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.ogrenci.soyad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.ogrenci.adSoyad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.program.ad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(op.durum) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgram> findByGenelArama(@Param("arama") String arama);

    // İstatistikler için count sorguları
    @Query("SELECT COUNT(op) FROM OgrenciProgram op WHERE op.durum = 'Aktif'")
    long countAktifIliskiler();

    @Query("SELECT COUNT(op) FROM OgrenciProgram op WHERE op.durum = 'Tamamlandı' OR op.durum = 'Tamamlandi'")
    long countTamamlananIliskiler();

    @Query("SELECT COUNT(op) FROM OgrenciProgram op WHERE op.ogrenci.id = :ogrenciId")
    long countByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    @Query("SELECT COUNT(op) FROM OgrenciProgram op WHERE op.program.id = :programId")
    long countByProgramId(@Param("programId") Long programId);

    // Bugün kayıt olanlar
    @Query("SELECT op FROM OgrenciProgram op WHERE DATE(op.kayitTarihi) = CURRENT_DATE")
    List<OgrenciProgram> findBugunKayitOlanlar();

    // Bu hafta kayıt olanlar
    @Query("SELECT op FROM OgrenciProgram op WHERE WEEK(op.kayitTarihi) = WEEK(CURRENT_DATE) AND YEAR(op.kayitTarihi) = YEAR(CURRENT_DATE)")
    List<OgrenciProgram> findBuHaftaKayitOlanlar();

    // Bu ay kayıt olanlar
    @Query("SELECT op FROM OgrenciProgram op WHERE MONTH(op.kayitTarihi) = MONTH(CURRENT_DATE) AND YEAR(op.kayitTarihi) = YEAR(CURRENT_DATE)")
    List<OgrenciProgram> findBuAyKayitOlanlar();

    // Durum güncelleme
    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgram op SET op.durum = :yeniDurum WHERE op.id = :id")
    int updateDurum(@Param("id") Long id, @Param("yeniDurum") String yeniDurum);

    // Toplu durum güncelleme
    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgram op SET op.durum = :yeniDurum WHERE op.ogrenci.id = :ogrenciId")
    int updateDurumByOgrenciId(@Param("ogrenciId") Long ogrenciId, @Param("yeniDurum") String yeniDurum);

    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgram op SET op.durum = :yeniDurum WHERE op.program.id = :programId")
    int updateDurumByProgramId(@Param("programId") Long programId, @Param("yeniDurum") String yeniDurum);

    // Öğrenci silme öncesi kontrol
    boolean existsByOgrenciId(Long ogrenciId);

    // Program silme öncesi kontrol
    boolean existsByProgramId(Long programId);

    // Çift kayıt kontrolü
    boolean existsByOgrenciIdAndProgramId(Long ogrenciId, Long programId);

    // Öğrencinin program sayısı
    @Query("SELECT COUNT(DISTINCT op.program.id) FROM OgrenciProgram op WHERE op.ogrenci.id = :ogrenciId AND op.durum = 'Aktif'")
    long countAktifProgramlarByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    // Programın öğrenci sayısı
    @Query("SELECT COUNT(DISTINCT op.ogrenci.id) FROM OgrenciProgram op WHERE op.program.id = :programId AND op.durum = 'Aktif'")
    long countAktifOgrencilerByProgramId(@Param("programId") Long programId);
}
