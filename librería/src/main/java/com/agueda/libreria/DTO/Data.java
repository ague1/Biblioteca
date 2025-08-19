package com.agueda.libreria.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record Data(List<BookInformation> results) {
}
