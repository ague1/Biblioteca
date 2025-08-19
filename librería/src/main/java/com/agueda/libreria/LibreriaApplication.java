package com.agueda.libreria;

import com.agueda.libreria.Repository.BookRepository;
import com.agueda.libreria.principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibreriaApplication implements CommandLineRunner {


	@Autowired

	private BookRepository bookRepository;
	public static void main(String[] args) {


		SpringApplication.run(LibreriaApplication.class, args);
	}

	public void run(String... args) throws Exception {
		Principal principal = new Principal(bookRepository);
		principal.muestraElMenu();
	}

}
