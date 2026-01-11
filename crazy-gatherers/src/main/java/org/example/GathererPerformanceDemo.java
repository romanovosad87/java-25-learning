package org.example;

import org.example.utils.Account;
import org.example.utils.DataUtils;

import java.util.function.Function;
import java.util.stream.Gatherer;

/// # Stream vs Gatherer Execution Timing
///
/// Demonstrates the difference in execution time between:
/// 1. Standard `map()` on a stream
/// 2. A sequential `Gatherer` (`Gatherer.ofSequential(...)`)
///
/// ## Behavior
///
/// - Standard `map()` is **stateless and predictable**.
///   The stream knows that mapping does not affect the final result (count),
///   so it can optimize execution and avoid unnecessary object creation.
///   As a result, `.count()` runs extremely fast.
///
/// - A sequential `Gatherer` is **opaque and potentially stateful**.
///   The stream cannot make assumptions:
///     - Each element must go through the gatherer fully
///     - Objects are created and pushed downstream
///   This adds overhead even for a simple transformation, making it slower.
///
/// ## Observed result
///
/// ```text
/// Standard map() execution time: 1 ms
/// Gatherer execution time: 22 ms
/// ```
///
/// ## Key takeaway
///
/// Gatherers provide **flexible, stateful intermediate operations**, but this flexibility
/// comes at the cost of **execution speed** for simple stateless transformations.
/// Use gatherers when you need **custom intermediate processing**, otherwise `map()` is faster.
public class GathererPerformanceDemo {
    static void main() {
       var accounts = DataUtils.randomAccounts(100_000);


        long startMap = System.nanoTime();
        var namesMap = accounts.stream()
                .map(account -> account.firstName() + " " + account.lastName())
                .count();
        long endMap = System.nanoTime();
        IO.println("Standard map() execution time: " + ((endMap - startMap)/1_000_000) + " ms");

        long startGatherer = System.nanoTime();
        var namesGatherer = accounts.stream()
                .gather(mapping(account -> account.firstName() + " " + account.lastName()))
                .count();
        long endGatherer = System.nanoTime();
        IO.println("Gatherer execution time: " + ((endGatherer - startGatherer)/1_000_000) + " ms");
    }


    private static Gatherer<? super Account, ?, String> mapping(Function<Account, String> function) {
        return Gatherer.ofSequential(
                ((_, element, downstream) -> downstream.push(function.apply(element)))
        );
    }
}
