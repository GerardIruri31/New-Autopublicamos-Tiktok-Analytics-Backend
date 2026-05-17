package com.example.sbazureappdemo.bookGraphs.service;


import com.example.sbazureappdemo.bookGraphs.dto.BookGraphsRequestDTO;
import com.example.sbazureappdemo.bookGraphs.dto.EfectividadBookMetaDTO;
import com.example.sbazureappdemo.bookGraphs.dto.RegistroMesLibroDTO;
import com.example.sbazureappdemo.bookGraphs.repository.BookGraphsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookGraphsService {
    Logger logger = LoggerFactory.getLogger(BookGraphsService.class);
    private final BookGraphsRepository bookGraphsRepository;

    public List<RegistroMesLibroDTO> booksPerMonth(BookGraphsRequestDTO filtros) {
        List<String> libros = filtros.getBookList();
        if (libros == null || libros.isEmpty()) {
            throw new IllegalArgumentException("La lista de libros no puede estar vacía.");
        }
        logger.info("Inicio del proceso data books per month");
        return bookGraphsRepository.dataBooksPerMonth(filtros);
    }

    public List<EfectividadBookMetaDTO> effectBooksPerMonth(BookGraphsRequestDTO filtros) {
        List<String> libros = filtros.getBookList();
        if (libros == null || libros.isEmpty()) {
            throw new IllegalArgumentException("La lista de libros no puede estar vacía.");
        }
        logger.info("Inicio del proceso efectividad books per month");
        return bookGraphsRepository.effectivenessBooksPerMonth(filtros);
    }
}
