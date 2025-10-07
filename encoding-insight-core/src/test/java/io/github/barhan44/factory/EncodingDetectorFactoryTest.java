package io.github.barhan44.factory;

import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.base.AbstractEncodingDetector;
import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EncodingDetectorFactoryTest {
    @Test
    void testDefaultDetectorsRegistered() {
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        EncodingDetector utf8 = factory.createDetector("UTF-8");
        EncodingDetector cp1251 = factory.createDetector("Windows-1251");

        assertNotNull(utf8);
        assertNotNull(cp1251);
        assertEquals("UTF-8 Detector", utf8.getName());
        assertEquals("Windows-1251 Detector", cp1251.getName());
    }

    @Test
    void testRegisterCustomDetector() {
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        factory.registerDetector("Dummy", DummyDetector.class);
        EncodingDetector dummy = factory.createDetector("Dummy");

        assertNotNull(dummy);
        assertEquals("Dummy Detector", dummy.getName());
    }

    private static class DummyDetector extends AbstractEncodingDetector {

        public DummyDetector() {
            super("Dummy Detector", new String[] { "DUMMY" });
        }

        @Override
        protected DetectionResult performDetection(byte[] data) {
            return new DetectionResult("DUMMY", 100, getName());
        }

        @Override
        public boolean canDetect(byte[] data) {
            return true;
        }
    }

}