package io.github.barhan44.encoding.insight.core.strategy.checker.impl;

import io.github.barhan44.encoding.insight.core.strategy.checker.AbstractChecker;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UTF8Checker extends AbstractChecker {
    @Override
    protected Charset getCharset() {
        return StandardCharsets.UTF_8;
    }
}
