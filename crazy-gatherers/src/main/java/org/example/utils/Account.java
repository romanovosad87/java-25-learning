package org.example.utils;

import java.math.BigDecimal;
import java.time.LocalDate;


public record Account(
    Long id,
    String firstName,
    String lastName,
    String email,
    LocalDate birthday,
    Gender gender,
    LocalDate creationDate,
    BigDecimal balance
)
{}
