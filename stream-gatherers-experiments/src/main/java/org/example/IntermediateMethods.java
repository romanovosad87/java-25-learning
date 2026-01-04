package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class IntermediateMethods {

    //filter, map, flatMap, distinct, sorted, peek, limit, skip, takeWhile, dropWhile
    // limit and takeWhile the methods that close the door and do not accept any new elements (interrupting gatherers)
    // skip and dropWhile , delay the execution of the stream (interrupting gatherers)
    // limit, skip, takeWhile and dropWhile should have internal mutable state . Limit and skip (counter), takeWhile and dropWhile (boolean)
    // sorted and distinct methods that create some buffer before emits some result, can't do that operations in unbound stream
    static void main() {

        var ints = List.of(2, 1, 3, 4, 5, 4, 3, 2, 1);
        var names = List.of("Alex", "Maria", "John", "Sophie", "Daniel", "Elena", "Michael", "Anna", "Thomas", "Olivia");
        var numbers = List.of(1, 1, 2, 2, 2, 3, 1, 1, 4);


        System.out.println(names.stream()
                .gather(every(6))
                .toList());

//        System.out.println(ints.stream()
//                .gather(increasingSequence(Comparator.<Integer>naturalOrder()))
//                .toList());

//        System.out.println(numbers.stream()
//                .gather(collapseConsecutive())
//                .toList());

//        System.out.println(names.parallelStream()
//                .gather(distinctBy(String::length))
//                .toList());

//        var flatMap = names.stream()
//                .flatMap(name -> name.lines())
//                .toList();
//
//
        List<String> flatMapGatherer = names.stream()
                .gather(flatMap(name -> name.lines()))
                .toList();
//
//        List<String> peekStream = names.stream()
//                .peek(IO::println)
//                .toList();
//
//        IO.println("---------------");
//
//        List<String> peekGatherer = names.stream()
//                .gather(peek(IO::println))
//                .toList();
//
//        System.out.println(names.parallelStream()
//                .limit(3)
//                .toList());
//
//        System.out.println(names.parallelStream()
//                .peek(System.out::println)
//                .gather(limit(3))
//                .toList());
//        System.out.println(flatMap);
//        IO.println("---------------");
//        System.out.println(flatMapGatherer);
//        IO.println("---------------");
//        System.out.println(peekStream);
//        IO.println("---------------");
//        System.out.println(peekGatherer);
//
//        System.out.println(names.stream()
//                .skip(3)
//                .toList());
//
//        System.out.println(names.stream()
//                .gather(skip(3))
//                .toList());
//
//        System.out.println(names.stream()
//                .takeWhile(name -> name.length() < 6)
//                .toList());
//
//        System.out.println(names.stream()
//                .gather(takeWhile(name -> name.length() < 6))
//                .toList());

//        System.out.println(names.parallelStream()
//                .sorted(Comparator.reverseOrder())
//                .toList());
//
//        IO.println("----------");
//
//
//        System.out.println(names.stream()
//                .peek(System.out::println)
//                .gather(limit(3))
//                .toList());
//
//        System.out.println(names.stream()
//                .sorted()
//                .dropWhile(name -> name.startsWith("A"))
//                .toList());
//
//        System.out.println(names.stream()
//                .sorted()
//                .gather(dropWhile(name -> name.startsWith("A")))
//                .toList());

//        System.out.println(names.stream()
//                .map(String::toUpperCase)
//                .toList());
//
//        System.out.println(names.stream()
//                .gather(map(String::toUpperCase))
//                .toList());
//        System.out.println(names.stream()
//                .filter(name -> name.length() > 4)
//                .toList());

//        System.out.println(names.stream()
//                .gather(filter(name -> name.length() > 4))
//                .toList());

//        System.out.println(ints.stream()
//                .distinct()
//                .limit(2)
//                .toList());
//
//        System.out.println(ints.stream()
//                .gather(distinct())
//                .limit(2)
//                .toList());

    }

    private static <T>Gatherer<T, ?, T> distinct() {
        return Gatherer.of(
                HashSet::new,
                ((state, element, downstream) -> state.add(element)),
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                },
                (state, downstream) -> {
                    for (Object elem : state) {
                        if (!downstream.push((T) elem)) {
                            break;
                        }
                    }
                }
        );
    }

    private static <T> Gatherer<T, ?, T> filter(Predicate<T> predicate) {
        return Gatherer.of(Gatherer.Integrator.ofGreedy(
                (_, element, downstream) -> {
                    if (predicate.test(element)) {
                        downstream.push(element);
                    }
                    return true;
                })
        );
    }

    private static <T>Gatherer<T, ?, T> map(Function<T, T> function) {
        return Gatherer.of(
                ((_, element, downstream) -> downstream.push(function.apply(element)))
        );
    }

    private static <T>Gatherer<T, ?, T> dropWhile(Predicate<T> predicate) {
        class Gate {
            boolean open = false;
        }
        return Gatherer.ofSequential(
                Gate::new,
                ((state, element, downstream) -> {
                    if (!state.open && !predicate.test(element)) {
                        state.open = true;
                    }
                    if (state.open) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }

    private static <T extends Comparable<T>> Gatherer<T, ?, T> sorted() {
        return Gatherer.of(
                () -> new TreeSet<T>(),
                ((state, element, _) -> {
                    state.add(element);
                    return true;
                }),
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                },
                (state, downstream) -> {
                    for (T e : state) {
                        if (!downstream.push(e)) {
                            break;
                        }
                    }
                });
    }

    private static <T>Gatherer<T, ?, T> sorted(Comparator<? super T> comparator) {
        return Gatherer.of(
                () -> new TreeSet<T>(comparator),
                ((state, element, _) -> {
                    state.add(element);
                    return true;
                } ),
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                },
                (state, downstream) -> state.forEach(downstream::push)
        );
    }

    private static <T>Gatherer<T, ?, T> takeWhile(Predicate<T> predicate) {
        return Gatherer.ofSequential(
                () -> new Object() {boolean open = true; },
                ((state, element, downstream) -> {
                    if (predicate.test(element) && state.open) {
                        return downstream.push(element);
                    }
                    if (!predicate.test(element)) {
                        state.open = false;
                        return false;
                    }
                    return true;
                })
        );
    }

    private static <T> Gatherer<? super T, ?, T> skip(long n) {
        class Count {
            private long count = 0;
        }
        return Gatherer.ofSequential(
                () -> new Object() {private long count = 0;},
                ((state, element, downstream) -> {
                    if (state.count++ >= n) {
                        downstream.push(element);
                    }
                    return true;
                })
        );
    }

    private static <T>Gatherer<? super T, ?, T> limit(long maxSize) {
        class Count {
            long count = 0;
        }
        return Gatherer.ofSequential(
                Count::new,
                ((state, element, downstream) -> {
                    state.count++;
                    if (state.count == maxSize) {
                        downstream.push(element);
                        return false;
                    } else if (state.count < maxSize) {
                        return downstream.push(element);
                    } else {
                        return false;
                    }
                    // show not correct behaviour with returning true always
//                    if (state.count++ < maxSize) {
//                        return downstream.push(element);
//                    }
//                        return true;

                })
        );
    }

    private static <T>Gatherer<T, ?, T> peek(Consumer<T> consumer) {
        return Gatherer.ofSequential((_, element, downstream) -> {
            consumer.accept(element);
            return downstream.push(element);
        });
    }

    private static <T,R>Gatherer<T, ?, R> flatMap(Function<T, Stream<R>> function) {
        return Gatherer.ofSequential((_, element, downstream) -> {
            Stream<R> result = function.apply(element);
            result.forEach(downstream::push);
            return true;
        });
    }

    private static <T> Gatherer<T, ?, List<T>> increasingSequence(Comparator<T> comparator) {
        return Gatherer.ofSequential(
                ()-> new ArrayList<T>(),
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


    // "Alex", "Maria", "John", "Sophie", "Daniel", "Elena", "Michael", "Anna", "Thomas", "Olivia"
    private static <T> Gatherer<T, ?, List<T>> slidingWindow(int size) {
        return Gatherer.ofSequential(
                () -> new ArrayDeque<T>() {},
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

    private static <T, K> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends K> keyExtractor) {
        return Gatherer.of(
                HashMap<K, T>::new, // state: Map<K, T> to remember first occurrence
                (state, element, downstream) -> {
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

    // var numbers = List.of(1, 1, 2, 2, 2, 3, 1, 1, 4);
    // Expected output:
    // [1, 2, 3, 1, 4]
    private static <T> Gatherer<T, ?, T> collapseConsecutive() {
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

    private static <T> Gatherer<T, ?, T> collapseConsecutive(BiPredicate<T, T> equals) {
        return Gatherer.ofSequential(
                AtomicReference<T>::new,
                (state, element, downstream) -> {
                    T last = state.get();
                    if (last == null || !equals.test(last, element)) {
                        state.set(element);
                        return downstream.push(element);
                    }
                    return true;
                }
        );
    }

    private static <T> Gatherer<T, ?, T> every(int step) {
        if (step == 0) {
            throw new IllegalArgumentException("step value can't be zero");
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

}
