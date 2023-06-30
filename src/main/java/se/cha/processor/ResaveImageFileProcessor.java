package se.cha.processor;

import lombok.Value;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cha.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;

/*
Read and save EXIF information:
https://stackoverflow.com/questions/36868013/editing-jpeg-exif-data-with-java
*/
public class ResaveImageFileProcessor extends ImageFileProcessorTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResaveImageFileProcessor.class.getName());

    private static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat("##0.0");
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("###,###,###,##0", new DecimalFormatSymbols(Locale.forLanguageTag("sv")));

    private final ResaveConfig config;

    public ResaveImageFileProcessor(ResaveConfig config) {
        super(false);
        this.config = config;
    }

    public boolean processFile(File imageFile, Set<File> files, ProgressListener progressListener) {
        boolean processed = false;

        if (FileUtils.isJpgImageFile(imageFile)) {
            try {
                final long fileLastModifiedTimeStamp = imageFile.lastModified();
                final byte[] jpgImageData = FileUtils.readFileToByteArray(imageFile);

                final TiffOutputSet jpgFileExifOutputSet = ExifUtils.getJpgFileExifOutputSet(jpgImageData);

                final BufferedImage image = ImageIO.read(imageFile);

                if (progressListener != null) {
                    // Rotate image if rotate information exist in exif meta data
                    final int exifTransformation = ExifUtils.getJpgFileExifRotation(jpgImageData);
                    final BufferedImage displayImage = ImageUtils.getExifRotatedImage(image, exifTransformation);

                    progressListener.setImage(displayImage);
                }

                final byte[] jpgQ9ImageData = getImageAsJpgQ9Data(image);

                byte[] jpgQ9ImageWithExifData = jpgQ9ImageData;

                // Add Exif information (if available, i.e. there is at least one exif directory present)
                if (!jpgFileExifOutputSet.getDirectories().isEmpty()) {
                    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(jpgQ9ImageData.length + 1024 * 4);
                    ExifUtils.writeJpgWithExif(jpgQ9ImageData, outputStream, jpgFileExifOutputSet);

                    jpgQ9ImageWithExifData = outputStream.toByteArray();
                }

                final int originalFileSize = jpgImageData.length;
                final int resavedFileSize = jpgQ9ImageWithExifData.length;
                final double fileSizeChangeFactor = 1.0 * resavedFileSize / originalFileSize;

                if (fileSizeChangeFactor <= config.getThreshold()) {
                    processed = true;
                    FileUtils.writeByteArrayToFile(imageFile, jpgQ9ImageWithExifData);
                    imageFile.setLastModified(fileLastModifiedTimeStamp);

                    LOGGER.info(LogUtil.format("Resaved", imageFile, "Saved file size is " + INTEGER_FORMAT.format(resavedFileSize) + " bytes" +
                            " (" + getPercentText(fileSizeChangeFactor) + " compared to the original size)." +
                            "    (" + FileUtils.byteCountToDisplaySize(originalFileSize) + " --> " + FileUtils.byteCountToDisplaySize(resavedFileSize) + ")"));
                } else {
                    LOGGER.info(LogUtil.format("Not resaved", imageFile, "Optimization gain was too low. Optimized file size attempt was " + getPercentText(fileSizeChangeFactor) + " of original size, while resave threshold is set to " + getPercentText(config.getThreshold()) + "."));
                }


            } catch (IOException | ImageWriteException e) {
                e.printStackTrace();
            } catch (ImageReadException e) {
                if (e.getMessage().contains("Jpeg contains more than one Photoshop App13 segment.")) {
                    LOGGER.warn(LogUtil.format("EXIF error", imageFile, "Could not read EXIF information properly. " + e.getMessage()));
                } else {
                    e.printStackTrace();
                }
            }
        }

        return processed;
    }


    private String getPercentText(double fileSizeChangeFactor) {
        return ONE_DECIMAL_FORMAT.format(100.0 * fileSizeChangeFactor) + "%";
    }

    private byte[] getImageAsJpgQ9Data(BufferedImage image) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(new MemoryCacheImageOutputStream(baos));

        // writes the file with given compression level from your JPEGImageWriteParam instance
        final JPEGImageWriteParam jpegParams = getJpgWriteParameters();
        final IIOImage iioImage = new IIOImage(image, null, null);
        writer.write(null, iioImage, jpegParams);
        writer.dispose();

        return baos.toByteArray();
    }

    private JPEGImageWriteParam getJpgWriteParameters() {
        final JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality((float) config.getQuality());
        return jpegParams;
    }

    @Value
    public static class ResaveConfig {
        double quality;
        double threshold;
    }

    @Value
    public static class ResizeConfig {
        int maxWidth;
        int maxHeight;
    }
}
