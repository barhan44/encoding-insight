package io.github.barhan44.encoding.insight.core.strategy.impl;

import io.github.barhan44.encoding.insight.core.strategy.AbstractDetectionStrategy;
import io.github.barhan44.encoding.insight.core.strategy.checker.impl.Win1251Checker;
import io.github.barhan44.encoding.insight.core.strategy.checker.impl.UTF8Checker;

public class DefaultDetectionStrategy extends AbstractDetectionStrategy {
    public DefaultDetectionStrategy() {
        addChecker(new UTF8Checker());
        addChecker(new Win1251Checker());
    }
}
