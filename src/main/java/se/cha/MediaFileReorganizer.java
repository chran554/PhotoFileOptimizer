package se.cha;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;


public class MediaFileReorganizer extends JFrame {


    public MediaFileReorganizer() throws HeadlessException {
        super("Media file re-organizer");

        final ImageFileDropPanel dropPanel = new ImageFileDropPanel();

        final FileProcessor fileProcessor = new MoveMediaFileProcessor();
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
        final MediaFileReorganizer testFrame = new MediaFileReorganizer();
        testFrame.setVisible(true);
    }
}
