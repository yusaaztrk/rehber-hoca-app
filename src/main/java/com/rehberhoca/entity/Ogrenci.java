package com.rehberhoca.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ogrenciler")
public class Ogrenci {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ad", nullable = false, length = 100)
    private String ad;
    
    @Column(name = "soyad", nullable = false, length = 100)
    private String soyad;
    
    @Column(name = "ad_soyad", nullable = false, length = 255)
    private String adSoyad;
    
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;
    
    @Column(name = "telefon", length = 20)
    private String telefon;
    
    @Column(name = "kayit_tarihi", nullable = false)
    private LocalDateTime kayitTarihi;
    
    @Column(name = "aktif")
    private Boolean aktif = true;
    
    // Many-to-Many ilişkisi - Öğrenci tarafı
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "ogrenci_program",
        joinColumns = @JoinColumn(name = "ogrenci_id"),
        inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private Set<Program> programlar = new HashSet<>();
    
    // Constructors
    public Ogrenci() {
        this.kayitTarihi = LocalDateTime.now();
        this.aktif = true;
    }
    
    public Ogrenci(String ad, String soyad, String email) {
        this();
        this.ad = ad;
        this.soyad = soyad;
        this.adSoyad = ad + " " + soyad;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAd() {
        return ad;
    }
    
    public void setAd(String ad) {
        this.ad = ad;
        updateAdSoyad();
    }
    
    public String getSoyad() {
        return soyad;
    }
    
    public void setSoyad(String soyad) {
        this.soyad = soyad;
        updateAdSoyad();
    }
    
    public String getAdSoyad() {
        return adSoyad;
    }
    
    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefon() {
        return telefon;
    }
    
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
    public LocalDateTime getKayitTarihi() {
        return kayitTarihi;
    }
    
    public void setKayitTarihi(LocalDateTime kayitTarihi) {
        this.kayitTarihi = kayitTarihi;
    }
    
    public Boolean getAktif() {
        return aktif;
    }
    
    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }
    
    public Set<Program> getProgramlar() {
        return programlar;
    }
    
    public void setProgramlar(Set<Program> programlar) {
        this.programlar = programlar;
    }
    
    // Utility methods
    public void addProgram(Program program) {
        this.programlar.add(program);
        program.getOgrenciler().add(this);
    }
    
    public void removeProgram(Program program) {
        this.programlar.remove(program);
        program.getOgrenciler().remove(this);
    }
    
    private void updateAdSoyad() {
        if (ad != null && soyad != null) {
            this.adSoyad = ad + " " + soyad;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ogrenci)) return false;
        Ogrenci ogrenci = (Ogrenci) o;
        return id != null && id.equals(ogrenci.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Ogrenci{" +
                "id=" + id +
                ", adSoyad='" + adSoyad + '\'' +
                ", email='" + email + '\'' +
                ", aktif=" + aktif +
                '}';
    }
}