package org.example.utils;

import java.util.Objects;


/// # AccountByFirstName
///
/// A helper record used to achieve **`distinct()` by a specific field**
/// (`firstName`) **before Stream Gatherers were available**.
///
/// ## The problem
///
/// The Stream API does not provide a built-in way to perform:
///
/// ```text
/// distinct by some property
/// ```
///
/// The standard `distinct()` operation relies on `equals()` and `hashCode()`
/// of the stream elements themselves.
///
/// ## The workaround
///
/// This record wraps an `Account` and **redefines equality**
/// to consider **only `firstName`**:
///
/// - `equals()` compares `account.firstName()`
/// - `hashCode()` is derived from `account.firstName()`
///
/// This allows `distinct()` to behave as *distinct-by-first-name*.
///
/// ## Usage pattern
///
/// ```java
/// accounts.stream()
///         .map(AccountByFirstName::new)
///         .distinct()
///         .map(AccountByFirstName::account)
///         .forEach(System.out::println);
/// ```
///
/// ## Why this was needed
///
/// Before Stream Gatherers:
///
/// - there was no way to introduce **stateful intermediate operations**
/// - `distinct()` could not be customized
/// - developers had to rely on **wrapper types** or
///   **custom `equals()` hacks**
///
/// ## Drawbacks of this approach
///
/// - introduces **artificial wrapper types**
/// - obscures intent
/// - couples equality semantics to a specific use case
///
/// ## Modern alternative
///
/// With **Stream Gatherers**, this logic can be expressed directly
/// as a **custom intermediate operation**, without modifying
/// equality semantics or creating wrapper records.
///
/// ## Key takeaway
///
/// This record represents a **pre-Gatherer workaround** for
/// property-based distinct operations in Java Streams.
public record AccountByFirstName(
        Account account
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountByFirstName(Account account1))) {
            return false;
        }
        return Objects.equals(account.firstName(), account1.firstName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(account.firstName());
    }
}
