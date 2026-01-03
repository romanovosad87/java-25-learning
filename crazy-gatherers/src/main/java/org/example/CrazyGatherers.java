package org.example;

import org.example.utils.Account;
import org.example.utils.ExerciseNotCompletedException;
import org.example.utils.Sex;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class CrazyGatherers {

    private Collection<Account> accounts;

    public CrazyGatherers(Collection<Account> accounts) {
        this.accounts = accounts;
    }

    /// Returns all accounts matching the given sex.
    ///
    /// Internally, this method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used instead of
    /// [java.util.stream.Stream#filter(java.util.function.Predicate)].
    ///
    /// @param sex
    ///   the sex to filter accounts by
    /// @return
    ///   a list of accounts with the given sex

    public List<Account> filterBySex(Sex sex) {
        return accounts.stream()
                .gather(filter(account -> account.sex() == sex))
                .toList();
    }

    /// Creates a gatherer that filters [Account] elements using the given predicate.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must forward an element downstream only if
    /// `predicate.test(element)` returns `true`.
    ///
    /// @param predicate
    ///   the predicate used to decide whether an account is propagated
    /// @return
    ///   a gatherer that filters accounts according to the predicate
    private Gatherer<Account, ?, Account> filter(Predicate<Account> predicate) {
        throw new ExerciseNotCompletedException();
    }

    /// Maps accounts to their full names.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used instead of
    /// [java.util.stream.Stream#map(java.util.function.Function)].
    ///
    /// @return
    ///   a list containing the full names of all accounts
    public List<String> mapToNames() {
        return accounts.stream()
                .gather(map(account -> "%s %s".formatted(
                        account.firstName(),
                        account.lastName())))
                .toList();
    }

    /// Creates a gatherer that transforms [Account] elements into another type.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must apply the given function to each incoming element
    /// and forward the result downstream.
    ///
    /// @param function
    ///   the mapping function applied to each account
    /// @return
    ///   a gatherer that maps accounts to values produced by the function
    private Gatherer<Account, ?, String> map(Function<Account, String> function) {
        throw new ExerciseNotCompletedException();
    }

    /// Prints the first name of every account.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used to perform
    /// side effects, similar to [java.util.stream.Stream#peek(java.util.function.Consumer)].
    ///
    /// @implNote
    /// The terminal operation consumes the stream without
    /// producing a meaningful result.
    ///
    /// @return
    ///   nothing; this method performs output as a side effect
    public void printAllFirstNames() {
        accounts.stream()
                .map(Account::firstName)
                .gather(peek(IO::println))
                .forEach(_ -> {});
    }

    /// Creates a gatherer that performs a side effect for each element
    /// while preserving the original element.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must pass each element downstream unchanged
    /// after applying the given consumer.
    ///
    /// @param consumer
    ///   the action to perform for each element
    /// @return
    ///   a gatherer that performs a side effect without modifying elements
    private Gatherer<String, ?, String> peek(Consumer<String> consumer) {
        throw new ExerciseNotCompletedException();
    }

    /// Flattens first-name lines from all accounts into a single stream.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used instead of
    /// [Stream#flatMap(java.util.function.Function)].
    ///
    /// @return
    ///   a list containing all first-name lines from all accounts
    public List<String> flatMapToFirstNameLines() {
        return accounts.stream()
                .gather(flatMap(account -> account.firstName().lines()))
                .toList();
    }

    /// Creates a gatherer that flattens streams produced from each element.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must apply the given function to each incoming element
    /// and forward all elements of the resulting stream downstream.
    ///
    /// @param function
    ///   a function producing a stream of values from an account
    /// @return
    ///   a gatherer that flattens produced streams into a single stream
    private Gatherer<Account, ?, String> flatMap(Function<Account, Stream<String>> function) {
        throw new ExerciseNotCompletedException();
    }

    /// Returns a list containing at most the given number of accounts.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used instead of
    /// [Stream#limit(long)].
    ///
    /// @param size
    ///   the maximum number of accounts to include
    /// @return
    ///   a list containing at most {@code size} accounts
    public List<Account> limitedListOfAccounts(long size) {
        return accounts.stream()
                .gather(limit(size))
                .toList();
    }

    /// Creates a gatherer that forwards only the first {@code size} elements.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must stop propagating elements downstream
    /// once the given limit has been reached.
    ///
    /// @param size
    ///   the maximum number of elements to propagate
    /// @return
    ///   a gatherer that limits the number of propagated elements
    private Gatherer<Account, ?, Account> limit(long size) {
        throw new ExerciseNotCompletedException();
    }

    /// Returns accounts while the email domain is `gmail.com`.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#takeWhile(java.util.function.Predicate)].
    ///
    /// Processing stops as soon as an account does not satisfy the predicate.
    ///
    /// @return
    ///   a list of accounts whose email domain is `gmail.com`
    ///   until the first non-matching account is encountered
    public List<Account> takeWhileEmailDomainIsGmail() {
        return accounts.stream()
                .gather(takeWhile(account -> account.email().split("@")[1].equals("gmail.com")))
                .toList();
    }

    /// Creates a gatherer that forwards elements while a predicate holds.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must stop the stream as soon as the predicate
    /// evaluates to `false`.
    ///
    /// @param predicate
    ///   the condition that must remain `true` to continue propagation
    /// @return
    ///   a gatherer that forwards elements while the predicate holds
    private Gatherer<Account, ?, Account> takeWhile(Predicate<Account> predicate) {
        throw new ExerciseNotCompletedException();
    }

    /// Returns a list of accounts skipping the first `size` elements.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#skip(long)].
    ///
    /// @param size
    ///   the number of accounts to skip
    /// @return
    ///   a list containing the remaining accounts after skipping the first `size` elements
    public List<Account> skipAccounts(long size) {
        return accounts.stream()
                .gather(skip(size))
                .toList();
    }

    /// Creates a gatherer that skips the first `size` elements.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must forward elements downstream only after
    /// skipping the specified number of elements.
    ///
    /// @param size
    ///   the number of elements to skip
    /// @return
    ///   a gatherer that skips the first `size` elements
    private Gatherer<Account, ?, Account> skip(long size) {
        throw new ExerciseNotCompletedException();
    }

    /// Returns accounts, skipping those whose birthday is after the given date.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#dropWhile(java.util.function.Predicate)].
    ///
    /// Processing stops skipping elements as soon as an account does not satisfy the predicate.
    ///
    /// @param date
    ///   the date used to determine which accounts to skip
    /// @return
    ///   a list of accounts starting from the first one whose birthday
    ///   is on or before the given date
    public List<Account> dropWhileBornAfter(LocalDate date) {
        return accounts.stream()
                .gather(dropWhile(account -> account.birthday().isAfter(date)))
                .toList();
    }

    /// Creates a gatherer that drops elements while a predicate holds.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must forward elements downstream only after the predicate
    /// evaluates to false for the first time.
    ///
    /// @param predicate
    ///   the condition that must remain true to continue dropping elements
    /// @return
    ///   a gatherer that drops elements while the predicate is true
    private Gatherer<Account, ?, Account> dropWhile(Predicate<Account> predicate) {
        throw new ExerciseNotCompletedException();
    }

    /// Returns a list of distinct first names from all accounts.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#distinct()].
    ///
    /// @return
    ///   a list of unique first names
    public List<String> distinctFirstNames() {
        return accounts.stream()
                .map(Account::firstName)
                .gather(distinct())
                .toList();
    }

    /// Creates a gatherer that forwards only distinct elements.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must ensure that each element is propagated downstream
    /// at most once.
    ///
    /// @return
    ///   a gatherer that filters out duplicate elements
    private Gatherer<String, ?, String> distinct() {
        throw new ExerciseNotCompletedException();
    }

    /// Returns a list of last names from all accounts, sorted alphabetically.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#sorted()].
    ///
    /// @return
    ///   a list of last names in ascending alphabetical order
    public List<String> sortByLastNames() {
        return accounts.stream()
                .map(Account::lastName)
                .gather(sorted())
                .toList();
    }

    /// Creates a gatherer that forwards elements in sorted order.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must propagate elements downstream in ascending order.
    ///
    /// @return
    ///   a gatherer that sorts elements before forwarding
    private Gatherer<String, ?, String> sorted() {
        throw new ExerciseNotCompletedException();
    }

    /// Returns a list of first names sorted using the given comparator.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Stream#sorted(java.util.Comparator)].
    ///
    /// @param comparator
    ///   the comparator used to determine the order of elements
    /// @return
    ///   a list of first names sorted according to the provided comparator
    public List<String> sortFirstNameByComparator(Comparator<String> comparator) {
        return accounts.stream()
                .map(Account::firstName)
                .gather(sorted(comparator))
                .toList();
    }

    /// Creates a gatherer that forwards elements in the order defined by a comparator.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must propagate elements downstream sorted according to the comparator.
    ///
    /// @param comparator
    ///   the comparator that defines the element order
    /// @return
    ///   a gatherer that sorts elements before forwarding
    private Gatherer<String, ?, String> sorted(Comparator<String> comparator) {
        throw new ExerciseNotCompletedException();
    }
}
