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
        return (double) targetSize / (double) masterSize;
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

    public static BufferedImage getFadeImage(BufferedImage image, double opacity) {
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        final BufferedImage fadedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        final int[] pixels = new int[imageWidth * imageWidth];
        image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        for (int i = 0; i < imageWidth * imageWidth; i++) {
            final int originalAlpha = pixels[i] >>> 24;
            final double newAlpha = originalAlpha * opacity;
            pixels[i] = (pixels[i] & 0x00ffffff) | (((int) newAlpha) << 24);
        }

        fadedImage.setRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        return fadedImage;
    }

    public static BufferedImage getSaturatedImage(BufferedImage image, double factor) {
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        final BufferedImage desaturatedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        final int[] pixels = new int[imageWidth * imageWidth];
        image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        for (int i = 0; i < imageWidth * imageWidth; i++) {
            final int r = (pixels[i] & 0x00ff0000) >> 16;
            final int g = (pixels[i] & 0x0000ff00) >> 8;
            final int b = (pixels[i] & 0x000000ff);

            final float[] hsb = Color.RGBtoHSB(r, g, b, null);
            hsb[1] = (float) (hsb[1] * factor);
            final int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

            pixels[i] = (pixels[i] & 0xff000000) | (rgb & 0x00ffffff);
        }

        desaturatedImage.setRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        return desaturatedImage;
    }

}