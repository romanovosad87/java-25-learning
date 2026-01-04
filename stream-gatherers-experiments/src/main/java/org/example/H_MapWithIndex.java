package org.example;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;

public class H_MapWithIndex {
    static void main() {
        var names = List.of("Alex", "Maria", "John", "Sophie", "Daniel", "Elena", "Michael", "Anna", "Thomas", "Olivia");

        names.stream()
                .map(String::toUpperCase)
                .forEach(IO::println);

        IO.println("------------------");

        names.stream()
//                .gather(mapWithIndex(String::toUpperCase))
                .gather(mapWithIndexImproved(String::toUpperCase))
                .forEach(IO::println);
    }

    static class Index {
        private int position = 0;

        public int getIncrement() {
            return position++;
        }
    }

    record ValueWithIndex<E>(E value, int index) {
        @Override
        public String toString() {
            return "%d: %s".formatted(index, value);
        }
    }


    // do not mutation visible outside your functional pipeline
    // use internal data not visible outside
    private static Gatherer<? super String, ?, String> mapWithIndex(Function<String, String> function) {
        class Count {
            long count = 0;
        }
        return Gatherer.ofSequential(
                Count::new,
                (state, element, downstream) -> {
                    String applied = function.apply(element);
                    return downstream.push("%s : %s".formatted(state.count++, applied));
                }
        );
    }

    private static <T, R>Gatherer<T, ?, ValueWithIndex<R>> mapWithIndexImproved(Function<T, R> function) {
        return Gatherer.ofSequential(
                Index::new,
                (state, element, downstream) -> {
                    var valueWithIndex = new ValueWithIndex<>(function.apply(element), state.getIncrement());
                    return downstream.push(valueWithIndex);
                }
        );
    }
}
