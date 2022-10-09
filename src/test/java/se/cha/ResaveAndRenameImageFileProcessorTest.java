package se.cha;

import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class ResaveAndRenameImageFileProcessorTest {

    @Ignore
    @Test
    public void testResave() {
        final FileProcessor fileProcessor = new ResaveAndRenameImageFileProcessor();
        final File testfileDirectory = new File("testfiles");
        final File[] files = testfileDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        final Thread thread = fileProcessor.processFiles(Arrays.asList(files), getProgressListener());

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

    private ProgressListener getProgressListener() {
        return new ProgressListener() {
            @Override
            public void setImage(Image image) {
                // Nothing by intention
            }

            @Override
            public void resetProgress(String message) {
                // Nothing by intention
            }

            @Override
            public void setProgress(int current, int max, String message) {
                // Nothing by intention
            }
        };
    }

}