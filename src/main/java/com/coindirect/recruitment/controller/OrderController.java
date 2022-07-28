package com.coindirect.recruitment.controller;

import com.coindirect.recruitment.dto.AvailabilityDto;
import com.coindirect.recruitment.dto.BookingDto;
import com.coindirect.recruitment.dto.RequestBookingDto;
import com.coindirect.recruitment.model.BookingDbo;
import com.coindirect.recruitment.model.BookingDboMapper;
import com.coindirect.recruitment.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

/**
 * Controller for handling bookings for a bing hall.
 * The hall can be imagined as a grid of rows and columns
 */
@RestController("orders")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final BookingDboMapper MAPPER = BookingDboMapper.INSTANCE;
    private final BookingService service;

    /**
     * Creates a booking with the requested details.
     * If unavailable returns a 200 with error message.
     *
     * @param requestBooking the requested booking details.
     * @return on success booking details. on failure error message.
     */
    @PostMapping("create")
    public ResponseEntity<BookingDto> createBooking(@RequestBody RequestBookingDto requestBooking) {
        BookingDbo result = service.create(MAPPER.toDbo(requestBooking));
        return ResponseEntity.ok(MAPPER.toDto(result));
    }

    /**
     * query a booking by grid position
     *
     * @param row    grid position row
     * @param column grid position column
     * @return the booking details. 400 if not found
     */
    @GetMapping("getByPosition/{row}/{column}")
    public ResponseEntity<BookingDto> getBookingByPosition(@PathVariable String row, @PathVariable String column) {
        return getOptional(service.getByPosition(row, column));
    }

    /**
     * query by booking id
     *
     * @param bookingId booking id
     * @return the booking details. 400 if not found
     */
    @GetMapping("getByBookingId/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable String bookingId) {
        return getOptional(service.getById(bookingId));
    }

    private ResponseEntity<BookingDto> getOptional(Optional<BookingDbo> service) {
        if (service.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(MAPPER.toDto(service.get()));
    }

    /**
     * Query if a cell is available
     *
     * @param row    grid position row
     * @param column grid position column
     * @return true if cell is available. false if not
     */
    @GetMapping("isAvailable/{row}/{column}")
    public ResponseEntity<AvailabilityDto> isAvailable(@PathVariable String row, @PathVariable String column) {
        return ResponseEntity.ok(new AvailabilityDto(service.isAvailable(row, column)));
    }

}
