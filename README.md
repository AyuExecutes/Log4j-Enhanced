# Log4j-Enhanced: Memory Logging and Performance Benchmarking 

## Overview
Log4j-Enhanced is a logging framework that extends Apache Log4j by introducing **MemAppender**, an efficient memory logging system, and a **VelocityLayout**, a template-driven log formatter. It enables configurable log retention, supports stress testing, and provides in-depth performance analysis to optimise logging operations.

## Features
- **Custom Memory Logging** (`MemAppender`)
  - Singleton-based log storage.
  - Supports retrieval, formatted output, and automatic log discarding.
- **Advanced Log Formatting** (`VelocityLayout`)
  - Uses Apache Velocity templates for flexible log output.
  - Works seamlessly with `MemAppender` and standard Log4j appenders.
- **Performance Benchmarking** 
  - Compares memory usage, logging speed, and appenders (Console, File, Memory).
  - Evaluates the efficiency of different storage structures (`ArrayList` vs `LinkedList`).
- Comprehensive Testing
  - JUnit tests validate functionality, stress test logging performance.

## Installation
### Prerequisites
- Java 19+
- Apache Maven

## Build and Run
```
git clone https://github.com/AyuExecutes/Log4j-Enhanced.git
cd log4j-enhanced
mvn clean install
mvn test
```

## Performance Insights
Refer to the [Performance Analysis](log4j-enhanced-performance-report.pdf) for detailed insight.



