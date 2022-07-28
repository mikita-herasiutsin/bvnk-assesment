package com.coindirect.recruitment.service;

import com.coindirect.recruitment.AlreadyBookedException;
import com.coindirect.recruitment.model.BookingDbo;
import com.coindirect.recruitment.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;

    public BookingDbo create(BookingDbo model) {
        try {
            return repository.save(model);
        } catch (DataIntegrityViolationException ex) {
            if ((ex.getCause() instanceof ConstraintViolationException)) {
                throw new AlreadyBookedException();
            }
            throw ex;
        }
    }

    public Optional<BookingDbo> getByPosition(String row, String column) {
        return repository.findByRowAndColumn(row, column);
    }

    public Optional<BookingDbo> getById(String id) {
        return repository.findById(UUID.fromString(id));
    }

    public boolean isAvailable(String row, String column) {
        return repository.findByRowAndColumn(row, column)
                         .isEmpty();
    }

}
