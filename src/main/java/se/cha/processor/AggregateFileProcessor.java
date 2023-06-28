package se.cha.processor;

import se.cha.ProgressListener;

import java.io.File;
import java.util.List;
import java.util.Set;

public class AggregateFileProcessor extends ImageFileProcessorTemplate {

    private final List<FileProcessor> fileProcessors;

    public AggregateFileProcessor(boolean recursive, List<FileProcessor> fileProcessors) {
        super(recursive);
        this.fileProcessors = fileProcessors;
    }

    @Override
    public boolean processFile(File imageFile, Set<File> imageFiles, ProgressListener progressListener) {
        boolean processed = false;

        if (fileProcessors != null) {
            for (FileProcessor fileProcessor : fileProcessors) {
                processed |= fileProcessor.processFile(imageFile, imageFiles, progressListener);
            }
        }

        return processed;
    }
}
