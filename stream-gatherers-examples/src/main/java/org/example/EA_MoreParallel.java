package org.example;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

public class EA_MoreParallel {
    static void main() {

        var ints = List.of(1, 2, 2, 1, 1, 3, 2, 4, 4, 2);
       ints.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
               .entrySet().stream()
               .sorted(Map.Entry.comparingByValue())
               .forEach(System.out::println);

        IO.println("---------------------------------");

        ints.stream()
                .gather(frequencyMap())
                .sorted(Map.Entry.comparingByValue())
                .limit(2)
                .forEach(System.out::println);

//        sequential();
//        IO.println("---------------------------------");
//        parallel();

    }

    private static <T>Gatherer<T, ?, Map.Entry<T, Long>> frequencyMap() {
        class Counter {
            long count = 0;
        }
        Supplier<Map<T, Counter>> supplier = HashMap::new;
        Gatherer.Integrator<
                Map<T, Counter>,
                T,
                Map.Entry<T, Long>>integrator =  (map, element, downstream) -> {
            if (downstream.isRejecting()) {
                return false;
            } else {
                map.computeIfAbsent(element, _ -> new Counter()).count++;
                return true;
            }
        };

        BiConsumer<Map<T, Counter>, Gatherer.Downstream<? super Map.Entry<T, Long>>> finisher = (map, downstream) -> {
            if (!downstream.isRejecting()) {
                map.entrySet().stream()
                        .map(e -> Map.entry(e.getKey(), e.getValue().count))
                        .takeWhile(_ -> !downstream.isRejecting())
                        .forEach(downstream::push);
            }
        };

        BinaryOperator<Map<T, Counter>> combiner = (map1, map2) -> {
            map2.forEach((key, counter) -> map1.merge(key, counter,
                    (counter1, counter2) -> {
                        counter1.count += counter2.count;
                        return counter1;
                    })
            );
            return map1;
        };
        return Gatherer.of(
                supplier,
                integrator,
                combiner,
                finisher
        );
    }

    private static void parallel() {
        class State {
            Set<String> threadUpstream = new HashSet<>();
            Set<String> threadGatherer = new HashSet<>();
            Set<String> threadDownstream = new HashSet<>();
        }

        var result = IntStream.range(0, 1_000_000)
                .parallel()
                .mapToObj(_ -> Thread.currentThread().getName())
                .distinct()
                .gather(Gatherer.<String, State, State>of(
                        State::new,
                        (state, element, _) -> {
                            state.threadUpstream.add(element);
                            state.threadGatherer.add(Thread.currentThread().getName());
                            return true;
                        },
                        (state1, state2) -> {
                            state1.threadUpstream.addAll(state2.threadUpstream);
                            state1.threadGatherer.addAll(state2.threadGatherer);
                            return state1;
                        },
                        ((state, downstream) ->
                                IntStream.range(0, 100)
                                        .forEach(_ -> downstream.push(state)))
                ))
                .peek(state -> state.threadDownstream.add(Thread.currentThread().getName()))
                .toList();

        IO.println("threadUpstream = " + result.getFirst().threadUpstream.size());
        IO.println("threadUpstream = " + result.getFirst().threadGatherer.size());
        IO.println("threadUpstream = " + result.getFirst().threadDownstream.size());
    }

    private static void sequential() {
        class State {
            Set<String> threadUpstream = new HashSet<>();
            Set<String> threadGatherer = new HashSet<>();
            Set<String> threadDownstream = new HashSet<>();
        }

        var result = IntStream.range(0, 1_000_000)
                .parallel()
                .mapToObj(_ -> Thread.currentThread().getName())
                .distinct()
                .gather(Gatherer.<String, State, State>ofSequential(
                        State::new,
                        (state, element, downstream) -> {
                            state.threadUpstream.add(element);
                            state.threadGatherer.add(Thread.currentThread().getName());
                            return true;
                        },
                        ((state, downstream) ->
                                IntStream.range(0, 100)
                                        .forEach(_ -> downstream.push(state)))
                ))
                .peek(state -> state.threadDownstream.add(Thread.currentThread().getName()))
                .toList();

        IO.println("threadUpstream = " + result.getFirst().threadUpstream.size());
        IO.println("threadUpstream = " + result.getFirst().threadGatherer.size());
        IO.println("threadUpstream = " + result.getFirst().threadDownstream.size());
    }
}
