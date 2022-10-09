package se.cha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveMediaFileProcessor extends RecursiveImageFileProcessorTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveMediaFileProcessor.class.getName());

    private static final String RENAMED_FILE_TEXT_FORMAT = "(\\d{4}-\\d{2}-\\d{2})\\s\\d{4}\\s--\\s+(.*)(\\s\\(\\d+\\))?"; // "yyyy-MM-dd HHmm -- <filename> (n)"
    private static final Pattern RENAMED_FILE_FORMAT = Pattern.compile(RENAMED_FILE_TEXT_FORMAT);

    boolean processFile(ProgressListener progressListener, File imageFile) {
        boolean processed = false;

        final Matcher matcher = RENAMED_FILE_FORMAT.matcher(imageFile.getName());
        final boolean renamedFile = matcher.matches();

        if (renamedFile) {
            final String dateText = matcher.group(1);
            final File mainDirectory = new File(imageFile.getParent(), dateText);
            //final File originalDirectory = new File(mainDirectory, mainDirectory.getName() + " - original");
            final File originalDirectory = mainDirectory;

            try {
                org.apache.commons.io.FileUtils.moveFileToDirectory(imageFile, originalDirectory, true);

                processed = true;
            } catch (IOException e) {
                LOGGER.error(LogUtil.format("Not moved", imageFile, "Could not move image file or create directory structure. " + e.getMessage()));
            }

            if (!processed) {
                LOGGER.warn(LogUtil.format("Not moved", imageFile, "Could not move image file or create directory structure because of unknown reason."));
            } else {
                LOGGER.info(LogUtil.format("Moved", imageFile, "Moved media file to \"" + originalDirectory.getName() + "\"."));
            }
        } else {
            LOGGER.info(LogUtil.format("Not moved", imageFile, "File is not on renamed format."));
        }

        return processed;
    }

}
