package org.example;

import org.example.utils.Account;
import org.example.utils.OrderDetails;
import org.example.utils.Gender;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class CrazyGatherers {

    private Collection<Account> accounts;

    private List<Integer> integers;

    public CrazyGatherers(Collection<Account> accounts) {
        this.accounts = accounts;
    }

    public CrazyGatherers(List<Integer> integers) {
        this.integers = integers;
    }

    /// Returns all accounts matching the given gender.
    ///
    /// Internally, this method demonstrates how a custom
    /// [java.util.stream.Gatherer] can be used instead of
    /// [java.util.stream.Stream#filter(java.util.function.Predicate)].
    ///
    /// @param gender
    ///   the gender to filter accounts by
    /// @return
    ///   a list of accounts with the given gender

    public List<Account> filterByGender(Gender gender) {
        return accounts.stream()
                .gather(filter(account -> account.gender() == gender))
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
        return Gatherer.of(
                ((_, element, downstream) -> {
                    if (predicate.test(element)) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
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
        return Gatherer.of(
                ((_, element, downstream) -> downstream.push(function.apply(element)))
        );
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
        return Gatherer.of(
                ((_, element, downstream) -> {
                    consumer.accept(element);
                    return downstream.push(element);
                })
        );
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
        return Gatherer.of(
                ((_, element, downstream) -> {
                    function.apply(element)
                            .forEach(downstream::push);
                    return true;
                })
        );
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
        return Gatherer.ofSequential(
                () -> new Object() { long currentSize = 0L; },
                ((state, element, downstream) -> {
                    if (state.currentSize++ < size) {
                        return downstream.push(element);
                    } else {
                        return false;
                    }
                })
        );
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
        return Gatherer.ofSequential(
                () -> new AtomicReference<>(true),
                ((gate, element, downstream) -> {
                    if (!gate.get()) {
                        return false;
                    }
                    if (predicate.test(element)) {
                        return downstream.push(element);
                    } else {
                        gate.set(false);
                        return false;
                    }
                })
        );
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
        return Gatherer.ofSequential(
                AtomicInteger::new,
                ((counter, element, downstream) -> {
                    if (counter.getAndIncrement() < size) {
                        return true;
                    } else {
                        return downstream.push(element);
                    }
                })
        );
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
        return Gatherer.ofSequential(
                () -> new Object() { boolean isClosed = true; },
                ((gate, element, downstream) -> {
                    if (gate.isClosed && !predicate.test(element)) {
                        gate.isClosed = false;
                    }
                    if (!gate.isClosed) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
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
        return Gatherer.ofSequential(
                HashSet::new,
                ((state, element, downstream) -> {
                    if (state.add(element)) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
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
        return Gatherer.of(
                () -> new PriorityQueue<String>(),
                ((queue, element, _) -> {
                    queue.add(element);
                    return true;
                }),
                (queue, queue2) -> {
                    queue.addAll(queue2);
                    return queue;
                },
                (queue, downstream) -> {
                    while (!queue.isEmpty()) {
                        downstream.push(queue.poll());
                    }
                }
        );
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
        return Gatherer.of(
                () -> new PriorityQueue<>(comparator),
                ((queue, element, _) -> {
                    queue.add(element);
                    return true;
                }),
                (queue, queue2) -> {
                    queue.addAll(queue2);
                    return queue;
                },
                (queue, downstream) -> {
                    while (!queue.isEmpty()) {
                        downstream.push(queue.poll());
                    }
                }
        );
    }

    /// Returns a list of first names from all accounts, concatenated using a fold operation.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Gatherers#fold(java.util.function.Supplier, java.util.function.BiFunction)].
    ///
    /// Internally, it accumulates the first names of all accounts into a single string,
    /// starting from an initial value `" $ "` and combining each element with `" | "`
    /// as a separator using the provided function.
    ///
    /// @return
    ///   a list containing the concatenated first names as a single string
    public List<String> concatenateFirstNames() {
        return accounts.stream()
                .map(Account::firstName)
                .gather(customStringFold(() -> "$", (str, name) -> str + " | " + name))
                .toList();
    }

    /// Creates a gatherer that accumulates elements using a fold-like operation.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must imitate [Gatherers#fold(java.util.function.Supplier, java.util.function.BiFunction)], starting from a supplied initial value
    /// and applying a combining function to each element.
    ///
    /// @param supplier
    ///   provides the initial value for the fold
    /// @param biConsumer
    ///   the function used to combine the accumulated value with each element
    /// @return
    ///   a gatherer that folds elements into a single result
    private Gatherer<String, ?, String> customStringFold(Supplier<String> initial, BiFunction<String, String, String> folder) {
        return Gatherer.ofSequential(
                () -> new Object() {
                    String word = initial.get();
                },
                ((state, element, _) -> {
                    state.word = folder.apply(state.word, element);
                    return true;
                }),
                (state, downstream) -> downstream.push(state.word)
        );
    }

    /// Returns a list of cumulative balances from all accounts.
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Gatherers#scan(java.util.function.Supplier, java.util.function.BiFunction)].
    ///
    /// Internally, it accumulates the balances of accounts step by step,
    /// starting from an initial value `BigDecimal.ZERO` and combining each
    /// account's balance using the provided scanning function.
    ///
    /// @return
    ///   a list containing the running totals of account balances
   public List<BigDecimal> scanBalances() {
       return accounts.stream()
               .gather(customScan(() -> BigDecimal.ZERO, (total, balance) -> total.add(balance.balance())))
               .toList();
    }

    /// Creates a gatherer that accumulates values in a running total manner.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must combine elements step by step using the provided
    /// scanner function, forwarding each intermediate result downstream.
    ///
    /// @param initial
    ///   a supplier providing the initial value for the accumulation
    /// @param scanner
    ///   a function that combines the current accumulated value and the next account
    /// @return
    ///   a gatherer that emits the running totals of balances
    private Gatherer<Account, ?, BigDecimal> customScan(Supplier<BigDecimal> initial,
                                                        BiFunction<BigDecimal, Account, BigDecimal> scanner) {
        return Gatherer.ofSequential(
                () -> new Object() {
                    BigDecimal total = initial.get();
                },
                ((state, element, downstream) -> {
                    state.total = scanner.apply(state.total, element);
                    return downstream.push(state.total);
                })
        );
    }

    /// Returns first names grouped into consecutive slices of a fixed size.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Gatherers#windowFixed(int)].
    ///
    /// Elements are collected in encounter order into lists of the given size.
    /// The final slice may contain fewer elements if the stream ends early.
    ///
    /// @param size
    ///   the maximum number of elements in each slice
    /// @return
    ///   a list of first-name slices
    public List<List<String>> groupEmailsByFixedWindow(int size) {
        return accounts.stream()
                .map(Account::email)
                .gather(Gatherers.windowFixed(size))
                .toList();
    }

    /// Creates a gatherer that collects elements into fixed-size windows.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must emit each window as a {@link java.util.List}
    /// once it reaches the specified size.
    ///
    /// @param size
    ///   the number of elements per window
    /// @return
    ///   a gatherer that groups elements into fixed-size windows
    private <T>Gatherer<T, ?, List<T>> windowFixed(int size) {
        return Gatherer.ofSequential(
                ArrayList<T>::new,
                ((window, element, downstream) -> {
                    window.add(element);
                    if (window.size() == size) {
                        List<T> emit = List.copyOf(window);
                        window.clear();
                        return downstream.push(emit);
                    }
                    return true;
                }),
                (state, downstream) -> {
                    if (!state.isEmpty()) {
                        List<T> emit = List.copyOf(state);
                        state.clear();
                        downstream.push(emit);
                    }
                }
        );
    }

    /// Returns first names grouped into consecutive slices of a fixed size.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to
    /// [Gatherers#windowSliding(int)].
    ///
    /// Elements are collected in encounter order into lists of the given size.
    /// The final slice may contain fewer elements if the stream ends early.
    ///
    /// @param size
    ///   the maximum number of elements in each slice
    /// @return
    ///   a list of first-name slices
    public List<List<String>> groupFirstNamesBySlidingWindow(int size) {
        return accounts.stream()
                .map(Account::firstName)
                .gather(windowSliding(size))
                .toList();
    }

    /// Creates a gatherer that groups elements into overlapping sliding windows.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer should maintain a moving window of the given size and
    /// emit a new list each time a new element enters the window, matching
    /// the semantics of a sliding window.
    ///
    /// @param size
    ///   the number of elements in each sliding window
    /// @param <T>
    ///   the type of streamed elements
    /// @return
    ///   a gatherer that emits overlapping sliding windows of elements
    private static  <T>Gatherer<T, ?, List<T>> windowSliding(int size) {
        return Gatherer.ofSequential(
                ArrayDeque<T>::new,
                ((queue, element, downstream) -> {
                    if (queue.size() < size) {
                        queue.addLast(element);
                    } else if (queue.size() == size) {
                        var emit = List.copyOf(queue);
                        queue.removeFirst();
                        queue.addLast(element);
                        return downstream.push(emit);
                    }
                    return true;
                }),
                (state, downstream) -> {
                    if (!state.isEmpty()) {
                        var emit = List.copyOf(state);
                        state.clear();
                        downstream.push(emit);
                    }
                }
        );
    }

    /// Returns a list of accounts with unique first names, processed in parallel.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can enforce uniqueness based on
    /// a derived key rather than object equality, even when the stream
    /// is executed concurrently.
    ///
    /// Only the first account for each first name is propagated
    /// downstream; subsequent accounts with the same first name
    /// are ignored. The gatherer must be thread-safe to handle
    /// parallel execution.
    ///
    /// @return
    ///   a list of accounts with distinct first names
    public List<Account> distinctByFirstName() {
        return accounts.parallelStream()
                .gather(distinctBy(Account::firstName))
                .toList();
    }

    /// Creates a thread-safe gatherer that emits elements with distinct extracted keys.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must track keys produced by the given extractor and
    /// forward only the first element associated with each key, while
    /// correctly handling concurrent updates from multiple threads.
    ///
    /// @param keyExtractor
    ///   function used to extract the comparison key
    /// @param <T>
    ///   the type of streamed elements
    /// @param <K>
    ///   the type of the extracted key
    /// @return
    ///   a thread-safe gatherer that emits only elements whose extracted keys are unique
    private <T, K>Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends K> keyExtractor) {
        return Gatherer.of(
                HashMap<K, T>::new, // initializer
                (state, element, _) -> {
                    K key = keyExtractor.apply(element);
                    if (!state.containsKey(key)) {
                        state.put(key, element);
                    }
                    return true;
                },
                (m1, m2) -> { // combiner
                    m2.forEach(m1::putIfAbsent);
                    return m1;
                },
                (state, downstream) -> { // finisher
                    for (T element : state.values()) {
                        if (!downstream.push((element))) {
                            break;
                        }
                    }
                }
        );
    }

    /// Returns a list of increasing sequences from the input integers.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement sequence detection
    /// based on a comparator. Each emitted list contains elements
    /// that are strictly increasing according to the provided comparator.
    ///
    /// For example, given list
    /// ```text
/// [2, 1, 3, 4, 5, 4, 3, 2, 1]
/// ```
    /// using the natural order comparator, the result would be:
    /// ```text
/// [[2], [1, 3, 4, 5], [4], [3], [2], [1]]
/// ```
    ///
    /// @return
    ///   a list of integer lists, each representing a maximal increasing sequence
    public List<List<Integer>> getIncreasingSequence() {
        return integers.stream()
                .gather(increasingSequence(Comparator.<Integer>naturalOrder()))
                .toList();
    }

    /// Creates a gatherer that collects elements into lists representing
    /// increasing sequences according to the given comparator.
    ///
    /// This method is intentionally left unimplemented as an exercise.
    /// The gatherer must start a new list whenever the next element does not
    /// continue the increasing sequence.
    ///
    /// @param comparator
    ///   the comparator used to determine the order of elements
    /// @param <T>
    ///   the type of streamed elements
    /// @return
    ///   a gatherer that emits consecutive increasing sequences
    private <T> Gatherer<T, ?, List<T>> increasingSequence(Comparator<T> comparator) {
        return Gatherer.ofSequential(
                ArrayList<T>::new,
                ((state, element, downstream) -> {
                    if (state.isEmpty() || comparator.compare(element, state.getLast()) > 0) {
                        state.add(element);
                    } else {
                        List<T> emitState = List.copyOf(state);
                        state.clear();
                        state.add(element);
                        return downstream.push(emitState);
                    }
                    return true;
                }),
                (state, downstream) -> {
                    if (!state.isEmpty() && !downstream.isRejecting()) {
                        List<T> emitState = List.copyOf(state);
                        state.clear();
                        downstream.push(emitState);
                    }
                }
        );
    }

    /// Returns a list of accounts selected at regular intervals from the stream.
    ///This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to taking
    /// every N-th element from a stream.
    ///
    /// For example, given a step of 2, every second account in encounter
    /// order is included in the result.
    ///
    /// @param step
    ///   the interval at which accounts are selected (must be non-zero)
    /// @return
    ///   a list of accounts sampled every `step` elements
    public List<Account> getEveryAccountByStep(int step) {
        return accounts.stream()
                .gather(every(step))
                .toList();
    }

    /// Creates a gatherer that selects every `step`-th element from a stream.
    ///
    /// The gatherer maintains a simple counter and pushes an element
    /// downstream only when the counter reaches a multiple of `step`.
    ///
    /// @param step
    ///   the interval at which elements are propagated (must be non-zero)
    /// @param <T>
    ///   the type of streamed elements
    /// @return
    ///   a gatherer that emits every `step`-th element
    /// @throws IllegalArgumentException
    ///   if `step` is less or equal to zero
    private <T> Gatherer<T, ?, T> every(int step) {
        if (step <= 0) {
            throw new IllegalArgumentException("step value can't be zero or negative value");
        }
        return Gatherer.ofSequential(
                AtomicInteger::new,
                ((state, element, downstream) -> {
                    if (state.incrementAndGet() % step == 0) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }

    /// Returns a list with consecutive duplicate elements collapsed into a single occurrence.
    ///
    /// This method demonstrates how a custom
    /// [java.util.stream.Gatherer] can implement behavior similar to removing
    /// consecutive duplicates from a stream, while preserving encounter order.
    ///
    /// For example, given the input list:
    ///
    /// ```text
/// [1, 1, 2, 2, 2, 3, 1, 1, 4]
/// ```
    ///
    /// the resulting list will be:
    ///
    /// ```text
/// [1, 2, 3, 1, 4]
/// ```
    ///
    /// @return
    ///   a list of elements with consecutive duplicates removed
    public List<Integer> collapseConsecutiveDuplicates() {
        return integers.stream()
                .gather(collapseConsecutive())
                .toList();
    }

/// Creates a gatherer that collapses consecutive duplicates into a single element.
///
/// This gatherer only propagates an element downstream if it differs from
/// the previous element, effectively removing consecutive duplicates while
/// preserving the encounter order.
///
/// @param <T>  the type of elements in the stream
    /// @return
    ///   a gatherer that removes consecutive duplicates in order
    private <T> Gatherer<T, ?, T> collapseConsecutive() {
        return Gatherer.ofSequential(
                () -> new AtomicReference<T>(),
                ((state, element, downstream) -> {
                    T last = state.get();
                    if (last == null || !last.equals(element)) {
                        state.set(element);
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }

    /// Returns a list of orders for all accounts.
    ///
    /// This method currently fetches orders for each account.
    /// Run the test [org.example.CrazyGatherersTest#getListOfOrdersByAccounts_executesWithinExpectedTime()]
    /// and fix the issue.
    ///
    /// Refactor this method so that it executes efficiently,
    /// while producing the same list of orders.
    ///
    /// @return a list of [OrderDetails] objects corresponding to all accounts
    public List<OrderDetails> getListOfOrdersByAccounts() {
        return accounts.stream()
                .gather(Gatherers.mapConcurrent(Runtime.getRuntime().availableProcessors(),
                        account -> callToAnotherMicroserviceToGetOrder(account.id())))
//                .map(account -> callToAnotherMicroserviceToGetOrder(account.id()))
                .toList();
    }

    private OrderDetails callToAnotherMicroserviceToGetOrder(Long accountId) {
        try {
            Thread.sleep(Duration.ofSeconds(1));
            return new OrderDetails(accountId + 1, accountId, "some info");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
