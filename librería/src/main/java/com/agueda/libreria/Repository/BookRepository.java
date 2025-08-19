package com.agueda.libreria.Repository;

import com.agueda.libreria.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Integer> {
}
