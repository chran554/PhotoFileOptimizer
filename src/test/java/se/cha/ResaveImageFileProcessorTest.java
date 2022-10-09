package se.cha;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ResaveImageFileProcessorTest {

    @Ignore
    @Test
    public void testResave() {
        final ResaveImageFileProcessor resaveImageFileProcessor = new ResaveImageFileProcessor();
        final File testfileDirectory = new File("testfiles");
        final File[] files = testfileDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        for (File file : files) {
            resaveImageFileProcessor.processFile(null, file, 0.75);
        }

        assertTrue(true);
    }

}