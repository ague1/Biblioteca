package com.agueda.libreria.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorInformation(
        @JsonProperty("name") String nombre,
        @JsonProperty("birth_year") String fechaDeNacimiento,
        @JsonProperty("death_year") String fechaDeFallecimiento) {

    @Override
    public String toString() {
        return "• Autor: " + nombre + "\n" +
                "Nacimiento: " + fechaDeNacimiento + "\n" +
                "Fallecimiento: " + fechaDeFallecimiento + "\n";
    }

}
