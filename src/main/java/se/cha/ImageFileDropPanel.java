package se.cha;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class ImageFileDropPanel extends JPanel implements ProgressListener {

    private static final int PROGRESSBAR_MAX = 10000;
    private static final Color COLOR_IMAGE_FADE = new Color(255, 255, 255, 200);
    //private static final Color COLOR_TEXT = new Color(230, 210, 164);
    private static final Color COLOR_TEXT = new Color(230, 210, 164).darker();

    private BufferedImage image = null;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private final PhotoPanel photoPanel = new PhotoPanel(40, 3.0);

    public ImageFileDropPanel() {
        super(new BorderLayout());

        try {
            image = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/photo stack 2.jpg"));
            final Graphics2D g2d = (Graphics2D) image.getGraphics();
            final Color originalColor = g2d.getColor();
            g2d.setColor(COLOR_IMAGE_FADE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.setColor(originalColor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final JLabel label = new JLabel("<html><b><h2>Drop image files in this window</h2></b></html>");
        label.setForeground(COLOR_TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar(0, PROGRESSBAR_MAX);

        progressLabel = new JLabel("");
        progressLabel.setForeground(COLOR_TEXT);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

        final JPanel componentPanel = new JPanel(new GridLayout(2, 0, 8, 8));
        componentPanel.setOpaque(false);
        componentPanel.add(progressLabel);
        componentPanel.add(progressBar);

        final JPanel photoPanelContainer = new JPanel(new BorderLayout());
        photoPanelContainer.setOpaque(false);
        photoPanelContainer.add(photoPanel, BorderLayout.CENTER);
        photoPanelContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(label, BorderLayout.NORTH);
        add(photoPanelContainer, BorderLayout.CENTER);
        add(componentPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setImage(Image image) {
        photoPanel.setImage(image, 10);
    }

    public void resetProgress(String message) {
        setProgress(0, PROGRESSBAR_MAX, message);
    }

    public void setProgress(int current, int max, String message) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setMaximum(max);
            progressBar.setValue(current);
            progressLabel.setText("<html><div style='text-align: center;'>" + message.replaceAll("\n", "<br>") + "</div></html>");
        });
    }

    @Override
    public void update(final Graphics g) {
        paint(g);
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension preferredSize = super.getPreferredSize();
        return new Dimension(Math.max(preferredSize.width, image.getWidth()), Math.max(preferredSize.height, image.getHeight()));
    }

    @Override
    public void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;

        //g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        final int screenWidth = getWidth();
        final int screenHeight = getHeight();
        final int imageHeight = image.getHeight();
        final int imageWidth = image.getWidth();

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        final double scaleFactor = ImageUtils.getScaleFactorToFill(new Dimension(imageWidth, imageHeight), new Dimension(screenWidth, screenHeight));

        final int resizedImageWidth = (int) (imageWidth * scaleFactor);
        final int resizedImageHeight = (int) (imageHeight * scaleFactor);

        g2d.drawImage(image, screenWidth / 2 - resizedImageWidth / 2, screenHeight / 2 - resizedImageHeight / 2, resizedImageWidth, resizedImageHeight, null);
    }
}
