package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Gatherer;

public class E_Parallel {
    static void main() {

        var ints = List.of(1, 2, 3, 4, 5, 4, 3, 2, 1);

        // if execute in parallel each thread will have its own HashSet
        Gatherer<Integer, ?, Integer> distinct =
                Gatherer.of(
                        () -> new HashSet<Integer>(),
                        (set, element, _) -> {
                            set.add(element);
                            return true;
                        },
                        (set1, set2) -> {
                            set1.addAll(set2);
                            return set1;
                        },
                        (set, downstream) -> set.forEach(downstream::push)

                );
        System.out.println(ints.stream()
//                .distinct()
                .gather(distinct)
                .toList());

    }
}
