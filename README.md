# Encoding Insight

## Overview

The **Encoding Insight** is a comprehensive library designed for detecting character encodings in text files. It provides a flexible and extensible mechanism for encoding detection with support for various strategies and optimizations.

## Key Features
- **Modular Architecture**: Easily add new detection strategies
- **Flexible Configuration**: Customize settings via the Configuration interface
- **Performance Optimization**: Supports result caching
- **Extensibility**: Create custom detection strategies
- **Ease of Use**: Convenient Builder pattern for detector creation

## Getting Started

### Basic Usage Example

```java
import api.core.io.github.barhan44.encoding.insight.DetectionResult;
import api.core.io.github.barhan44.encoding.insight.EncodingInsight;
import detector.core.io.github.barhan44.encoding.insight.EncodingDetector;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

EncodingDetector detector = EncodingInsight.getDefaultDetector();

// Detect encoding from file
DetectionResult result = detector.detect(new File("example.txt"));

// Detect encoding from InputStream
try(
InputStream input = Files.newInputStream(Paths.get("example.txt"))){
DetectionResult result = detector.detect(input);
}
```

## Core Components
- **EncodingDetector** — main interface for encoding detection
- **DetectionStrategy** — defines the encoding detection strategy
- **Configuration** — detector configuration settings
- **OptimizationOptions** — optimization options
- **DetectionResult** — detection result object

## Advanced Features

### Customizing the Detector (in development)

```java

import api.core.io.github.barhan44.encoding.insight.EncodingInsight;
import detector.core.io.github.barhan44.encoding.insight.EncodingDetector;
import impl.detector.core.io.github.barhan44.encoding.insight.UTF8EncodingDetector;
import impl.optimization.core.io.github.barhan44.encoding.insight.DefaultOptimizationOptions;
import io.github.barhan44.encoding.insight.core.strategy.impl.UTF8DetectionStrategy;

EncodingDetector detector = EncodingInsight.getDetector(
        new io.github.barhan44.encoding.insight.configuration.DefaultConfiguration(),
        new UTF8DetectionStrategy(),
        new DefaultOptimizationOptions(),
        UTF8EncodingDetector.class
);
```

## Documentation

Full documentation is available in the JavaDoc generated from source code comments.

## License

The library is distributed under the **MIT License**.