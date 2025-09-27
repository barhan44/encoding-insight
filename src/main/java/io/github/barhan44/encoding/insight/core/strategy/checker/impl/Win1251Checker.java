package io.github.barhan44.encoding.insight.core.strategy.checker.impl;

import io.github.barhan44.encoding.insight.core.strategy.checker.AbstractChecker;

import java.nio.charset.Charset;

public class Win1251Checker extends AbstractChecker {

    @Override
    protected Charset getCharset() {
        return Charset.forName("Windows-1251");
    }
}
