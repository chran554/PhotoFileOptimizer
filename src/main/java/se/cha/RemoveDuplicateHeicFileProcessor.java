package se.cha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveDuplicateHeicFileProcessor extends ImageFileProcessorTemplate {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    private static final Matcher HEIC_FILENAME_MATCHER = Pattern.compile("(.*)?IMG_(\\d{4})(.*)\\.HEIC").matcher("");

    @Override
    protected void processFile(ProgressListener progressListener, File imageFile, Set<File> imageFiles) {
        HEIC_FILENAME_MATCHER.reset(imageFile.getName());
        if (HEIC_FILENAME_MATCHER.matches()) {
            final String jpgFileName = HEIC_FILENAME_MATCHER.group(1) + "IMG_E" + HEIC_FILENAME_MATCHER.group(2) + ".JPG";
            final File jpgFile = new File(imageFile.getParent(), jpgFileName);

            final String aaeFileName1 = "IMG_" + HEIC_FILENAME_MATCHER.group(2) + HEIC_FILENAME_MATCHER.group(3) + ".AAE";
            final String aaeFileName2 = HEIC_FILENAME_MATCHER.group(1) + aaeFileName1;
            final File aaeFile1 = new File(imageFile.getParent(), aaeFileName1);
            final File aaeFile2 = new File(imageFile.getParent(), aaeFileName2);

            if (imageFiles.contains(jpgFile) && jpgFile.exists() && jpgFile.isFile()) {
                imageFile.delete();
                LOGGER.info("Deleted " + imageFile.getName() + ".");

                if (aaeFile1.exists() && aaeFile1.isFile()) {
                    aaeFile1.delete();
                    LOGGER.info("Deleted " + aaeFile1.getName() + ".");
                }

                if (aaeFile2.exists() && aaeFile2.isFile()) {
                    aaeFile2.delete();
                    LOGGER.info("Deleted " + aaeFile2.getName() + ".");
                }
            }
        }
    }


}
