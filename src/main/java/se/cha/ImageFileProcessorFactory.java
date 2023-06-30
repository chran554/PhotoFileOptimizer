package se.cha;

import se.cha.processor.*;

import java.util.ArrayList;
import java.util.List;

public final class ImageFileProcessorFactory {

    public FileProcessor createFileProcessor() {
        final List<FileProcessor> fileProcessors = new ArrayList<>();

        if (State.isResizeEnabled()) {
            fileProcessors.add(new ResizeImageFileProcessor(State.getResizeConfig()));
        }

        if (State.isResaveEnabled() && !State.isResizeEnabled()) {
            fileProcessors.add(new ResaveImageFileProcessor(State.getResaveConfig()));
        }

        if (State.isRenameEnabled()) {
            fileProcessors.add(new RenameImageFileProcessor());
        }

        return new AggregateFileProcessor(true, fileProcessors);
    }
}
