package com.agueda.libreria.service;

public interface IConversionData {
    <T> T obtenerDatos(String json, Class<T> clase);
}
