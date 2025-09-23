package io.github.barhan44.encoding.insight.core.api;

import java.nio.charset.Charset;

public class DetectionResult {
    private final boolean success;
    private final Charset charset;

    public DetectionResult(boolean success, Charset charset) {
        this.success = success;
        this.charset = charset;
    }

    public boolean isSuccess() {
        return success;
    }

    public Charset getCharset() {
        return charset;
    }
}
