package org.example;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/// # Gatherer Parallelism Demo
///
/// Shows how **parallel** and **sequential** Stream Gatherers
/// behave inside a **parallel stream pipeline**.
///
/// The demo highlights where parallelism is:
/// - preserved (`Gatherer.of(...)`)
/// - restricted (`Gatherer.ofSequential(...)`)
public class GathererParallelismDemo {
    static void main() {
        sequential();
//        parallel();
    }

    /// # Sequential Gatherer on a Parallel Stream
    ///
    /// Demonstrates how a **sequential gatherer**
    /// behaves when applied to a **parallel stream**.
    ///
    /// Even though the upstream pipeline is parallel,
    /// the gatherer itself is executed **on a single thread**.
    ///
    /// ## Observed stages
    ///
    /// | Stage | Expected behavior |
    /// |------|-------------------|
    /// | **Upstream** | Multiple threads |
    /// | **Gatherer** | **Single thread** |
    /// | **Downstream** | Multiple threads |
    ///
    /// ## Why `Gatherer.ofSequential(...)`
    ///
    /// `Gatherer.ofSequential(...)` introduces a **parallelism barrier**:
    ///
    /// - the accumulator is executed by exactly one thread
    /// - no combiner is required
    /// - mutable state does not need to be thread-safe
    ///
    /// ## Execution model
    ///
    /// ```text
/// parallel upstream
///     ↓
/// sequential gatherer (single State)
///     ↓
/// parallel downstream
/// ```
    ///
    /// ## When to use this
    ///
    /// Sequential gatherers are appropriate when:
    ///
    /// - ordering must be preserved
    /// - state mutation is complex
    /// - correctness is more important than throughput
    ///
    /// ## Key takeaway
    ///
    /// `ofSequential()` allows **local, deterministic logic**
    /// inside an otherwise parallel stream pipeline.
    private static void sequential() {
        class State {
            final Set<String> threadUpstream = new HashSet<>();
            final Set<String> threadGatherer = new HashSet<>();
            final Set<String> threadDownstream = new HashSet<>();
        }

        var result = IntStream.range(0, 1_000_000)
                .parallel()
                .mapToObj(_ -> Thread.currentThread().getName())
                .distinct()
                .gather(Gatherer.<String, State, State>ofSequential(
                        State::new,
                        (state, element, downstream) -> {
                            state.threadUpstream.add(element);
                            state.threadGatherer.add(Thread.currentThread().getName());
                            return true;
                        },
                        ((state, downstream) ->
                                IntStream.range(0, 100)
                                        .forEach(_ -> downstream.push(state)))
                ))
                .peek(state -> state.threadDownstream.add(Thread.currentThread().getName()))
                .toList();

        IO.println("threadUpstream = " + result.getFirst().threadUpstream.size());
        IO.println("threadUpstream = " + result.getFirst().threadGatherer.size());
        IO.println("threadUpstream = " + result.getFirst().threadDownstream.size());
    }

    /// # Parallel Gatherer execution
    ///
    /// Demonstrates how a **parallel [java.util.stream.Gatherer]** behaves when used in a
    /// **parallel stream pipeline**.
    ///
    /// This method intentionally observes **thread participation**
    /// at three distinct stages of stream execution.
    ///
    /// ## Observed stages
    ///
    /// | Stage | Description |
    /// |------|-------------|
    /// | **Upstream** | Threads executing the stream before the gatherer |
    /// | **Gatherer** | Threads executing the gatherer accumulator |
    /// | **Downstream** | Threads consuming elements emitted by the gatherer |
    ///
    /// Each stage records thread names in a `Set`, allowing us to measure
    /// how many **distinct threads** participated.
    ///
    /// ## Why `Gatherer.of(...)`
    ///
    /// `Gatherer.of(...)` creates a **parallel-capable gatherer**:
    ///
    /// - each worker thread owns its own `State` instance
    /// - partial states are merged using the **combiner**
    /// - no single-thread execution guarantee is provided
    ///
    /// ## Execution model
    ///
    /// ```text
/// parallel upstream
///     ↓
/// parallel gatherer (multiple State instances)
///     ↓
/// state combiner
///     ↓
/// parallel downstream
/// ```
    ///
    /// ## Important details
    ///
    /// - `[IntStream#parallel()]` enables parallel execution
    /// - `[IntStream#distinct()]` ensures one element per upstream thread
    /// - the finisher emits the same state **100 times** to amplify downstream effects
    /// - the combiner merges thread sets from multiple states
    ///
    /// ## Key takeaway
    ///
    /// In a **parallel gatherer**, **all three stages** — upstream, gatherer,
    /// and downstream — may execute on **multiple threads**.
    ///
    /// This requires:
    ///
    /// - thread-confined mutable state
    /// - deterministic and associative state merging
    private static void parallel() {
        class State {
            final Set<String> threadUpstream = new HashSet<>();
            final Set<String> threadGatherer = new HashSet<>();
            final Set<String> threadDownstream = new HashSet<>();
        }

        var result = IntStream.range(0, 1_000_000)
                .parallel()
                .mapToObj(_ -> Thread.currentThread().getName())
                .distinct()
                .gather(Gatherer.<String, State, State>of(
                        State::new,
                        (state, element, _) -> {
                            state.threadUpstream.add(element);
                            state.threadGatherer.add(Thread.currentThread().getName());
                            return true;
                        },
                        (state1, state2) -> {
                            state1.threadUpstream.addAll(state2.threadUpstream);
                            state1.threadGatherer.addAll(state2.threadGatherer);
                            return state1;
                        },
                        ((state, downstream) ->
                                IntStream.range(0, 100)
                                        .forEach(_ -> downstream.push(state)))
                ))
                .peek(state -> state.threadDownstream.add(Thread.currentThread().getName()))
                .toList();

        IO.println("threadUpstream = " + result.getFirst().threadUpstream.size());
        IO.println("threadUpstream = " + result.getFirst().threadGatherer.size());
        IO.println("threadUpstream = " + result.getFirst().threadDownstream.size());
    }
}
