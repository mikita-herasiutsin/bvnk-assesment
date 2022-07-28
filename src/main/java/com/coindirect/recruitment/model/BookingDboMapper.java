package com.coindirect.recruitment.model;

import com.coindirect.recruitment.dto.BookingDto;
import com.coindirect.recruitment.dto.RequestBookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper
public interface BookingDboMapper {

    BookingDboMapper INSTANCE = getMapper(BookingDboMapper.class);

    @Mapping(target = "id", ignore = true)
    BookingDbo toDbo(RequestBookingDto dto);

    @Mapping(target = "bookingId", source = "id")
    BookingDto toDto(BookingDbo dbo);

}
