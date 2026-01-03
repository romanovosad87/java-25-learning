package org.example.utils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TestUtils {

    public static List<Account> accounts() {
        return List.of(
                new Account(1L, "Alice", "Smith", "alice@gmail.com",
                        LocalDate.of(1990, 1, 1), Sex.FEMALE,
                        LocalDate.of(2020, 1, 1), BigDecimal.valueOf(1000)),
                new Account(2L, "Bob", "Johnson", "bob@gmail.com",
                        LocalDate.of(1985, 5, 20), Sex.MALE,
                        LocalDate.of(2019, 3, 15), BigDecimal.valueOf(2000)),
                new Account(3L, "Carol", "Williams", "carol@gmail.com",
                        LocalDate.of(1995, 7, 10), Sex.FEMALE,
                        LocalDate.of(2021, 6, 30), BigDecimal.valueOf(1500))
        );
    }
}
