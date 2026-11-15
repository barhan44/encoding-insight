# Encoding Insight

## Overview

A modular Java library for detecting character encodings in byte arrays and files, with primary support for UTF-8 and
Windows-1251 encodings. Designed with extensibility, clarity, and ease of use in mind.

***

## Key Features

- Detection of UTF-8 (ASCII, byte order mark, multibyte sequences) and Windows-1251 text.
- Composite detector that combines multiple detection strategies and returns the result with the highest confidence.
- Factory for registering and creating detectors by name.
- Lightweight and modular architecture separating detection logic from the client-facing API.
- File utility that reads only a bounded prefix of a file — the whole file is never loaded when a smaller limit is sufficient.
- Confidence levels (0–100) returned with detection results.
- Unit and integration tests covering empty input, BOM handling, broken byte sequences and concurrent access.
- Simple facade for quick integration.

## Project Structure

- **Core Module** (`encoding-insight-core`):
  Contains detection algorithms, the `EncodingDetector` interface (package `io.github.barhan44.api`), the composite
  detector, the factory and the `DetectionResult` model. The core module has no external runtime dependencies.
- **API Module** (`encoding-insight-api`):
  Provides the `EncodingInsight` facade, `EncodingDetectionService` and file utilities. It depends on the core module.

Note: the `io.github.barhan44.api` package is located in the **core** module. The api module contains the root
`io.github.barhan44` package and `io.github.barhan44.utils`.

## Maven Dependency

```xml
<dependency>
    <groupId>io.github.barhan44</groupId>
    <artifactId>encoding-insight-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

If the artifact is not available in a remote repository, install it locally from the project root:

```bash
mvn install
```

## Quick Start

```java
import io.github.barhan44.EncodingDetectionService;
import io.github.barhan44.EncodingInsight;
import io.github.barhan44.model.DetectionResult;

public class Main {
    public static void main(String[] args) throws Exception {
        EncodingInsight library = new EncodingInsight();
        EncodingDetectionService service = library.getDetectionService();

        DetectionResult result = service.detectFile("example.txt");
        if (result != null) {
            System.out.println("Detected encoding: " + result.getEncoding()
                    + " with confidence: " + result.getConfidence() + "%");
        } else {
            System.out.println("Encoding could not be determined.");
        }
    }
}
```

`detectFile` analyzes at most the first 8192 bytes of the file. To obtain all candidate results instead of the best one:

```java
import io.github.barhan44.model.DetectionResult;

import java.util.List;

List<DetectionResult> candidates = service.detectAll(bytes);
```

## Adding Custom Detectors

Register a class that implements `EncodingDetector` (or extends `AbstractEncodingDetector`) and provides a public
no-argument constructor:

```java
import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.composite.CompositeEncodingDetector;
import io.github.barhan44.factory.EncodingDetectorFactory;

EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
factory.registerDetector("Custom-Encoding", CustomEncodingDetector.class);

EncodingDetector customDetector = factory.createDetector("Custom-Encoding");

CompositeEncodingDetector composite = new CompositeEncodingDetector("Custom Composite");
composite.addDetector(customDetector);
```

The factory registry is thread-safe and may be read and modified concurrently; registering detectors during application
startup is still recommended.

## Limitations

- Only UTF-8 and Windows-1251 are supported out of the box; UTF-16/UTF-32 are not implemented.
- `detectFile` samples the first 8192 bytes of a file. Detection results for a file whose encoding changes or whose
  distinguishing content appears after that prefix may differ from a full-file analysis.
- Detection is heuristic: confidence reflects accumulated signals, not a formal proof of the encoding.

## Testing

Run the full build and all tests with Maven:

```bash
mvn clean test
```

To run a single test class or method:

```bash
mvn -pl encoding-insight-core test -Dtest=UTF8EncodingDetectorEdgeCaseTest
mvn -pl encoding-insight-core test -Dtest=UTF8EncodingDetectorEdgeCaseTest#detectsValidCyrillicUtf8WithoutBom
```

## Documentation

Full documentation is available in the JavaDoc generated from source code comments.

## License

The library is distributed under the **MIT License**. See the LICENSE file for details.
