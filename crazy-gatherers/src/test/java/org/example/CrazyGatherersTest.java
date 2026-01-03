package org.example;

import org.assertj.core.api.Assertions;
import org.example.utils.Account;
import org.example.utils.Sex;
import org.example.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CrazyGatherersTest {

    private CrazyGatherers crazyGatherers;

    @BeforeEach
    public void setAccounts() {
        crazyGatherers = new CrazyGatherers(TestUtils.accounts());
    }

    @Test
    void filterBySex() {
        List<Account> result = crazyGatherers.filterBySex(Sex.FEMALE);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Alice");
        assertThat(result.get(1).firstName()).isEqualTo("Carol");
    }

    @Test
    void mapToNames() {
    }

    @Test
    void printAllFirstNames() {
    }

    @Test
    void flatMapToFirstNameLines() {
    }

    @Test
    void limitedListOfAccounts() {
    }

    @Test
    void takeWhileEmailDomainIsGmail() {
    }

    @Test
    void skipAccounts() {
    }

    @Test
    void dropWhileBornAfter() {
    }

    @Test
    void distinctFirstNames() {
    }

    @Test
    void sortByLastNames() {
    }

    @Test
    void sortFirstNameByComparator() {
    }
}