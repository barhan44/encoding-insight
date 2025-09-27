package io.github.barhan44.encoding.insight.configuration;

public class DefaultConfiguration implements Configuration {

    @Override
    public int getBufferSize() {
        return 1024;
    }

    @Override
    public boolean isStrictDetection() {
        return false;
    }

    @Override
    public int getMaxReadBytes() {
        return 4096;
    }
}
