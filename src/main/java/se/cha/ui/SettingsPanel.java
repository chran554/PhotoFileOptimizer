package se.cha.ui;

import se.cha.State;
import se.cha.processor.ResaveImageFileProcessor;
import se.cha.processor.ResizeImageFileProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanel extends JPanel {

    private List<ActionListener> actionListeners = new ArrayList<>();

    private static final JSlider resaveThresholdSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
    private static final JSlider resaveJpgQualitySlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);

    private static final JTextField resizeMaxWidthTextField = new JTextField(6);
    private static final JTextField resizeMaxHeightTextField = new JTextField(6);

    public SettingsPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        final JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        final JButton exitButton = new JButton("Exit");
        bottomButtonPanel.add(exitButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));

        exitButton.addActionListener(e -> {
            actionListeners.forEach(l -> l.actionPerformed(new ActionEvent(e.getSource(), 0, "exit")));
        });

        final JLabel settingsLabel = new JLabel("<html><h2>Settings</h2></html>", new ImageIcon(getSettingsImage()), JLabel.LEFT);
        settingsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JPanel resaveSettingsPanel = getResaveSettingsPanel();
        final JPanel resizeSettingsPanel = getResizeSettingsPanel();

        final JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        settingsPanel.add(resaveSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        settingsPanel.add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
        settingsPanel.add(resizeSettingsPanel, new GridBagConstraints(0, 2, 1, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JScrollPane scrollPane = new JScrollPane(settingsPanel);
        scrollPane.setBorder(null);

        add(settingsLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    private static BufferedImage getSettingsImage() {
        BufferedImage imageSettings = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        try {
            final BufferedImage imageCogWheel = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/icon-settings.png"));
            final Graphics2D g2d = (Graphics2D) imageSettings.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2d.drawImage(imageCogWheel, 0,0, imageSettings.getWidth(), imageSettings.getHeight(), null);
            g2d.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageSettings;
    }

    private static JPanel getResizeSettingsPanel() {
        final JLabel resizeLabel = new JLabel("<html><h3>Re-size</h3></html>");
        final JLabel resizeExplanationLabel = new JLabel();

        final JPanel resizeSettingsPanel = new JPanel(new GridBagLayout());

        resizeSettingsPanel.add(resizeLabel, new GridBagConstraints(0, 0, 4, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));

        resizeSettingsPanel.add(new JLabel("Max dimensions:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        resizeSettingsPanel.add(resizeMaxWidthTextField, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        resizeSettingsPanel.add(new JLabel("x"), new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        resizeSettingsPanel.add(resizeMaxHeightTextField, new GridBagConstraints(3, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));

        resizeSettingsPanel.add(resizeExplanationLabel, new GridBagConstraints(0, 2, 4, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

        final DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStateConfigurations();
                final Dimension maxDimensions = getResizeMaxDimensions();
                resizeExplanationLabel.setText(getResizeText(maxDimensions.width, maxDimensions.height));
            }
        };

        resizeMaxWidthTextField.getDocument().addDocumentListener(documentListener);
        resizeMaxHeightTextField.getDocument().addDocumentListener(documentListener);

        resizeMaxWidthTextField.setText("1600");
        resizeMaxHeightTextField.setText("1600");

        return resizeSettingsPanel;
    }

    private static JPanel getResaveSettingsPanel() {
        final JLabel resaveLabel = new JLabel("<html><h3>Re-save</h3></html>");
        final JLabel resaveExplanationLabel = new JLabel();

        final JLabel thresholdValueLabel = new JLabel();
        final JLabel qualityValueLabel = new JLabel();

        final JPanel resaveSettingsPanel = new JPanel(new GridBagLayout());

        resaveSettingsPanel.add(resaveLabel, new GridBagConstraints(0, 0, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));

        resaveSettingsPanel.add(new JLabel("Threshold file size (in percent):"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        resaveSettingsPanel.add(thresholdValueLabel, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
        resaveSettingsPanel.add(resaveThresholdSlider, new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

        resaveSettingsPanel.add(new JLabel("JPG quality (in percent):"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
        resaveSettingsPanel.add(qualityValueLabel, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
        resaveSettingsPanel.add(resaveJpgQualitySlider, new GridBagConstraints(2, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

        resaveSettingsPanel.add(resaveExplanationLabel, new GridBagConstraints(0, 3, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

        final ChangeListener changeListener = l -> {
            final int jpgQuality = resaveJpgQualitySlider.getValue();
            final int fileSizeThreshold = resaveThresholdSlider.getValue();
            qualityValueLabel.setText(jpgQuality + "%");
            thresholdValueLabel.setText(fileSizeThreshold + "%");
            resaveExplanationLabel.setText(getResaveText(fileSizeThreshold, jpgQuality));
            updateStateConfigurations();
        };

        resaveJpgQualitySlider.addChangeListener(changeListener);
        resaveThresholdSlider.addChangeListener(changeListener);

        final int initialQuality = (int) (State.getResaveConfig().getQuality() * 100);
        final int initialThreshold = (int) (State.getResaveConfig().getThreshold() * 100);
        resaveJpgQualitySlider.setValue(initialQuality);
        resaveThresholdSlider.setValue(initialThreshold);

        return resaveSettingsPanel;
    }

    private static void updateStateConfigurations() {
        State.setConfigs(getResaveConfig(), getResizeConfig());
    }

    private static ResaveImageFileProcessor.ResaveConfig getResaveConfig() {
        final int fileSizeThreshold = resaveThresholdSlider.getValue();
        final int jpgQuality = resaveJpgQualitySlider.getValue();
        return new ResaveImageFileProcessor.ResaveConfig(jpgQuality / 100.0, fileSizeThreshold / 100.0);
    }

    private static Dimension getResizeMaxDimensions() {
        int maxWidth = -1;
        int maxHeight = -1;

        try {
            maxWidth = Integer.parseInt(resizeMaxWidthTextField.getText());
        } catch (NumberFormatException e) {
            // Nothing by intention
        }
        try {
            maxHeight = Integer.parseInt(resizeMaxHeightTextField.getText());
        } catch (NumberFormatException e) {
            // Nothing by intention
        }

        return new Dimension(maxWidth, maxHeight);
    }

    private static ResizeImageFileProcessor.ResizeConfig getResizeConfig() {
        final Dimension maxDimensions = getResizeMaxDimensions();
        return new ResizeImageFileProcessor.ResizeConfig(maxDimensions.width, maxDimensions.height, getResaveConfig().getQuality());
    }

    private static String getResaveText(int fileSizeThreshold, int jpgQuqality) {
        return "<html>Image will be resaved as jpg (lossy) with quality of " + jpgQuqality + "%<br>" +
                "only if resaving will result in a file size of " + fileSizeThreshold + "% (or less) of original size.</html>";
    }

    private static String getResizeText(int maxWidth, int maxHeight) {
        return "<html>Image will be resized (shrunken only, never enlarged), keeping its aspect ratio.<br>" +
                "Image will <i>fit</i> within a maximum pixel width of " + maxWidth + " and height " + maxHeight + ".</html>";
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }
}
