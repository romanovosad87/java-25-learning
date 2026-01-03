package org.example.utils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TestUtils {

    private static final List<String> FIRST_NAMES = List.of(
            "Alice", "Bob", "Carol", "David", "Eve", "Frank", "Grace", "Hannah", "Ian", "Julia"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Smith", "Johnson", "Williams", "Brown", "Davis", "Miller", "Wilson", "Taylor", "Anderson"
    );

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmail.com", "yahoo.com", "outlook.com", "example.com"
    );

    public static List<Account> accounts() {
        return List.of(
                new Account(1L, "Alice", "Smith", "alice@gmail.com",
                        LocalDate.of(1990, 1, 1), Sex.FEMALE,
                        LocalDate.of(2020, 1, 1), BigDecimal.valueOf(1000)),

                new Account(2L, "Bob", "Johnson", "bob@yahoo.com",
                        LocalDate.of(1985, 5, 20), Sex.MALE,
                        LocalDate.of(2019, 3, 15), BigDecimal.valueOf(2000)),

                new Account(3L, "Carol", "Williams", "carol@gmail.com",
                        LocalDate.of(1995, 7, 10), Sex.FEMALE,
                        LocalDate.of(2021, 6, 30), BigDecimal.valueOf(1500)),

                new Account(4L, "David", "Brown", "david@outlook.com",
                        LocalDate.of(1988, 2, 28), Sex.MALE,
                        LocalDate.of(2018, 9, 12), BigDecimal.valueOf(1800)),

                new Account(5L, "Eve", "Davis", "eve@gmail.com",
                        LocalDate.of(2000, 12, 5), Sex.FEMALE,
                        LocalDate.of(2022, 4, 1), BigDecimal.valueOf(2200)),

                new Account(6L, "Alice", "Taylor", "alice.t@gmail.com",
                        LocalDate.of(1992, 3, 14), Sex.FEMALE,
                        LocalDate.of(2021, 5, 10), BigDecimal.valueOf(1300)) // duplicate first name
        );
    }

    public static List<Account> randomAccounts(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> {
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
                    String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
                    String email = firstName.toLowerCase() + i + "@" + EMAIL_DOMAINS.get(random.nextInt(EMAIL_DOMAINS.size()));

                    LocalDate birthday = LocalDate.of(
                            1970 + random.nextInt(30), // year 1970-1999
                            1 + random.nextInt(12),   // month 1-12
                            1 + random.nextInt(28)    // day 1-28 (safe for all months)
                    );

                    Sex sex = random.nextBoolean() ? Sex.FEMALE : Sex.MALE;

                    LocalDate registered = LocalDate.of(
                            2015 + random.nextInt(8), // year 2015-2022
                            1 + random.nextInt(12),
                            1 + random.nextInt(28)
                    );

                    BigDecimal balance = BigDecimal.valueOf(500 + random.nextInt(5000));

                    return new Account((long) i, firstName, lastName, email, birthday, sex, registered, balance);
                })
                .toList();
    }
}
