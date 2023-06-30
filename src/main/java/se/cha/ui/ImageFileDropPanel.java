package se.cha.ui;

import se.cha.ImageUtils;
import se.cha.ProgressListener;
import se.cha.State;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class ImageFileDropPanel extends JPanel implements ProgressListener {

    private static final int PROGRESSBAR_MAX = 10000;
    private static final Color COLOR_IMAGE_FADE = new Color(255, 255, 255, 200);
    private static final Color COLOR_TEXT = new Color(230, 210, 164, 200).darker();

    private BufferedImage image = null;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private final PhotoPanel photoPanel = new PhotoPanel(40, 3.0);

    private ImageCheckBox renameCheckbox;
    private ImageCheckBox resaveCheckbox;
    private ImageCheckBox resizeCheckbox;
    private ImageButton settingsButton;
    private java.util.List<ActionListener> actionListeners = new ArrayList<>();

    public ImageFileDropPanel() {
        super(new BorderLayout());

        try {
            image = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/photo stack 2.jpg"));
            final Graphics2D g2d = (Graphics2D) image.getGraphics();
            final Color originalColor = g2d.getColor();
            g2d.setColor(COLOR_IMAGE_FADE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.setColor(originalColor);
            g2d.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final JPanel buttonPanel = getSelectionPanel();

        final JLabel label = new JLabel("<html><b><h2>Drop image files in this window</h2></b></html>");
        label.setForeground(COLOR_TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar(0, PROGRESSBAR_MAX);

        progressLabel = new JLabel("");
        progressLabel.setForeground(COLOR_TEXT);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

        final JPanel componentPanel = new JPanel(new GridBagLayout());
        componentPanel.setOpaque(false);
        componentPanel.add(progressLabel, new GridBagConstraints(0,0,1,1,1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,10), 0,0));
        componentPanel.add(progressBar, new GridBagConstraints(0,1,1,1,1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,10), 0,0));

        final JPanel photoPanelContainer = new JPanel(new BorderLayout());
        photoPanelContainer.setOpaque(false);
        photoPanelContainer.add(photoPanel, BorderLayout.CENTER);
        photoPanelContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(label, BorderLayout.NORTH);
        add(photoPanelContainer, BorderLayout.CENTER);
        add(componentPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.WEST);
    }

    private JPanel getSelectionPanel() {
        BufferedImage buttonImageRename = null;
        BufferedImage buttonImageResize = null;
        BufferedImage buttonImageResave = null;
        BufferedImage buttonImageSettings = null;
        try {
            buttonImageRename = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/icon-rename.png"));
            buttonImageResave = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/icon-resave.png"));
            buttonImageResize = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/icon-resize.png"));
            buttonImageSettings = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/icon-settings.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        renameCheckbox = new ImageCheckBox(buttonImageRename, ImageUtils.getFadeImage(ImageUtils.getSaturatedImage(buttonImageRename, 0.2), 0.4), 50, 50);
        resaveCheckbox = new ImageCheckBox(buttonImageResave, ImageUtils.getFadeImage(ImageUtils.getSaturatedImage(buttonImageResave, 0.2), 0.4), 50, 50);
        resizeCheckbox = new ImageCheckBox(buttonImageResize, ImageUtils.getFadeImage(ImageUtils.getSaturatedImage(buttonImageResize, 0.2), 0.4), 50, 50);

        renameCheckbox.setToolTipText("Enable rename of dropped files according to pattern \"yyyy-mm-dd hh:mm <old file name>\".");
        resaveCheckbox.setToolTipText("Enable re-save of jpg files if the result file size will be at most a configured factor of the original size.");
        resizeCheckbox.setToolTipText("Enable re-size (shrink) of dropped image files to configured maximum dimensions.");

        renameCheckbox.addActionListener(e -> updateState());
        resaveCheckbox.addActionListener(e -> updateState());
        resizeCheckbox.addActionListener(e -> updateState());

        renameCheckbox.setSelected(true);
        resaveCheckbox.setSelected(true);
        resizeCheckbox.setSelected(false);

        settingsButton = new ImageButton(buttonImageSettings, 30, 30);
        settingsButton.setToolTipText("Show settings");
        settingsButton.addActionListener(e -> {
            actionListeners.forEach(l-> l.actionPerformed(new ActionEvent(e.getSource(), 0, "settings")));
        });

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

        buttonPanel.add(Box.createVerticalStrut(renameCheckbox.getHeight() / 4));
        buttonPanel.add(renameCheckbox);
        buttonPanel.add(Box.createVerticalStrut(resaveCheckbox.getHeight() / 4));
        buttonPanel.add(resaveCheckbox);
        buttonPanel.add(Box.createVerticalStrut(resizeCheckbox.getHeight() / 4));
        buttonPanel.add(resizeCheckbox);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(settingsButton);

        return buttonPanel;
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    @Override
    public void setImage(Image image) {
        photoPanel.setImage(image, 10);
    }

    public void resetProgress(String message) {
        setProgress(0, PROGRESSBAR_MAX, message);
    }

    public void setProgress(int current, int max, String message) {
        final boolean inProgress = current > 0 && current < max;
        renameCheckbox.setEnabled(!inProgress);
        resaveCheckbox.setEnabled(!inProgress);
        resizeCheckbox.setEnabled(!inProgress);

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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        final int screenWidth = getWidth();
        final int screenHeight = getHeight();
        final int imageHeight = image.getHeight();
        final int imageWidth = image.getWidth();

        final Color originalColor = g2d.getColor();
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        g2d.setColor(originalColor);

        final double scaleFactor = ImageUtils.getScaleFactorToFill(new Dimension(imageWidth, imageHeight), new Dimension(screenWidth, screenHeight));

        final int resizedImageWidth = (int) (imageWidth * scaleFactor);
        final int resizedImageHeight = (int) (imageHeight * scaleFactor);

        g2d.drawImage(image, screenWidth / 2 - resizedImageWidth / 2, screenHeight / 2 - resizedImageHeight / 2, resizedImageWidth, resizedImageHeight, null);
    }

    public void updateState() {
        State.setEnabledProcessors(renameCheckbox.isSelected(), resaveCheckbox.isSelected(), resizeCheckbox.isSelected());
    }
}

