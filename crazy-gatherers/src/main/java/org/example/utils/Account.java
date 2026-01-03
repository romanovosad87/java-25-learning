package org.example.utils;

import java.math.BigDecimal;
import java.time.LocalDate;


public record Account(
    Long id,
    String firstName,
    String lastName,
    String email,
    LocalDate birthday,
    Sex sex,
    LocalDate creationDate,
    BigDecimal balance
)
{}
