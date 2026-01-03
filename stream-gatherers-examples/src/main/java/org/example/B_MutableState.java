package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

public class B_MutableState {

    static void main() {

        var ints = List.of(1, 2, 3, 4, 5, 4, 3, 2, 1);

        // dropWhile (close / open gate)
        Predicate<Integer> integerPredicate = element -> element < 4;

        class Gate {
            boolean open = false;
        }
        Gatherer<? super Integer, ?, Integer> dropWhile = Gatherer.ofSequential(
                Gate::new,
                (gate, element, downstream) -> {
                    if (gate.open){
                        return downstream.push(element);
                    } else if (!integerPredicate.test(element)) {
                        gate.open = true;
                        return downstream.push(element);
                    } else {
                        return true;
                    }
                }
        );
        IO.println(ints.stream()
                .gather(dropWhile)
//                .dropWhile(integerPredicate)
                .toList());

        // distinct


        Gatherer<Integer,?, Integer> distinct = Gatherer.ofSequential(
                HashSet::new,
                ((state, element, downstream) -> {
                    if (state.add(element)) {
                        downstream.push(element);
                    }
                    return true;
                })
        );

        IO.println(ints.stream()
                .gather(distinct)
//                .distinct()
                .toList());


    }
}
