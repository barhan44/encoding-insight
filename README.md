# Encoding Insight

## Overview

A modular Java library for detecting character encodings in byte streams and files, with primary support for UTF-8 and
Windows-1251 encodings. Designed with extensibility, clarity, and ease of use in mind.

***

## Key Features

- Accurate detection of UTF-8 and Windows-1251 encodings.
- Composite detector allowing multiple encoding detection strategies.
- Factory pattern to easily add or customize detectors.
- Lightweight and modular architecture separating core detection logic from API.
- Utilities to help read files and convert encoding.
- Confidence levels returned with detection results.
- Fully tested with unit and integration tests.
- Simple, intuitive API facade for quick integration.

## Project Structure

- **Core Module** (`encoding-insight-core`):
  Contains detection algorithms, detector interfaces, and core data models.
- **API Module** (`encoding-insight-api`):
  Provides facade classes, services, and utilities for easier usage.

## Quick Start

### Maven Dependency

(Add the built artifact to your project dependencies or install locally.)

### Usage Example

```java
import io.github.barhan44.api.EncodingInsight;
import io.github.barhan44.api.EncodingDetectionService;
import io.github.barhan44.core.model.EncodingDetectionResult;

public class Main {
    public static void main(String[] args) throws Exception {
        EncodingInsight library = new EncodingInsight();
        EncodingDetectionService service = library.getDetectionService();

        DetectionResult result = service.detectFile("example.txt");
        if (result != null) {
            System.out.println("Detected encoding: " + result.getEncoding() +
                               " with confidence: " + result.getConfidence() + "%");
        } else {
            System.out.println("Encoding could not be determined.");
        }
    }
}
```

***

## Adding Custom Detectors

Use the `EncodingDetectorFactory` to register and create custom encodings:

```java
EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
factory.registerDetector("Custom-Encoding", CustomEncodingDetector.class);

EncodingDetector customDetector = factory.createDetector("Custom-Encoding");
```

***

## Contributing

Contributions are welcome! Please follow standard open-source contribution practices:

- Fork the repository.
- Create a feature branch.
- Commit your changes with clear messages.
- Submit a pull request.

***

## Testing

Run tests using Maven:

```bash
mvn clean test
```

Includes both unit and integration tests covering detection accuracy and API functionality.

***

## Documentation

Full documentation is available in the JavaDoc generated from source code comments.

## License

The library is distributed under the **MIT License**. See LICENSE file for details.

***

This library provides a robust foundation for encoding detection in Java applications, enabling easier handling of text files with varied character encodings.