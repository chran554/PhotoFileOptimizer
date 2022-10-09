package se.cha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class ImageFileProcessorTemplate implements FileProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public Thread processFiles(List<File> files, ProgressListener progressListener) {
        LOGGER.info(LogUtil.format("Scanning", "Initiating work. Scanning for files."));

        final Thread processorThread = new Thread() {
            @Override
            public void run() {
                resetProgress(progressListener, "Scanning for image files...");
                final Set<File> imageFiles = new TreeSet<>(files);

                long lastKeepOsAlive = 0L;

                int amountProcessedFiles = 0;
                for (File imageFile : imageFiles) {
                    LOGGER.info(LogUtil.format("Processing", imageFile, "Processing file " + (amountProcessedFiles + 1) + " of " + imageFiles.size() + "."));

                    setProgress(progressListener, amountProcessedFiles, imageFiles.size(), imageFile);
                    if (progressListener != null) {
                        // progressListener.setImage(null);
                    }

                    lastKeepOsAlive = keepOsAlive(lastKeepOsAlive);

                    processFile(progressListener, imageFile, imageFiles);
                    setProgress(progressListener, ++amountProcessedFiles, imageFiles.size(), null);
                }

                resetProgress(progressListener, "Processed " + amountProcessedFiles + " files");
                LOGGER.info(LogUtil.format("Finish", "Ending work. Processed " + amountProcessedFiles + " files."));
            }

            private long keepOsAlive(long lastKeepOsAlive) {
                // Keep the OS from going to sleep (fake a mouse movement)
                if ((System.currentTimeMillis() - lastKeepOsAlive) > 10L * 1000) {
                    try {
                        final Robot rob = new Robot();
                        final Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                        rob.mouseMove(mouseLoc.x, mouseLoc.y); // Move to same position

                        lastKeepOsAlive = System.currentTimeMillis();

                        LOGGER.trace(LogUtil.format("Keep awake", "OS kept alive by robo-mouse-move..."));
                    } catch (AWTException e) {
                        // Do nothing by intention...
                        e.printStackTrace();
                    }
                }
                return lastKeepOsAlive;
            }
        };

        processorThread.setDaemon(true);
        processorThread.start();

        return processorThread;
    }

    protected abstract void processFile(ProgressListener progressListener, File imageFile, Set<File> imageFiles);

    private void resetProgress(ProgressListener progressListener, String message) {
        if (progressListener != null) {
            progressListener.resetProgress(message);
            progressListener.setImage(null);
        }
    }

    private void setProgress(ProgressListener progressListener, int current, int max, File imageFile) {
        if (progressListener != null) {
            String message = "";

            if (imageFile != null) {
                message = imageFile.getName();
            }

            progressListener.setProgress(current, max, message);
        }
    }
}
