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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/*
Read and save EXIF information:
https://stackoverflow.com/questions/36868013/editing-jpeg-exif-data-with-java
*/
public class ResizeImageFileProcessor extends ImageFileProcessorTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResizeImageFileProcessor.class.getName());

    private final ResizeConfig config;

    public ResizeImageFileProcessor(ResizeConfig config) {
        super(false);
        this.config = config;
    }

    public boolean processFile(File imageFile, Set<File> files, ProgressListener progressListener) {
        boolean processed = false;

        if (FileUtils.isJpgImageFile(imageFile)) {
            processed = processJpg(imageFile, progressListener, processed);
        }

        return processed;
    }

    private boolean processJpg(File imageFile, ProgressListener progressListener, boolean processed) {
        try {
            final long fileLastModifiedTimeStamp = imageFile.lastModified();
            final byte[] originalImageJpgData = FileUtils.readFileToByteArray(imageFile);

            final TiffOutputSet jpgFileExifOutputSet = ExifUtils.getJpgFileExifOutputSet(originalImageJpgData);

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(originalImageJpgData);
            final BufferedImage image = ImageIO.read(byteArrayInputStream);

            if (progressListener != null) {
                // Rotate image if rotate information exist in exif meta data
                final int exifTransformation = ExifUtils.getJpgFileExifRotation(originalImageJpgData);
                final BufferedImage displayImage = ImageUtils.getExifRotatedImage(image, exifTransformation);
                progressListener.setImage(displayImage);
            }

            final double leastScaleFactor = Math.min(
                    1.0 * config.getMaxWidth() / image.getWidth(),
                    1.0 * config.getMaxHeight() / image.getHeight()
            );

            if (leastScaleFactor < 1.0) {
                final int newWidth = Math.min((int) Math.round(image.getWidth() * leastScaleFactor), config.getMaxWidth());
                final int newHeight = Math.min((int) Math.round(image.getHeight() * leastScaleFactor), config.getMaxHeight());

                final BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
                final Graphics2D g2d = (Graphics2D) resizedImage.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                final byte[] resizedImageJpgData = getImageAsJpgData(resizedImage, config.getQuality());

                byte[] jpgImageWithExifData = resizedImageJpgData;
                // Add Exif information (if available, i.e. there is at least one exif directory present)
                if (!jpgFileExifOutputSet.getDirectories().isEmpty()) {
                    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(resizedImageJpgData.length + 1024 * 4);
                    ExifUtils.writeJpgWithExif(resizedImageJpgData, outputStream, jpgFileExifOutputSet);
                    jpgImageWithExifData = outputStream.toByteArray();
                }

                processed = true;
                FileUtils.writeByteArrayToFile(imageFile, jpgImageWithExifData);
                imageFile.setLastModified(fileLastModifiedTimeStamp);

                LOGGER.info(LogUtil.format("Resized", imageFile, "Image file is rescaled to " + newWidth + "x" + newHeight + " (" + (leastScaleFactor * 100) + "%)."));
            } else {
                LOGGER.info(LogUtil.format("Resized", imageFile, "Image is already within specified dimensions (" + image.getWidth() + "x" + image.getHeight() + ")."));
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
        return processed;
    }

    private byte[] getImageAsJpgData(BufferedImage image, double quality) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(new MemoryCacheImageOutputStream(baos));

        // writes the file with given compression level from your JPEGImageWriteParam instance
        final JPEGImageWriteParam jpegParams = getJpgWriteParameters(quality);
        final IIOImage iioImage = new IIOImage(image, null, null);
        writer.write(null, iioImage, jpegParams);
        writer.dispose();

        return baos.toByteArray();
    }

    private JPEGImageWriteParam getJpgWriteParameters(double quality) {
        final JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality((float) quality);
        return jpegParams;
    }

    @Value
    public static class ResizeConfig {
        int maxWidth;
        int maxHeight;
        double quality;
    }
}
