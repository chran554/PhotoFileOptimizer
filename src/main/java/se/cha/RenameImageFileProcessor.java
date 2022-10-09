package se.cha;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameImageFileProcessor extends RecursiveImageFileProcessorTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameImageFileProcessor.class.getName());


    private static final String DATE_FORMAT_TEXT = "yyyy-MM-dd HHmm";
    private static final String RENAMED_FILE_TEXT_FORMAT = "\\d{4}-\\d{2}-\\d{2}\\s\\d{4}\\s--\\s+(.*)(\\s\\(\\d+\\))?"; // "yyyy-MM-dd HHmm -- <filename> (n)"

    private static final Pattern RENAMED_FILE_FORMAT = Pattern.compile(RENAMED_FILE_TEXT_FORMAT);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_TEXT);
    private static final Date START_OF_ALL_DIGITAL_IMAGES = new GregorianCalendar(1991, Calendar.JANUARY, 1).getTime();

    boolean processFile(ProgressListener progressListener, File imageFile) {
        boolean processed = false;

        Date imageDate = null;

        if (!isValidImageDate(imageDate) && FileUtils.isExifImageFile(imageFile)) {
            imageDate = FileUtils.getImageExifDate(imageFile);
        }

        if (!isValidImageDate(imageDate) && FileUtils.isMovFile(imageFile)) {
            imageDate = FileUtils.getImageDateFromMovFile(imageFile);
        }

        if (!isValidImageDate(imageDate) && FileUtils.isMp4File(imageFile)) {
            imageDate = FileUtils.getImageDateFromMp4File(imageFile);
        }

        if (!isValidImageDate(imageDate) && FileUtils.isNefFile(imageFile)) {
            imageDate = FileUtils.getImageDateFromNefFile(imageFile);
        }

        if (!isValidImageDate(imageDate) && FileUtils.isXmpFile(imageFile)) {
            imageDate = FileUtils.getImageDateFromXmpFile(imageFile);
        }

        if (!isValidImageDate(imageDate) && FileUtils.isAaeFile(imageFile)) {
            imageDate = FileUtils.getImageDateFromAaeFile(imageFile);
        }

        if (!isValidImageDate(imageDate)) {
            imageDate = getImageDateFromFile(imageFile);
        }

        if (imageDate != null) {
            final boolean isAlreadyRenamed = isRenamedFile(imageFile);

            if (!isAlreadyRenamed) {
                final String newFileName = getNewFileName(imageFile, imageDate);
                final File newFile = new File(imageFile.getParent(), newFileName);

                final boolean renameSuccess = imageFile.renameTo(newFile);

                processed = renameSuccess;

                if (!renameSuccess) {
                    LOGGER.warn(LogUtil.format("Not renamed", imageFile, "Could not rename image file because of unknown file error."));
                } else {
                    LOGGER.info(LogUtil.format("Renamed", imageFile, "Renamed to \"" + newFile.getName() + "\"."));
                }
            } else {
                LOGGER.info(LogUtil.format("Not renamed", imageFile, "File is already renamed."));
            }
        } else {
            LOGGER.warn(LogUtil.format("Not renamed", imageFile, "Could not find a original date for image file."));
        }

        return processed;
    }

    private boolean isValidImageDate(Date imageDate) {
        return imageDate != null && imageDate.after(START_OF_ALL_DIGITAL_IMAGES);
    }

    private Date getImageDateFromFile(File imageFile) {
        Date fileModifiedDate = FileUtils.getModifiedDate(imageFile);
        Date fileCreationDate = FileUtils.getCreatedDate(imageFile);

        if (fileCreationDate.before(START_OF_ALL_DIGITAL_IMAGES)) {
            fileCreationDate = null;
        }

        if (fileModifiedDate.before(START_OF_ALL_DIGITAL_IMAGES)) {
            fileModifiedDate = null;
        }

        if ((fileCreationDate == null) && (fileModifiedDate == null)) {
            return null;
        } else if ((fileCreationDate == null) && (fileModifiedDate != null)) {
            return fileModifiedDate;
        } else if ((fileCreationDate != null) && (fileModifiedDate == null)) {
            return fileCreationDate;
        } else {
            return fileCreationDate.before(fileModifiedDate)
                    ? fileCreationDate
                    : fileModifiedDate;
        }
    }

    private String getNewFileName(File imageFile, Date imageDate) {
        final String originalFileName = getOriginalFilename(imageFile);
        final String fileExtension = FilenameUtils.getExtension(imageFile.getName());

        String newFileName = null;

        boolean unusedName = false;
        int index = 0;
        while (!unusedName) {
            index++;
            final String indexText = index > 1 ? " (" + index + ")" : "";
            newFileName = DATE_FORMAT.format(imageDate) + " -- " + originalFileName + indexText + "." + fileExtension;

            unusedName = !(new File(imageFile.getParent(), newFileName).exists());
        }

        if (index > 1) {
            LOGGER.warn(LogUtil.format("Already renamed", imageFile, "File seem to be already renamed. Renaming using index suffix (" + index + ")."));
        }

        return newFileName;
    }

    private boolean isRenamedFile(File imageFile) {
        return RENAMED_FILE_FORMAT.matcher(imageFile.getName()).matches();
    }

    private static String getOriginalFilename(File imageFile) {
        String completeFilename = imageFile.getName();

        final Matcher matcher = RENAMED_FILE_FORMAT.matcher(completeFilename);
        if (matcher.matches()) {
            completeFilename = matcher.group(1);
        }
        return FilenameUtils.getBaseName(completeFilename);
    }

}
