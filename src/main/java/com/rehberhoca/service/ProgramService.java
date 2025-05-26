package com.rehberhoca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.Program;
import com.rehberhoca.repository.OgrenciRepository;
import com.rehberhoca.repository.ProgramRepository;
import com.rehberhoca.repository.OgrenciProgramRepository;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private OgrenciRepository ogrenciRepository;

    @Autowired
    private OgrenciProgramRepository ogrenciProgramRepository;

    // Tüm programları getir
    public List<Program> tumProgramlariGetir() {
        return programRepository.findAll();
    }

    // Program kaydet
    public Program programKaydet(Program program) {
        return programRepository.save(program);
    }

    // Program güncelle
    public Program programGuncelle(Program program) {
        return programRepository.save(program);
    }

    // Program sil
    @Transactional
    public void programSil(Long id) {
        try {
            Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program bulunamadı!"));

            // Önce OgrenciProgram tablosundaki tüm kayıtları sil
            ogrenciProgramRepository.deleteByProgramId(id);

            // Sonra programı sil
            programRepository.delete(program);

        } catch (Exception e) {
            throw new RuntimeException("Program silme işlemi başarısız: " + e.getMessage());
        }
    }

    // ID ile program getir
    public Program programGetir(Long id) {
        return programRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Program bulunamadı!"));
    }

    // Program adı ile arama
    public List<Program> programAra(String programAdi) {
        return programRepository.findByAdContainingIgnoreCase(programAdi);
    }

    // Öğrencinin programlarını getir
    public List<Program> ogrencininProgramlari(Long ogrenciId) {
        return programRepository.findByOgrenciId(ogrenciId);
    }

    // Programa kayıtlı öğrenciler
    public List<Ogrenci> programinOgrencileri(Long programId) {
        return ogrenciRepository.findByProgramId(programId);
    }

    // İstatistik metodları
    public long getTotalCount() {
        return programRepository.count();
    }

    public long getAktifProgramSayisi() {
        return programRepository.countByAktifTrue();
    }

    // findById metodu
    public java.util.Optional<Program> findById(Long id) {
        return programRepository.findById(id);
    }
}