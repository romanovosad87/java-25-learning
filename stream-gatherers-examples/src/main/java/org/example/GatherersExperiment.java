package org.example;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class GatherersExperiment {

    static void main() {

        var strings = List.of("one", "two", "three", "four", "five");

        Gatherer<?, ?, ?> gatherer = () -> (state, element, downstream) -> true;
        Gatherer<?, ?, ?> gathererOf = Gatherer.of(
                (_, element, downstream) -> true
        );

        Gatherer<String, Void, String> mappingGatherer = Gatherer.of(
                ((_, element, downstream) -> {
                    downstream.push(element.toUpperCase());
                    return true;
                }
                ));

        Gatherer<String, Void, String> filteringGatherer = Gatherer.of(
                ((_, element, downstream) -> {
                    if (element.length() == 3) {
                        downstream.push(element.toUpperCase());
                    }
                    return true;
                }
                ));

        Gatherer<String, Void, String> filteringGathererImproved = Gatherer.of(
                ((_, element, downstream) -> {
                    if (element.length() == 3) {
                        return downstream.push(element.toUpperCase());
                    }
                    return true;
                }
                ));


        IO.println(strings.stream()
                .gather(filteringGatherer)
                .toList());


        IO.println(Stream.of(1, 2, 3, 4)
                .map(i -> i + 1)
//                .gather(map(i -> i + 1))
                .toList());

        IO.println(Stream.of(List.of(1, 2), List.of(3, 4))
//                .mapMulti((element, consumer) -> element.forEach(consumer))
                .gather(mapMulti((element, consumer) -> element.forEach(consumer)))
                .toList());


        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
//                .limit(2)
                .gather(limit(2))
                .toList());

        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(Gatherers.fold(() -> 0, (sum, i) -> sum + i))
                .toList());

        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(Gatherers.scan(() -> 0, (sum, i) -> sum + i))
                .toList());

        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(Gatherers.windowFixed(4))
                .toList());

        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(Gatherers.windowSliding(4))
                .toList());



        IO.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(zipWithIndex((idx, number) -> idx + " - " + number))
                .toList());
    }

    static <T, R> Gatherer<T, ?, R> map(Function<? super T, ? extends R> mapper) {
        return Gatherer.of(
                (_, element, downstream) -> downstream.push(mapper.apply(element))
        );
    }

    static <T, R> Gatherer<T, ?, R> mapMulti(BiConsumer<? super T, Consumer<? super R>> mapper) {
        return Gatherer.of(
                (_, element, downstream) -> {
                    mapper.accept(element, element1 -> downstream.push(element1));
                    return !downstream.isRejecting();
                }
        );
    }

    static <T> Gatherer<T, ?, T> limit(long mazSize) {
        class Count {
            long left = mazSize;
        }

        return Gatherer.ofSequential(Count::new,
                (count, element, downstream) -> {
                    if (count.left <= 0) {
                        return false;
                    }
                    count.left -= 1;
                    return downstream.push(element) && count.left > 0;
                });
    }

    static <T, R> Gatherer<T, ?, R> zipWithIndex(BiFunction<Long, T, R> zipper) {
        class State {
            long index;
        }
        return Gatherer.ofSequential(
                State::new,
                ((state, element, downstream) ->
                    downstream.push(zipper.apply(state.index++, element)))

        );
    }
}
