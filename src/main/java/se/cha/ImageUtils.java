package se.cha;

import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class.getName());

    private ImageUtils() {
    }

    public static double getScaleFactor(int masterSize, int targetSize) {
        return masterSize > targetSize
                ? (double) targetSize / (double) masterSize
                : (double) targetSize / (double) masterSize;
    }


    public static double getScaleFactorToFit(Dimension original, Dimension toFit) {
        double scale = 1.0;

        if (original != null && toFit != null) {
            final double scaleWidth = getScaleFactor(original.width, toFit.width);
            final double scaleHeight = getScaleFactor(original.height, toFit.height);

            scale = Math.min(scaleHeight, scaleWidth);
        }

        return scale;
    }

    public static double getScaleFactorToFill(Dimension original, Dimension toFill) {
        double scale = 1.0;

        if (original != null && toFill != null) {
            final double scaleWidth = getScaleFactor(original.width, toFill.width);
            final double scaleHeight = getScaleFactor(original.height, toFill.height);

            scale = Math.max(scaleHeight, scaleWidth);
        }

        return scale;
    }

    public static Dimension getImageDimension(final Image image) {
        return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    public static BufferedImage getRotatedImage(BufferedImage image, int amountQuadrantRotates) {
        BufferedImage resultImage = image;

        if (amountQuadrantRotates > 0) {
            final boolean oddAmountQuadrantRotates = (amountQuadrantRotates % 2) == 1;

            final int halfWidth = image.getWidth() / 2;
            final int halfHeight = image.getHeight() / 2;

            final AffineTransform rotateTransform = new AffineTransform();
            rotateTransform.setToQuadrantRotation(amountQuadrantRotates);

            final AffineTransform tx = new AffineTransform();
            tx.translate(oddAmountQuadrantRotates ? halfHeight : halfWidth, oddAmountQuadrantRotates ? halfWidth : halfHeight);
            tx.concatenate(rotateTransform);
            tx.translate(-halfWidth, -halfHeight);

            final AffineTransformOp transformOperation = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            resultImage = transformOperation.filter(image, null);
        }

        return resultImage;
    }

    /**
     * Rotates an image in multiples of 90 degrees.
     *
     * @param image           The image to rotate
     * @param exifOrientation The Tiff tag orientation
     * @return The rotated image.
     * @see TiffTagConstants#ORIENTATION_VALUE_HORIZONTAL_NORMAL
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_90_CW
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_180
     * @see TiffTagConstants#ORIENTATION_VALUE_ROTATE_270_CW
     */
    public static BufferedImage getExifRotatedImage(BufferedImage image, int exifOrientation) {
        int amountQuadrantRotates = -1;

        if (exifOrientation == TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL) {
            amountQuadrantRotates = 0;
        } else if (exifOrientation == TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW) {
            amountQuadrantRotates = 1;
        } else if (exifOrientation == TiffTagConstants.ORIENTATION_VALUE_ROTATE_180) {
            amountQuadrantRotates = 2;
        } else if (exifOrientation == TiffTagConstants.ORIENTATION_VALUE_ROTATE_270_CW) {
            amountQuadrantRotates = 3;
        } else {
            LOGGER.warn("Unsupported exif image orientation: " + exifOrientation);
        }

        return (amountQuadrantRotates > 0)
                ? getRotatedImage(image, amountQuadrantRotates)
                : image;
    }

}
