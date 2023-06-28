package se.cha;

import se.cha.processor.AggregateFileProcessor;
import se.cha.processor.FileProcessor;
import se.cha.processor.RenameImageFileProcessor;
import se.cha.processor.ResaveImageFileProcessor;

import java.util.ArrayList;
import java.util.List;

public final class ImageFileProcessorFactory {

    public FileProcessor createFileProcessor() {
        final List<FileProcessor> fileProcessors = new ArrayList<>();

        if (State.isRescaleEnabled()) {
            //fileProcessors.add(new ResizeImageFileProcessor());
        }

        if (State.isResaveEnabled() && !State.isRescaleEnabled()) {
            fileProcessors.add(new ResaveImageFileProcessor(State.getResaveConfig()));
        }

        if (State.isRenameEnabled()) {
            fileProcessors.add(new RenameImageFileProcessor());
        }

        return new AggregateFileProcessor(true, fileProcessors);
    }
}
