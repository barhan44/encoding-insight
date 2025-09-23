package io.github.barhan44.encoding.insight.configuration;

public class DefaultConfiguration implements Configuration {

    @Override
    public int getBufferSize() {
        return 8192;
    }
}
