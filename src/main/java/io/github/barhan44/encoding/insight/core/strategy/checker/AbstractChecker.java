package io.github.barhan44.encoding.insight.core.strategy.checker;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public abstract class AbstractChecker implements EncodingChecker {
    protected abstract Charset getCharset();

    @Override
    public Charset check(byte[] data) {
        Charset charset = getCharset();
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            decoder.decode(ByteBuffer.wrap(data));
            return charset;
        } catch (Exception e) {
            return null;
        }
    }
}
