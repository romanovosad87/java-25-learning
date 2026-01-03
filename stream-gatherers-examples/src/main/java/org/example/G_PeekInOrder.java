package org.example;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Gatherer;

public class G_PeekInOrder {
    static void main() {
        var ints = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        ints.parallelStream()
                .peek(IO::println)
                .forEach(_ -> {
                });

        IO.println("-------------");

        ints.parallelStream()
                .gather(peekInOrder(IO::println))
                .forEach(_ -> {
                });
    }

    private static Gatherer<? super Integer, ?, Integer> peekInOrder(Consumer<Integer> consumer) {
        return Gatherer.ofSequential((_, element, downstream) -> {
            consumer.accept(element);
            return downstream.push(element);
        });
    }
}
