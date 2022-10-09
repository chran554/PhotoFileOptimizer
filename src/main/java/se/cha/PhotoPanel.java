package se.cha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PhotoPanel extends JPanel implements ActionListener {

    private static final Color LIGHT_GRAY = new Color(225, 225, 225);

    private final int minimumWidth;
    private final double borderSizePercent;
    private Image image = null;
    private Timer timer = new Timer(0, this);

    /**
     * Border width in percent of max of image width and height.
     *
     * @param borderSizePercent The size of the border (with respect to image size) in percent. Like '3.0' for 3%.
     */
    public PhotoPanel(int minimumWidth, double borderSizePercent) {
        setOpaque(false);
        this.minimumWidth = minimumWidth;
        this.borderSizePercent = borderSizePercent;
    }

    public void setImage(Image image) {
        timer.stop();
        timer.removeActionListener(this);

        this.image = image;
        repaint();
    }

    public void setImage(Image image, int amountSeconds) {
        setImage(image);

        if (image != null) {
            timer = new Timer(amountSeconds * 1000, this);
            timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setImage(null);
    }

    @Override
    public void paint(Graphics g) {
        boolean imagePainted = false;

        if (image != null) {
            final Graphics2D g2d = (Graphics2D) g;

            final Dimension imageDimension = ImageUtils.getImageDimension(image);

            final Dimension panelSize = getSize();
            final double scaleFactorToFit = ImageUtils.getScaleFactorToFit(imageDimension, panelSize);

            final int photoWidth = (int) Math.round(imageDimension.getWidth() * scaleFactorToFit);
            final int photoHeight = (int) Math.round(imageDimension.getHeight() * scaleFactorToFit);

            final int borderSize = (int) Math.round((borderSizePercent / 100.0) * Math.max(photoWidth, photoHeight));

            final int imageWidth = photoWidth - 2 * borderSize;
            final int imageHeight = photoHeight - 2 * borderSize;

            final int xIndent = (int) Math.round((panelSize.width - photoWidth) / 2.0);
            final int yIndent = (int) Math.round((panelSize.height - photoHeight) / 2.0);

            if (imageWidth >= minimumWidth && imageHeight >= minimumWidth) {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(xIndent, yIndent, photoWidth, photoHeight);
                g2d.setColor(LIGHT_GRAY);
                g2d.drawRect(xIndent, yIndent, photoWidth, photoHeight);

                // Render photo
                g2d.fillRect(xIndent + borderSize, yIndent + borderSize, imageWidth, imageHeight);
                g2d.drawImage(image, xIndent + borderSize, yIndent + borderSize, imageWidth, imageHeight, null);

                g2d.drawRect(xIndent + borderSize, yIndent + borderSize, imageWidth, imageHeight);

                imagePainted = true;
            }

        }

        if (!imagePainted) {
            super.paint(g);
        }

        g.dispose();
    }
}
