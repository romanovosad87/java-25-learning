package org.example;

import java.util.List;

public class H_Reduce {
    static void main() {
        var ints = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        System.out.println(ints.parallelStream()
                .map(e -> e * 10)
                .reduce(10, Integer::sum));

        IO.println("-----------");

        System.out.println(ints.parallelStream()
                .map(e -> e * 10)
                .reduce(0, Integer::sum) + 10);
    }
}
