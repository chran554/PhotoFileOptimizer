package se.cha;

import se.cha.ui.ImageFileDropPanel;
import se.cha.ui.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;


public class PhotoFileOptimizer extends JFrame {

    private final ImageFileProcessorFactory fileProcessorFactory = new ImageFileProcessorFactory();

    public PhotoFileOptimizer() throws HeadlessException {
        super("Photo file optimizer");

        LogUtil.configureSlf4jSimpleLogger();

        final ImageFileDropPanel dropPanel = new ImageFileDropPanel();

        final ImageFileProcessorDropTargetListener dropTargetListener = new ImageFileProcessorDropTargetListener(fileProcessorFactory, dropPanel);
        new DropTarget(dropPanel, dropTargetListener);

        final SettingsPanel settingsPanel = new SettingsPanel();

        final CardLayout cardLayout = new CardLayout();
        final JPanel panel = new JPanel(cardLayout);
        panel.add("drop", dropPanel);
        panel.add("settings", settingsPanel);

        cardLayout.show(panel, "drop");

        dropPanel.addActionListener(e -> {
            if ("settings".equals(e.getActionCommand())) {
                cardLayout.show(panel, "settings");
            }
        });

        settingsPanel.addActionListener(e -> {
            if ("exit".equals(e.getActionCommand())) {
                cardLayout.show(panel, "drop");
            }
        });

        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Dimension preferredSize = getPreferredSize();
        final double scaleFactor = ImageUtils.getScaleFactorToFit(preferredSize, new Dimension(600, 600));

        setSize((int) (preferredSize.width * scaleFactor), (int) (preferredSize.height * scaleFactor));
    }

    public static void main(String[] args) {
        final PhotoFileOptimizer testFrame = new PhotoFileOptimizer();
        testFrame.setVisible(true);
    }
}
