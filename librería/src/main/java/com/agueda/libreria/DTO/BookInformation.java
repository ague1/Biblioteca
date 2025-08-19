package com.agueda.libreria.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookInformation(
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String titulo,
        @JsonProperty("authors") List<AuthorInformation> autor,
        @JsonProperty("languages") List<String> idiomas,
        @JsonProperty("download_count") Double numeroDeDescargas
) {
    @Override
    public String toString() {
        return "Titulo: " + titulo + "\n" +
                "Autor(es): " + formatearAutores() + "\n" +
                "Idioma(s): " + (idiomas != null && !idiomas.isEmpty() ? String.join(", ", idiomas) : "Desconocido") + "\n" +
                "Número de descargas: " + numeroDeDescargas;
    }


    public String formatearAutores() {
        if (autor == null || autor.isEmpty()) {
            return "Desconocido";
        }
        return autor.stream()
                .map(AuthorInformation::nombre) // usa el toString de AuthorInformation
                .collect(Collectors.joining(", "));
    }
}
