# ROP Orient Express - Result Utility Library

## Overview

The **Result Utility Library** is a Java implementation of 
the "Result" monad, providing a structured way to represent success 
and failure states in functional programming paradigms. 
Inspired by functional languages like Rust and Haskell, this library enables safer 
error handling without relying on exceptions.

## Purpose

The library aims to simplify error handling by encapsulating operations 
within `Result<S, F>` types, where `S` represents success values and `F` 
represents failure values. This approach allows developers to process results 
in a predictable and composable manner.

## Rationale

Traditional exception handling in Java can be cumbersome and error-prone. 
By using `Result<S, F>`, we avoid unchecked exceptions and enforce explicit 
handling of success and failure cases. This improves code clarity and maintainability.

## Features

- **Creation Methods**

    - `success(S value)`: Creates a successful result.
    - `failure(F value)`: Creates a failure result.
    - `fromOptional(Optional<S> optional, Supplier<F> supplier)`: Converts an `Optional` into a `Result`.
    - `fromPredicate(S value, Predicate<S> predicate, Supplier<F> supplier)`: Evaluates a predicate to determine success or failure.
    - `fromBoolean(Boolean value, Supplier<S> successFn, Supplier<F> failureFn)`: Converts a boolean condition into a `Result`.

- **Transformation Methods**

    - `map(Function<S, S2> mapper)`: Transforms the success value.
    - `mapFailure(Function<F, F2> mapper)`: Transforms the failure value.
    - `mapEither(Function<S, S2> successMapper, Function<F, F2> failureMapper)`: Transforms both success and failure values.

- **Binding Methods**

    - `bind(Function<S, Result<S2, F>> binding)`: Chains operations that return `Result`.
    - `bindFailure(Function<F, Result<S, F2>> binding)`: Chains operations on failures.

- **Utility Methods**

    - `isSuccess() / isFailure()`: Checks the result state.
    - `orThrow()`: Extracts the success value or throws an exception.
    - `toOptional()`: Converts the result to an `Optional`.

## Usage

### Example 1: Basic Success and Failure (constructor)

```java
Result<Integer, String> success = Result.success(42);
Result<Integer, String> failure = Result.failure("Error occurred");
```

### Example 2: Transforming Results (map)

```java
Result<Integer, String> result = Result.success(10);
Result<String, String> transformed = result.map(value -> "Value: " + value);
```

### Example 3: Binding Operations (bind)

```java
Result<Integer, String> divide(int a, int b) {
    return b == 0 ? Result.failure("Cannot divide by zero") : Result.success(a / b);
}

Result<Integer, String> finalResult = divide(10, 2).bind(value -> divide(value, 2));
```

## Installation

TBA

## License

This library is released under the MIT License.

## Contribution

Contributions are welcome! Feel free to submit issues and pull requests to improve this library.

## Contact

For any questions, reach out via GitHub issues.

