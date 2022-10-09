package se.cha;

import java.io.File;
import java.util.List;

public interface FileProcessor {

    Thread processFiles(List<File> files, ProgressListener progressListener);
}
