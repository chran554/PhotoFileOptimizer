package se.cha.ui;

import se.cha.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImageButton extends JButton {

    private final BufferedImage image;
    private final BufferedImage imageNoFocus;
    private final BufferedImage imageDisabled;
    private final int width;
    private final int height;
    private boolean hasFocus = false;

    public ImageButton(BufferedImage image, int width, int height) {
        super(new BufferedImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), width, height));

        this.width = width;
        this.height = height;

        this.image = image;
        this.imageNoFocus = ImageUtils.getFadeImage(image, 0.4);
        this.imageDisabled = ImageUtils.getFadeImage(image, 0.25);

        final Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setSize(size);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hasFocus = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hasFocus = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        if (isEnabled()) {
            if (hasFocus) {
                g2d.drawImage(image, 0, 0, width - 2, height - 2, null);
            } else {
                g2d.drawImage(imageNoFocus, 0, 0, width - 2, height - 2, null);
            }
        } else {
            g2d.drawImage(imageDisabled, 0, 0, width - 2, height - 2, null);
        }
    }

}
