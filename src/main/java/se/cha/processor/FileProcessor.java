package se.cha.processor;

import se.cha.ProgressListener;

import java.io.File;
import java.util.Set;

public interface FileProcessor {

    Thread processFiles(Set<File> files, ProgressListener progressListener);
    boolean processFile(File file, Set<File> files, ProgressListener progressListener);
}
