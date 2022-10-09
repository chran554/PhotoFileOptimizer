package se.cha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RecursiveImageFileProcessorTemplate implements FileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecursiveImageFileProcessorTemplate.class.getName());
    private static final Matcher SIMPLE_FILENAME_MATCHER = Pattern.compile("_?\\p{Alpha}{3}_?\\d{4}\\.\\p{Alpha}{3}").matcher("");

    public Thread processFiles(List<File> files, ProgressListener progressListener) {

        LOGGER.info(LogUtil.format("Scanning", "Initiating work. Scanning for files."));

        final Thread processorThread = new Thread() {
            @Override
            public void run() {
                resetProgress(progressListener, "Scanning for image files...");
                final Set<File> imageFiles = recursiveFindMediaFiles(new TreeSet<>(files));

                long lastKeepOsAlive = 0L;

                int amountProcessedFiles = 0;
                for (File imageFile : imageFiles) {
                    LOGGER.info(LogUtil.format("Processing", imageFile, "Processing file " + (amountProcessedFiles + 1) + " of " + imageFiles.size() + "."));

                    setProgress(progressListener, amountProcessedFiles, imageFiles.size(), imageFile);
                    if (progressListener != null) {
                        // progressListener.setImage(null);
                    }

                    lastKeepOsAlive = keepOsAlive(lastKeepOsAlive);

                    processFile(progressListener, imageFile);
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
                final String fileName = imageFile.getName();
                message = (SIMPLE_FILENAME_MATCHER.reset(fileName).matches() ? imageFile.getParentFile().getName() + "\n" : "") + fileName;
            }

            progressListener.setProgress(current, max, message);
        }
    }

    abstract boolean processFile(ProgressListener progressListener, File imageFile);

    private static Set<File> recursiveFindMediaFiles(Set<File> files) {
        final Set<File> foundFiles = new TreeSet<>();

        for (final File file : files) {

            if (file.exists()) {
                if (file.isFile()) {
                    if (FileUtils.isMediaFile(file)) {
                        foundFiles.add(file);
                    }
                } else if (file.isDirectory()) {
                    final Set<File> subFiles = new TreeSet<>(Arrays.asList(file.listFiles()));
                    foundFiles.addAll(recursiveFindMediaFiles(subFiles));
                } else {
                    LOGGER.error("Did not recognize type of supposed file \"" + file.getAbsolutePath() + "\", not a file nor directory.");
                }
            }
        }

        return foundFiles;
    }

}
