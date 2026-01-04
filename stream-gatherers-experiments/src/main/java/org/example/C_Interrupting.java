package org.example;

import java.util.List;
import java.util.stream.Gatherer;

public class C_Interrupting {
    static void main() {

        var ints = List.of(1, 2, 3, 4, 5, 4, 3, 2, 1);

        // Rejecting state
        // 1) it starts in non-rejecting isRejecting() = false
        // 2) it can never switch from rejecting to non-rejecting
        // 3) it can only switch on a call to push


        System.out.println(ints.stream()
//                .limit(4)
                .gather(limit(4))
                .limit(2)
                .toList());
    }

    static <T> Gatherer<T, ?, T> limit(long size) {
        class Count {
            long size = 1;
        }
       return Gatherer.ofSequential(
               Count::new,
               ((count, element, downstream) -> {
//                   if (downstream.isRejecting()) {
//                       return false;
//                   } DON'T DO THAT as rejecting can only switch on a call to push
                   if (count.size++ <= size) {
                       return downstream.push(element);
                   } else {
                       return false;
                   }
               })
       );
    }
}
