package se.cha;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ExifUtils {

    private ExifUtils() {

    }

    public static void writeJpgWithExif(byte[] jpgFileData, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        new ExifRewriter().updateExifMetadataLossless(jpgFileData, os, outputSet);
    }

    public static void updateGps(TiffOutputSet outputSet, double longitude, double latitude) throws ImageWriteException {
        outputSet.setGPSInDegrees(longitude, latitude);
    }

    public static void updateApertureValue(TiffOutputSet outputSet, RationalNumber aperture) throws ImageWriteException {
        // Example of how to add a field/tag to the output set.
        //
        // Note that you should first remove the field/tag if it already exists in this directory,
        // or you may end up with duplicate tags. See above.
        //
        // Certain fields/tags are expected in certain Exif directories;
        // Others can occur in more than one directory (and often have a different meaning in different directories).
        //
        // TagInfo constants often contain a description of what directories are associated with a given tag.
        final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
        // Make sure to remove old value if present (this method will not fail if the tag does not exist).
        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
        exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, aperture);
    }

    public static TiffOutputSet getJpgFileExifOutputSet(byte[] jpgFileData) throws ImageReadException, IOException, ImageWriteException {
        TiffOutputSet outputSet = null;

        // Note that metadata might be null if no metadata is found.
        final ImageMetadata metadata = Imaging.getMetadata(jpgFileData);
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (null != jpegMetadata) {
            // Note that exif might be null if no Exif metadata is found.
            final TiffImageMetadata exif = jpegMetadata.getExif();

            if (null != exif) {
                // TiffImageMetadata class is immutable (read-only).
                // TiffOutputSet class represents the Exif data to write.
                // Usually, we want to update existing Exif metadata by changing the values of a few fields, or adding a field.
                // In these cases, it is easiest to use getOutputSet() to start with a "copy" of the fields read from the image.
                outputSet = exif.getOutputSet();
            }
        }

        // If file does not contain any exif metadata, we create an empty set of exif metadata.
        // Otherwise, we keep all of the other existing tags.
        if (null == outputSet) {
            outputSet = new TiffOutputSet();
        }
        return outputSet;
    }


    /**
     * Gets the image rotation as a constant of {@link TiffTagConstants}.
     *
     * @see TiffTagConstants#ORIENTATION_VALUE_HORIZONTAL_NORMAL
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_90_CW
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_180
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_270_CW
     */
    public static int getJpgFileExifRotation(byte[] jpgFileData) throws ImageReadException, IOException, ImageWriteException {
        int rotation = TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL;

        // Note that metadata might be null if no metadata is found.
        final ImageMetadata metadata = Imaging.getMetadata(jpgFileData);
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (null != jpegMetadata) {
            // Note that exif might be null if no Exif metadata is found.
            final TiffImageMetadata exif = jpegMetadata.getExif();

            if (null != exif) {
                final Short imageTransformation = (Short) exif.getFieldValue(TiffTagConstants.TIFF_TAG_ORIENTATION);

                if (imageTransformation != null) {
                    rotation = imageTransformation;
                }
            }
        }

        return rotation;
    }

}
