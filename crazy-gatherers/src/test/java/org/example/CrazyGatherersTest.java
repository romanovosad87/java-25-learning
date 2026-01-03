package org.example;

import org.example.utils.Account;
import org.example.utils.OrderDetails;
import org.example.utils.Gender;
import org.example.utils.TestUtils;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CrazyGatherersTest {

    private CrazyGatherers crazyGatherers;

    @BeforeEach
    public void setAccounts() {
        crazyGatherers = new CrazyGatherers(TestUtils.accounts());
    }

    @Test
    @Order(1)
    void filterByGender() {
        List<Account> result = crazyGatherers.filterByGender(Gender.FEMALE);

        assertThat(result).hasSize(4);
        assertThat(result.get(0).firstName()).isEqualTo("Alice");
        assertThat(result.get(1).firstName()).isEqualTo("Carol");
        assertThat(result.get(2).firstName()).isEqualTo("Eve");
        assertThat(result.get(3).firstName()).isEqualTo("Alice");
    }

    @Test
    @Order(2)
    void mapToNames() {
        List<String> result = crazyGatherers.mapToNames();

        assertThat(result).containsExactly(
                "Alice Smith",
                "Bob Johnson",
                "Carol Williams",
                "David Brown",
                "Eve Davis",
                "Alice Taylor"
        );
    }

    @Test
    @Order(3)
    void printAllFirstNames() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            crazyGatherers.printAllFirstNames();

            String output = outContent.toString().trim();

            String[] lines = output.split(System.lineSeparator());

            assertThat(lines).containsExactly(
                    "Alice",
                    "Bob",
                    "Carol",
                    "David",
                    "Eve",
                    "Alice"
            );
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @Order(4)
    void flatMapToFirstNameLines() {
        List<String> result = crazyGatherers.flatMapToFirstNameLines();

        assertThat(result).containsExactlyInAnyOrder(
                "Alice", "Bob", "Carol", "David", "Eve", "Alice"
        );
    }

    @Test
    @Order(5)
    void limitedListOfAccounts() {
        int limitSize = 2;
        List<Account> result = crazyGatherers.limitedListOfAccounts(limitSize);

        assertThat(result).hasSize(limitSize);
        assertThat(result.get(0).firstName()).isEqualTo("Alice");
        assertThat(result.get(1).firstName()).isEqualTo("Bob");
    }

    @Test
    void takeWhileEmailDomainIsGmail() {
        List<Account> result = crazyGatherers.takeWhileEmailDomainIsGmail();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("Alice");
    }

    @Test
    void skipAccounts() {
        int skipSize = 2;
        List<Account> result = crazyGatherers.skipAccounts(skipSize);

        assertThat(result).hasSize(TestUtils.accounts().size() - skipSize);
        assertThat(result.get(0).firstName()).isEqualTo("Carol");
        assertThat(result.get(1).firstName()).isEqualTo("David");
        assertThat(result.get(2).firstName()).isEqualTo("Eve");
        assertThat(result.get(3).firstName()).isEqualTo("Alice");
    }

    @Test
    void dropWhileBornAfter() {
        List<Account> result = crazyGatherers.dropWhileBornAfter(LocalDate.of(1988, 12, 31));

        assertThat(result).hasSize(5);
        assertThat(result.get(0).firstName()).isEqualTo("Bob");
        assertThat(result.get(1).firstName()).isEqualTo("Carol");
        assertThat(result.get(2).firstName()).isEqualTo("David");
        assertThat(result.get(3).firstName()).isEqualTo("Eve");
        assertThat(result.get(4).firstName()).isEqualTo("Alice");
    }

    @Test
    void distinctFirstNames() {
        List<String> result = crazyGatherers.distinctFirstNames();

        assertThat(result).hasSize(5);
        assertThat(result).containsExactlyInAnyOrder("Alice", "Bob", "Carol", "David", "Eve");
    }

    @Test
    void sortByLastNames() {
        List<String> result = crazyGatherers.sortByLastNames();

        assertThat(result).containsExactly(
                "Brown",
                "Davis",
                "Johnson",
                "Smith",
                "Taylor",
                "Williams"
        );
    }

    @Test
    void sortFirstNameByComparator() {
        List<String> result = crazyGatherers.sortFirstNameByComparator(Comparator.reverseOrder());

        assertThat(result).containsExactly(
                "Eve",
                "David",
                "Carol",
                "Bob",
                "Alice",
                "Alice"
        );
    }

    @Test
    void concatenateFirstNamesTest() {
        List<String> result = crazyGatherers.concatenateFirstNames();

        assertThat(result)
                .hasSize(1)
                .containsExactly("$ | Alice | Bob | Carol | David | Eve | Alice");
    }

    @Test
    void scanBalances() {
        List<BigDecimal> result = crazyGatherers.scanBalances();

        assertThat(result).containsExactly(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(4500),
                BigDecimal.valueOf(6300),
                BigDecimal.valueOf(8500),
                BigDecimal.valueOf(9800)
        );
    }

    @Test
    void groupEmailsByFixedWindow() {
        List<List<String>> result = crazyGatherers.groupEmailsByFixedWindow(4);

        assertThat(result).hasSize(2);

        assertThat(result.get(0)).containsExactly(
                "alice@gmail.com",
                "bob@yahoo.com",
                "carol@gmail.com",
                "david@outlook.com"
        );

        assertThat(result.get(1)).containsExactly(
                "eve@gmail.com",
                "alice.t@gmail.com"
        );
    }

    @Test
    void groupFirstNamesBySlicingWindow() {
        List<List<String>> result =
                crazyGatherers.groupFirstNamesBySlidingWindow(3);

        assertThat(result).containsExactly(
                List.of("Alice", "Bob", "Carol"),
                List.of("Bob", "Carol", "David"),
                List.of("Carol", "David", "Eve"),
                List.of("David", "Eve", "Alice")
        );
    }

    @Test
    void distinctByFirstName_parallelStream() {
        List<Account> accounts = TestUtils.randomAccounts(10_000);
        CrazyGatherers crazyGatherersRandom = new CrazyGatherers(accounts);
        List<Account> result = crazyGatherersRandom.distinctByFirstName();

        assertThat(result).hasSize(10);

        Set<String> firstNames = result.stream()
                .map(Account::firstName)
                .collect(Collectors.toSet());

        assertThat(firstNames).containsExactlyInAnyOrder("Alice", "Bob", "Carol", "David", "Eve", "Frank", "Grace", "Hannah", "Ian", "Julia");
    }

    @Test
    void getIncreasingSequence_shouldReturnCorrectSequences() {
        // Arrange
        var ints = List.of(2, 1, 3, 4, 5, 4, 3, 2, 1);
        CrazyGatherers crazyGatherers = new CrazyGatherers(ints);

        // Act
        List<List<Integer>> result = crazyGatherers.getIncreasingSequence();

        // Assert
        assertThat(result).hasSize(6);
        assertThat(result.get(0)).containsExactly(2);
        assertThat(result.get(1)).containsExactly(1, 3, 4, 5);
        assertThat(result.get(2)).containsExactly(4);
        assertThat(result.get(3)).containsExactly(3);
        assertThat(result.get(4)).containsExactly(2);
        assertThat(result.get(5)).containsExactly(1);
    }

    @Test
    void getEveryAccountByStep_step2() {
        List<Account> result = crazyGatherers.getEveryAccountByStep(2);

        // We expect every 2nd account: Bob, David, Alice(Taylor)
        assertThat(result).hasSize(3);
        assertThat(result.get(0).firstName()).isEqualTo("Bob");
        assertThat(result.get(1).firstName()).isEqualTo("David");
        assertThat(result.get(2).firstName()).isEqualTo("Alice");
    }

    @Test
    void getEveryAccountByStep_step1_returnsAll() {
        List<Account> result = crazyGatherers.getEveryAccountByStep(1);

        // Step 1 returns all accounts in order
        assertThat(result).hasSize(TestUtils.accounts().size());
        assertThat(result).containsExactlyElementsOf(TestUtils.accounts());
    }

    @Test
    void getEveryAccountByStep_zeroStep_throws() {
        // Step 0 should throw IllegalArgumentException
        assertThatThrownBy(() -> crazyGatherers.getEveryAccountByStep(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void collapseConsecutiveDuplicates_shouldRemoveConsecutiveDuplicates() {
        var ints = List.of(1, 1, 2, 2, 2, 3, 1, 1, 4);
        CrazyGatherers crazyGatherers = new CrazyGatherers(ints);

        List<Integer> result = crazyGatherers.collapseConsecutiveDuplicates();

        assertThat(result)
                .containsExactly(1, 2, 3, 1, 4);
    }

    @Test
    void collapseConsecutiveDuplicates_emptyList_returnsEmptyList() {
        var emptyList = new ArrayList<Integer>();
        CrazyGatherers crazyGatherers = new CrazyGatherers(emptyList);

        List<Integer> result = crazyGatherers.collapseConsecutiveDuplicates();

        assertThat(result).isEmpty();
    }

    @Test
    void collapseConsecutiveDuplicates_singleElement_returnsSameList() {
        int element = 42;
        var listWithOneElement = List.of(element);
        CrazyGatherers crazyGatherers = new CrazyGatherers(listWithOneElement);

        List<Integer> result = crazyGatherers.collapseConsecutiveDuplicates();

        assertThat(result).containsExactly(element);
    }

    @Test
    void getListOfOrdersByAccounts_executesWithinExpectedTime() {
        int accountSize = 5;
        var accounts = TestUtils.randomAccounts(accountSize);
        CrazyGatherers crazyGatherers = new CrazyGatherers(accounts);

        // Get number of available cores
        int cores = Runtime.getRuntime().availableProcessors();

        // Calculate expected time in milliseconds: ceil(accountSize / cores) * 1000 + 1000 ms for overhead
        long expectedTimeMillis = (long) (Math.ceil((double) accountSize / cores) * 1000) + 1000;

        // Measure start time
        Instant start = Instant.now();

        List<OrderDetails> orders = crazyGatherers.getListOfOrdersByAccounts();

        // Measure end time
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        // Verify correctness
        assertThat(orders)
                .hasSize(accountSize);

        // Verify execution time in milliseconds
        assertThat(duration.toMillis())
                .as("Expected getListOfOrdersByAccounts() to execute in less than %d ms (based on %d cores), but took %d ms",
                        expectedTimeMillis, cores, duration.toMillis())
                .isLessThan(expectedTimeMillis);
    }
}