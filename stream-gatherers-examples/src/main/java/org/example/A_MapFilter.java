package org.example;

import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class A_MapFilter {

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
                        return downstream.push(element.toUpperCase());
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
                .limit(1)
                .toList());

    }
}
