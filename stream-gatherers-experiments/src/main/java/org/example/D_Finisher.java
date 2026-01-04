package org.example;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Gatherer;

public class D_Finisher {
    static void main() {

        var strings = List.of("one", "two", "three", "four", "five");



        // sorting


        Gatherer<? super String, ?, ?> sortingGatherer = Gatherer.ofSequential(
                TreeSet::new,
                ((set, element, _) -> {
                    set.add(element);
                    return true;
                }),
                (set, downstream) -> set.stream()
                        .allMatch(downstream::push)
//                        .takeWhile(_ -> !downstream.isRejecting())
//                        .forEach(downstream::push)
        );
        IO.println(strings.stream()
                .gather(sortingGatherer)
                .limit(3)
                .toList());

    }
}
