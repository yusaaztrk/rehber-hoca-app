package com.rehberhoca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rehberhoca.entity.Ogrenci;

@Repository
public interface OgrenciRepository extends JpaRepository<Ogrenci, Long> {

    // Email ile öğrenci bul
    Optional<Ogrenci> findByEmail(String email);

    // Ad soyad ile arama (case insensitive)
    List<Ogrenci> findByAdSoyadContainingIgnoreCase(String adSoyad);

    // Email'e göre öğrenci varlığını kontrol et
    boolean existsByEmail(String email);

    // Ad soyad'a göre öğrenci varlığını kontrol et
    boolean existsByAdSoyad(String adSoyad);

    // Telefon numarasına göre öğrenci bul
    List<Ogrenci> findByTelefonContaining(String telefon);

    // Kayıt tarihi aralığına göre öğrencileri bul
    @Query("SELECT o FROM Ogrenci o WHERE o.kayitTarihi BETWEEN :startDate AND :endDate")
    List<Ogrenci> findByKayitTarihiBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                           @Param("endDate") java.time.LocalDateTime endDate);

    // Program ID'sine göre öğrencileri bul
    @Query("SELECT o FROM Ogrenci o JOIN o.programlar p WHERE p.id = :programId")
    List<Ogrenci> findByProgramId(@Param("programId") Long programId);

    // E-posta adresi olan öğrenci sayısı
    @Query("SELECT COUNT(o) FROM Ogrenci o WHERE o.email IS NOT NULL AND o.email != ''")
    long countByEmailNotNull();

    // Telefon numarası olan öğrenci sayısı
    @Query("SELECT COUNT(o) FROM Ogrenci o WHERE o.telefon IS NOT NULL AND o.telefon != ''")
    long countByTelefonNotNull();

    // Aktif öğrenci sayısı
    long countByAktifTrue();

    // Bugün kayıt olanlar
    @Query("SELECT o FROM Ogrenci o WHERE DATE(o.kayitTarihi) = CURRENT_DATE")
    List<Ogrenci> findBugunKayitOlanlar();

    // Bu hafta kayıt olanlar
    @Query("SELECT o FROM Ogrenci o WHERE WEEK(o.kayitTarihi) = WEEK(CURRENT_DATE) AND YEAR(o.kayitTarihi) = YEAR(CURRENT_DATE)")
    List<Ogrenci> findBuHaftaKayitOlanlar();

    // Bu ay kayıt olanlar
    @Query("SELECT o FROM Ogrenci o WHERE MONTH(o.kayitTarihi) = MONTH(CURRENT_DATE) AND YEAR(o.kayitTarihi) = YEAR(CURRENT_DATE)")
    List<Ogrenci> findBuAyKayitOlanlar();
}