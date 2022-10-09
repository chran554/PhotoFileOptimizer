package se.cha;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;

/**
 * Remove duplicate HEIC images when there is a processed iPhone JPG image present.
 * (iPhone creates a processed \"IMG_Ennnn.jpg\" image from \"IMG_nnnn.heic\" original.
 */
public class DuplicateHeicImageRemover extends JFrame {

    public DuplicateHeicImageRemover() throws HeadlessException {
        super("Remove duplicate HEIC images");

        LogUtil.configureSlf4jSimpleLogger();

        final ImageFileDropPanel dropPanel = new ImageFileDropPanel();

        final FileProcessor fileProcessor = new RemoveDuplicateHeicFileProcessor();
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
        final se.cha.DuplicateHeicImageRemover testFrame = new se.cha.DuplicateHeicImageRemover();
        testFrame.setVisible(true);
    }
}