package io.github.barhan44.encoding.insight.core.strategy.impl;

import io.github.barhan44.encoding.insight.core.api.DetectionResult;
import io.github.barhan44.encoding.insight.core.strategy.DetectionStrategy;

import java.nio.charset.StandardCharsets;

public class UTF8DetectionStrategy implements DetectionStrategy {
    @Override
    public DetectionResult detect(byte[] data) {
        if (data == null || data.length == 0) {
            return new DetectionResult(false, null);
        }

        if (isUTF8BOM(data)) {
            return new DetectionResult(true, StandardCharsets.UTF_8);
        }

        return detectUTF8Sequence(data);
    }

    private boolean isUTF8BOM(byte[] data) {
        return data.length >= 3 &&
                data[0] == (byte) 0xEF &&
                data[1] == (byte) 0xBB &&
                data[2] == (byte) 0xBF;
    }

    private DetectionResult detectUTF8Sequence(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];

            if (b >= 0) {
                continue;
            }

            int bytesToFollow = 0;
            switch (b & 0b11110000) {
                case 0b11000000:
                    bytesToFollow = 1;
                    break;
                case 0b11100000:
                    bytesToFollow = 2;
                    break;
                case 0b11110000:
                    bytesToFollow = 3;
                    break;
                default:
                    return new DetectionResult(false, null);
            }

            if (i + bytesToFollow >= data.length) {
                return new DetectionResult(false, null);
            }

            for (int j = 1; j <= bytesToFollow; j++) {
                if ((data[i + j] & 0b11000000) != 0b10000000) {
                    return new DetectionResult(false, null);
                }
            }
            i += bytesToFollow;
        }
        return new DetectionResult(true, StandardCharsets.UTF_8);
    }
}
