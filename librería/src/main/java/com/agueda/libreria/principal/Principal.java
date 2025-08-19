package com.agueda.libreria.principal;

import com.agueda.libreria.Repository.BookRepository;
import com.agueda.libreria.DTO.AuthorInformation;
import com.agueda.libreria.model.Book;
import com.agueda.libreria.DTO.BookInformation;
import com.agueda.libreria.DTO.Data;
import com.agueda.libreria.service.ConversionData;
import com.agueda.libreria.service.UsageAPI;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private UsageAPI usageAPI = new UsageAPI();
    private ConversionData converter = new ConversionData();
    private Scanner teclado = new Scanner(System.in);

    private List<BookInformation> librosRegistrados = new ArrayList<>();

    private BookRepository repositorio;

    public Principal(BookRepository bookRepository) {
        this.repositorio= bookRepository;
    }

    public void muestraElMenu() {
        boolean salir = false;

        while (!salir) {
            System.out.println("""
                    ===== MENÚ LIBRERÍA =====
                     1. Buscar libro por Titulo
                     2. Listar libros Registrados
                     3. Listar Autores Registrados
                     4. Listar Autores vivos en un determinado Año
                     5. Listar libros por idioma
                     6. Listar libros con mas Numero de Descargas
                     0. Salir
                     Seleccione una opción:""");

            String opcion = teclado.nextLine();

            switch (opcion) {
                case "1":
                    buscarRegistrarLibro();
                    break;
                case "2":
                    listarLibrosRegistrados();
                    break;
                case "3":
                    listarAutores(librosRegistrados);
                    break;
                case "4":
                    listarAutoresAno();
                    break;
                case "5":
                    listarLibrosIdioma(librosRegistrados);
                    break;
                case "6":
                    listaDescargas(librosRegistrados);
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }


    private void buscarRegistrarLibro() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String tituloLibro = teclado.nextLine();

        var json = usageAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = converter.obtenerDatos(json, Data.class);

        Optional<BookInformation> libroBuscado = datosBusqueda.results().stream()
                .filter(l -> l.titulo().toLowerCase().contains(tituloLibro.toLowerCase()))
                .findFirst();


        if (libroBuscado.isPresent()) {
            BookInformation libro = libroBuscado.get();
            System.out.println(libro);

            // Verifica si ya está registrado
            boolean existe = librosRegistrados.stream()
                    .anyMatch(l -> l.titulo().equalsIgnoreCase(libro.titulo()));

            if (!existe) {
                Book book = new Book(libro);
                repositorio.save(book);

                System.out.println("Libro registrado correctamente: " + libro.titulo());
            } else {
                System.out.println("El libro ya estaba registrado: " + libro.titulo());
            }

        } else {
            System.out.println("Libro no encontrado");
        }
    }


    private void listarLibrosRegistrados() {
        System.out.println("=== Libros Registrados ===");
        if (librosRegistrados.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
        } else {
            for (BookInformation libro : librosRegistrados) {
                System.out.println("• " + libro + "\n");
            }
        }
    }


    public void listarAutores(List<BookInformation> libros) {
        Map<AuthorInformation, List<String>> autoresConLibros = libros.stream()
                .flatMap(libro -> libro.autor().stream()
                        .map(autor -> new AbstractMap.SimpleEntry<>(autor, libro.titulo())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        if (autoresConLibros.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        System.out.println("Autores registrados:");
        autoresConLibros.forEach((autor, titulos) -> {
            System.out.println(autor.toString());
            System.out.println("Libros:" + String.join(", ", titulos) + "\n");
        });

    }

    private void listarAutoresAno() {
        System.out.println("Ingrese el año para verificar autores vivos:");
        int ano = Integer.parseInt(teclado.nextLine().trim());

        var autoresVivos = converter.obtenerDatos(usageAPI.obtenerDatos(URL_BASE + "?search="), Data.class)
                .results().stream()
                .flatMap(libro -> libro.autor().stream()
                        .map(a -> new AbstractMap.SimpleEntry<>(a, libro.titulo())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet().stream()
                .filter(e -> {
                    AuthorInformation a = e.getKey();
                    int n = Integer.parseInt(a.fechaDeNacimiento());
                    int f = a.fechaDeFallecimiento().isEmpty() ? Integer.MAX_VALUE
                            : Integer.parseInt(a.fechaDeFallecimiento());
                    return n <= ano && ano <= f;
                })
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("No existe ningún autor vivo en el año " + ano);
            return;
        }

        autoresVivos.forEach(e -> System.out.println(e.getKey().toString().replace("\n", "\n  ")
                + "\n  Libros: " + String.join(", ", e.getValue()) + "\n"));
    }

    private void listarLibrosIdioma(List<BookInformation> libros) {
        System.out.println("""
                ==Ingrese el idioma para buscar libros==
                es- Español
                en- Ingles
                fr- Frances:
                pt Portugues""");
        String idioma = teclado.nextLine().trim().toLowerCase();

        // Filtrar libros que contengan el idioma
        List<BookInformation> librosFiltrados = libros.stream()
                .filter(libro -> libro.idiomas() != null &&
                        libro.idiomas().stream().anyMatch(i -> i.equalsIgnoreCase(idioma)))
                .toList();

        if (librosFiltrados.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
            return;
        }

        System.out.println("Libros en idioma '" + idioma + "':");
        librosFiltrados.forEach(libro -> System.out.println(libro.toString()));
    }

    private void listaDescargas(List<BookInformation> libros) {
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        System.out.println("Libros ordenados por número de descargas (mayor a menor):");

        libros.stream()
                .sorted(Comparator.comparingDouble(BookInformation::numeroDeDescargas).reversed())
                .forEach(System.out::println);
    }

}
