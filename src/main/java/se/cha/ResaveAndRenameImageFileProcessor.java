package se.cha;

import java.io.File;

public class ResaveAndRenameImageFileProcessor extends RecursiveImageFileProcessorTemplate {

    private static final RenameImageFileProcessor renameFileProcessor = new RenameImageFileProcessor();
    private static final ResaveImageFileProcessor resaveFileProcessor = new ResaveImageFileProcessor();

    boolean processFile(ProgressListener progressListener, File imageFile) {
        final boolean resaved = resaveFileProcessor.processFile(progressListener, imageFile);
        final boolean renamed = renameFileProcessor.processFile(progressListener, imageFile);

        return resaved || renamed;
    }

}
