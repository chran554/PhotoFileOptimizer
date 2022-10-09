package se.cha;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;


public class ImageFileResaver extends JFrame {

    public ImageFileResaver() throws HeadlessException {
        super("Image file resaver");

        final ImageFileDropPanel dropPanel = new ImageFileDropPanel();

        final FileProcessor fileProcessor = new ResaveImageFileProcessor();
        final ImageFileProcessorDropTargetListener dropTargetListener = new ImageFileProcessorDropTargetListener(fileProcessor, dropPanel);
        new DropTarget(dropPanel, dropTargetListener);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(dropPanel, BorderLayout.CENTER);

        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Dimension preferredSize = getPreferredSize();
        final double scaleFactor = ImageUtils.getScaleFactorToFit(preferredSize, new Dimension(600, 600));

        setSize((int) (preferredSize.width * scaleFactor), (int) (preferredSize.height * scaleFactor));
    }

    public static void main(String[] args) {
        final ImageFileResaver testFrame = new ImageFileResaver();
        testFrame.setVisible(true);
    }
}
