package com.coindirect.recruitment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"row", "column"})
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"row", "column"})})
@AllArgsConstructor
@NoArgsConstructor
public class BookingDbo {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "\"row\"", nullable = false)
    private String row;
    @Column(nullable = false)
    private String column;
    private String name;

}
