package io.github.barhan44.encoding.insight.core.strategy;

import io.github.barhan44.encoding.insight.core.api.DetectionResult;
import io.github.barhan44.encoding.insight.core.strategy.checker.EncodingChecker;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDetectionStrategy implements DetectionStrategy {
    protected final List<EncodingChecker> checkers = new ArrayList<>();

    public void addChecker(EncodingChecker checker) {
        checkers.add(checker);
    }

    @Override
    public DetectionResult detect(byte[] data) {
        for (EncodingChecker checker : checkers) {
            Charset result = checker.check(data);
            if (result != null) {
                return new DetectionResult(true, result);
            }
        }
        return new DetectionResult(false, null);
    }
}
