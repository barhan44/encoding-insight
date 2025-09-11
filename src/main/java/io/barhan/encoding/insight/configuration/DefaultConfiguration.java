package io.barhan.encoding.insight.configuration;

public class DefaultConfiguration implements Configuration {

    @Override
    public int getBufferSize() {
        return 8192;
    }
}
