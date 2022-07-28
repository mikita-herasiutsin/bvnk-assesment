package com.coindirect.recruitment.repository;

import com.coindirect.recruitment.model.BookingDbo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingDbo, UUID> {

    Optional<BookingDbo> findByRowAndColumn(String row, String column);

}
