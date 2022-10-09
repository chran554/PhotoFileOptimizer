package se.cha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageFileProcessorDropTargetListener implements DropTargetListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFileProcessorDropTargetListener.class.getName());

    private final FileProcessor fileProcessor;
    private final ProgressListener progressListener;

    public ImageFileProcessorDropTargetListener(FileProcessor fileProcessor, ProgressListener progressListener) {
        this.fileProcessor = fileProcessor;
        this.progressListener = progressListener;
    }

    public void dragEnter(DropTargetDragEvent dtde) { /* Nothing by intention */ }

    public void dragOver(DropTargetDragEvent dtde) { /* Nothing by intention */ }

    public void dropActionChanged(DropTargetDragEvent dtde) { /* Nothing by intention */ }

    public void dragExit(DropTargetEvent dte) { /* Nothing by intention */ }

    public void drop(DropTargetDropEvent dtde) {
        LOGGER.trace("Drop in progress...");

        // final DataFlavor[] currentDataFlavors = dtde.getCurrentDataFlavors();
        final Transferable transferable = dtde.getTransferable();

        final DataFlavor[] transferDataFlavors = transferable.getTransferDataFlavors();
        final List<DataFlavor> dataFlavorList = Arrays.asList(transferDataFlavors);
        //System.out.println("transferDataFlavors:\n" + StringUtils.join(dataFlavorList, "\n"));

        try {
            boolean flavorFound = false;
            for (DataFlavor dataFlavor : dataFlavorList) {
                if (dataFlavor.isFlavorJavaFileListType()) {
                    flavorFound = true;

                    dtde.acceptDrop(DnDConstants.ACTION_COPY);

                    final List<File> droppedFiles = (List<File>) transferable.getTransferData(dataFlavor);
                    //System.out.println("transferData:\n" + StringUtils.join(transferData, "\n"));

                    fileProcessor.processFiles(droppedFiles, progressListener);

                    dtde.dropComplete(true);
                }
            }

            if (!flavorFound) {
                LOGGER.error("No suitable file list flavor could be found...");
                dtde.rejectDrop();
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }

    }

}
