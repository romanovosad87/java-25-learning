# 🎉 Crazy-Gatherers

The `crazy-streams` module contains the **`CrazyGatherers`** class, which provides a series of hands-on exercises designed to help you **master Java Stream Gatherers** .

The exercises in `CrazyGatherers` progress from simple operations—like filtering, mapping, and printing elements—to more advanced stream operations such as folding, scanning, windowing, and handling parallel streams. Each exercise demonstrates how custom Gatherers can mimic or extend standard stream operations, providing a practical way to understand the mechanics of streams.

The name "CrazyGatherers" was inspired by the set of **'Crazy' exercises by Bobocode**, which you can find here:
- [Crazy exercises by Bobocode](https://github.com/bobocode-projects/java-fundamentals-exercises/tree/main/5-0-functional-programming)

## 🛠 Requirements

You should have **Java 25** installed to compile and run this module.

## ✨ Features

- 🏗 Hands-on exercises for learning custom Gatherers.
- 📌 Examples of standard stream operations using Gatherers:
    - `filter`, `map`, `peek`, `flatMap`, `limit`, `skip`, `takeWhile`, `dropWhile`, `distinct`, `sorted`
- 🚀 Advanced custom operations:
    - Folding and scanning elements
    - Windowing elements into batches
    - Collapsing consecutive duplicates
    - Sampling elements at regular intervals
    - Extracting increasing sequences
- 🔄 Sequential and parallel stream handling

## ✅ Tests and Completed Solutions

Each method in `CrazyGatherers` is covered by tests in `org.example.CrazyGatherersTest`. You can run these tests to validate your solutions.

If you ever get stuck or want to see reference implementations, you can check the **`completed`** branch in the project.
