package org.example;

import java.time.Duration;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class F_MapConcurrent {
    static void main() {
        //  if you part of your sequential stream to run in parallel then you can use mapConcurrent() method

//        firstExample();
        concurrentMapping();
    }

    private static void firstExample() {
        IO.println(Stream.of(1, 2, 10, 20, 30, 40)
                .gather(
                        Gatherers.mapConcurrent(5, n -> {
                            try {
                                Thread.sleep(n * 1000);
                            } catch (InterruptedException _) {
                                IO.println("Task " + n + " was interrupted!");
                                Thread.currentThread().interrupt();
                            }
                            return n;
                        })
                ).limit(2)
                .toList());
    }

    private static void concurrentMapping() {
        long start = System.nanoTime();
        var events = IntStream.range(0, 100)
                .boxed()
//                .parallel().map(element -> slowIo(element))
                .gather(Gatherers.mapConcurrent(100, element -> slowIo(element)))
                .toList();
        long stop = System.nanoTime();
        IO.println("took " + Duration.ofNanos(stop - start) + " to generate " + events);
    }

    private static int slowIo(int operand) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return operand * 2;
    }
}
