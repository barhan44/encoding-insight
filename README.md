# Encoding Insight

## Overview

A modular Java library for detecting character encodings in byte arrays, input streams and files, with primary support
for UTF-8 and Windows-1251 encodings. Designed with extensibility, clarity, and ease of use in mind.

***

## Key Features

- Detection of UTF-8 (ASCII, byte order mark, multibyte sequences) and Windows-1251 text.
- Composite detector that combines multiple detection strategies and returns the result with the highest confidence.
- Factory for registering and creating detectors by name.
- Lightweight and modular architecture separating detection logic from the client-facing API.
- Bounded sampling for files and streams: only a configurable prefix (32 KB by default) is read, never the whole file.
- Binary-data heuristic that rejects NUL bytes and excessive control characters instead of misclassifying them.
- Incomplete UTF-8 characters at the sample boundary are trimmed before detection.
- Confidence levels (0–100) and a ready-to-use `java.nio.charset.Charset` returned with detection results.
- Unit and integration tests covering empty input, BOM handling, broken byte sequences, binary data, large files and
  concurrent access.
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
    <version>1.1.0</version>
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
                    + " (" + result.getCharset() + ")"
                    + " with confidence: " + result.getConfidence() + "%");
        } else {
            System.out.println("Encoding could not be determined.");
        }
    }
}
```

`detectFile` analyzes at most the configured sample size (32 KB by default). The sample size can be changed through
`new EncodingInsight(maxSampleBytes)` or `new EncodingDetectionService(composite, maxSampleBytes)`.

Streams are supported without loading the whole stream or closing it:

```java
import java.io.InputStream;

InputStream input = ...;
DetectionResult result = service.detect(input); // reads at most 32 KB; caller closes the stream
```

To obtain all candidate results instead of the best one:

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
- `detectFile` and stream detection sample a configurable prefix (32768 bytes by default). Detection results for a
  file whose encoding changes or whose distinguishing content appears after that prefix may differ from a full-file
  analysis.
- Binary data is rejected by a conservative heuristic (NUL bytes or an excessive C0 control-byte ratio). The heuristic
  can be bypassed by passing bytes directly to a detector, but the composite detector and service apply it.
- Detection is heuristic: confidence reflects accumulated signals, not a formal proof of the encoding.

## Testing

Run the full build, all tests and the coverage report with Maven:

```bash
mvn clean verify
```

The JaCoCo report is generated in `encoding-insight-core/target/site/jacoco/index.html` and
`encoding-insight-api/target/site/jacoco/index.html`.

To run a single test class or method:

```bash
mvn -pl encoding-insight-core test -Dtest=UTF8EncodingDetectorEdgeCaseTest
mvn -pl encoding-insight-core test -Dtest=UTF8EncodingDetectorEdgeCaseTest#detectsValidCyrillicUtf8WithoutBom
```

## Documentation

Full documentation is available in the JavaDoc generated from source code comments.

## License

The library is distributed under the **MIT License**. See the LICENSE file for details.
