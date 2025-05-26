package com.rehberhoca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rehberhoca.entity.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    // Program adı ile arama (case insensitive)
    List<Program> findByAdContainingIgnoreCase(String ad);

    // Kategori ile arama
    List<Program> findByKategori(String kategori);

    // Seviye ile arama
    List<Program> findBySeviye(String seviye);

    // Durum ile arama
    List<Program> findByDurum(String durum);

    // Aktif programlar
    List<Program> findByAktifTrue();

    // Belirli bir öğrencinin programları
    @Query("SELECT p FROM Program p JOIN p.ogrenciler o WHERE o.id = :ogrenciId")
    List<Program> findByOgrenciId(@Param("ogrenciId") Long ogrenciId);

    // Kapasitesi dolu olmayan programlar
    @Query("SELECT p FROM Program p WHERE p.kapasite IS NULL OR SIZE(p.ogrenciler) < p.kapasite")
    List<Program> findProgramsWithAvailableCapacity();

    // Süre aralığına göre programlar
    @Query("SELECT p FROM Program p WHERE p.sure BETWEEN :minSure AND :maxSure")
    List<Program> findBySureRange(@Param("minSure") Integer minSure, @Param("maxSure") Integer maxSure);

    // En çok öğrencisi olan programlar
    @Query("SELECT p FROM Program p ORDER BY SIZE(p.ogrenciler) DESC")
    List<Program> findMostPopularPrograms();

    // Öğrencisi olmayan programlar
    @Query("SELECT p FROM Program p WHERE SIZE(p.ogrenciler) = 0")
    List<Program> findProgramsWithoutStudents();

    // Aktif program sayısı
    long countByAktifTrue();
}