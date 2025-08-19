package com.agueda.libreria.model;

import com.agueda.libreria.DTO.BookInformation;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")

public class Book {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    private Double numeroDeDescargas;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id")
    private List<Autor> autores;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private List<String> idiomas;


    public Book() {}

    public Book(BookInformation libroInfo) {
        this.id = libroInfo.id();
        this.titulo = libroInfo.titulo();
        this.numeroDeDescargas = libroInfo.numeroDeDescargas();

        // Convertir AuthorInformation a Autor
        this.autores = libroInfo.autor().stream()
                .map(a -> {
                    Integer birthYear = a.fechaDeNacimiento().isEmpty() ? null : Integer.parseInt(a.fechaDeNacimiento());
                    Integer deathYear = a.fechaDeFallecimiento().isEmpty() ? null : Integer.parseInt(a.fechaDeFallecimiento());
                    return new Autor(a.nombre(), birthYear, deathYear);
                })
                .toList();

        this.idiomas = libroInfo.idiomas();
    }

    public Book(Integer id, String titulo, Double numeroDeDescargas, List<Autor> autores, List<String> idiomas) {
        this.id = id;
        this.titulo = titulo;
        this.numeroDeDescargas = numeroDeDescargas;
        this.autores = autores;
        this.idiomas = idiomas;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Double getNumeroDeDescargas() { return numeroDeDescargas; }
    public void setNumeroDeDescargas(Double numeroDeDescargas) { this.numeroDeDescargas = numeroDeDescargas; }

    public List<Autor> getAutores() { return autores; }
    public void setAutores(List<Autor> autores) { this.autores = autores; }

    public List<String> getIdiomas() { return idiomas; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
}
