package se.cha.ui;

import se.cha.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageCheckBox extends JCheckBox {

    private final BufferedImage imageOn;
    private final BufferedImage imageOff;
    private final BufferedImage imageOnDisabled;
    private final BufferedImage imageOffDisabled;
    private final int width;
    private final int height;

    public ImageCheckBox(BufferedImage imageOn, BufferedImage imageOff, int width, int height) {
        super(new BufferedImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), width, height));

        this.width = width;
        this.height = height;

        this.imageOn = imageOn;
        this.imageOff = imageOff;
        this.imageOnDisabled = ImageUtils.getFadeImage(imageOn, 0.25);
        this.imageOffDisabled = ImageUtils.getFadeImage(imageOff, 0.25);

        final Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setSize(size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        if (isSelected()) {
            if (isEnabled()) {
                g2d.drawImage(imageOn, 0, 0, width - 2, height - 2, null);
            } else {
                g2d.drawImage(imageOnDisabled, 0, 0, width - 2, height - 2, null);
            }
        } else {
            if (isEnabled()) {
                g2d.drawImage(imageOff, 0, 0, width - 2, height - 2, null);
            } else {
                g2d.drawImage(imageOffDisabled, 0, 0, width - 2, height - 2, null);
            }
        }
    }

}
