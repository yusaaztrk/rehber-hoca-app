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

import com.rehberhoca.entity.OgrenciProgramAtama;
import com.rehberhoca.entity.OgrenciProgramAtama.AtamaDurum;

/**
 * Öğrenci-Program Atama Repository
 */
@Repository
public interface OgrenciProgramAtamaRepository extends JpaRepository<OgrenciProgramAtama, Long> {

    // Temel sorgular
    List<OgrenciProgramAtama> findByOgrenciId(Long ogrenciId);
    List<OgrenciProgramAtama> findByProgramId(Long programId);
    Optional<OgrenciProgramAtama> findByOgrenciIdAndProgramId(Long ogrenciId, Long programId);
    boolean existsByOgrenciIdAndProgramId(Long ogrenciId, Long programId);

    // Durum bazlı sorgular
    List<OgrenciProgramAtama> findByDurum(AtamaDurum durum);
    List<OgrenciProgramAtama> findByOgrenciIdAndDurum(Long ogrenciId, AtamaDurum durum);
    List<OgrenciProgramAtama> findByProgramIdAndDurum(Long programId, AtamaDurum durum);

    // Aktif atamalar
    @Query("SELECT a FROM OgrenciProgramAtama a WHERE a.durum = 'Aktif'")
    List<OgrenciProgramAtama> findAktifAtamalar();

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE a.ogrenci.id = :ogrenciId AND a.durum = 'Aktif'")
    List<OgrenciProgramAtama> findAktifAtamalarByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE a.program.id = :programId AND a.durum = 'Aktif'")
    List<OgrenciProgramAtama> findAktifAtamalarByProgramId(@Param("programId") Long programId);

    // Sayma işlemleri
    long countByOgrenciId(Long ogrenciId);
    long countByProgramId(Long programId);
    long countByDurum(AtamaDurum durum);
    long countByOgrenciIdAndDurum(Long ogrenciId, AtamaDurum durum);
    long countByProgramIdAndDurum(Long programId, AtamaDurum durum);

    // Özel sayma sorguları
    @Query("SELECT COUNT(a) FROM OgrenciProgramAtama a WHERE a.durum = 'Aktif'")
    long countAktifAtamalar();

    @Query("SELECT COUNT(a) FROM OgrenciProgramAtama a WHERE a.ogrenci.id = :ogrenciId AND a.durum = 'Aktif'")
    long countAktifAtamalarByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    @Query("SELECT COUNT(a) FROM OgrenciProgramAtama a WHERE a.program.id = :programId AND a.durum = 'Aktif'")
    long countAktifAtamalarByProgramId(@Param("programId") Long programId);

    // Tarih bazlı sorgular
    List<OgrenciProgramAtama> findByAtamaTarihiBetween(LocalDateTime baslangic, LocalDateTime bitis);

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE DATE(a.atamaTarihi) = CURRENT_DATE")
    List<OgrenciProgramAtama> findBugunAtananlar();

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE a.atamaTarihi >= :tarih")
    List<OgrenciProgramAtama> findSonGunlerdeAtananlar(@Param("tarih") LocalDateTime tarih);

    // Arama işlemleri
    @Query("SELECT a FROM OgrenciProgramAtama a WHERE " +
           "LOWER(a.ogrenci.adSoyad) LIKE LOWER(CONCAT('%', :arama, '%')) OR " +
           "LOWER(a.program.ad) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgramAtama> findByGenelArama(@Param("arama") String arama);

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE " +
           "LOWER(a.ogrenci.adSoyad) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgramAtama> findByOgrenciAdContaining(@Param("arama") String arama);

    @Query("SELECT a FROM OgrenciProgramAtama a WHERE " +
           "LOWER(a.program.ad) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<OgrenciProgramAtama> findByProgramAdContaining(@Param("arama") String arama);

    // Güncelleme işlemleri
    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgramAtama a SET a.durum = :yeniDurum, a.guncellemeTarihi = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateDurum(@Param("id") Long id, @Param("yeniDurum") AtamaDurum yeniDurum);

    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgramAtama a SET a.durum = :yeniDurum, a.guncellemeTarihi = CURRENT_TIMESTAMP WHERE a.ogrenci.id = :ogrenciId")
    int updateDurumByOgrenciId(@Param("ogrenciId") Long ogrenciId, @Param("yeniDurum") AtamaDurum yeniDurum);

    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgramAtama a SET a.durum = :yeniDurum, a.guncellemeTarihi = CURRENT_TIMESTAMP WHERE a.program.id = :programId")
    int updateDurumByProgramId(@Param("programId") Long programId, @Param("yeniDurum") AtamaDurum yeniDurum);

    @Modifying
    @Transactional
    @Query("UPDATE OgrenciProgramAtama a SET a.notlar = :notlar, a.guncellemeTarihi = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateNotlar(@Param("id") Long id, @Param("notlar") String notlar);

    // Silme işlemleri
    @Modifying
    @Transactional
    void deleteByOgrenciId(Long ogrenciId);

    @Modifying
    @Transactional
    void deleteByProgramId(Long programId);

    @Modifying
    @Transactional
    void deleteByOgrenciIdAndProgramId(Long ogrenciId, Long programId);

    // İstatistik sorguları
    @Query("SELECT a.durum, COUNT(a) FROM OgrenciProgramAtama a GROUP BY a.durum")
    List<Object[]> getDurumIstatistikleri();

    @Query("SELECT DATE(a.atamaTarihi), COUNT(a) FROM OgrenciProgramAtama a " +
           "WHERE a.atamaTarihi >= :baslangic GROUP BY DATE(a.atamaTarihi) ORDER BY DATE(a.atamaTarihi)")
    List<Object[]> getGunlukAtamaIstatistikleri(@Param("baslangic") LocalDateTime baslangic);
}
