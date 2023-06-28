package se.cha;

import lombok.Getter;
import se.cha.processor.ResaveImageFileProcessor;

public final class State {

    private static final float DEFAULT_RESAVE_COMPRESSION_QUALITY = 0.9f;
    private static final double DEFAULT_RESAVE_FILE_SIZE_CHANGE_FACTOR_THRESHOLD = 0.8;

    @Getter
    private static ResaveImageFileProcessor.ResaveConfig resaveConfig = new ResaveImageFileProcessor.ResaveConfig(DEFAULT_RESAVE_COMPRESSION_QUALITY, DEFAULT_RESAVE_FILE_SIZE_CHANGE_FACTOR_THRESHOLD);

    @Getter
    private static boolean renameEnabled = true;
    @Getter
    private static boolean resaveEnabled = true;
    @Getter
    private static boolean rescaleEnabled = false;

    public static void setConfigs(ResaveImageFileProcessor.ResaveConfig resaveConfig) {
        State.resaveConfig = resaveConfig;
    }

    public static void setEnabledProcessors(boolean renameEnabled, boolean resaveEnabled, boolean rescaleEnabled) {
        State.renameEnabled = renameEnabled;
        State.resaveEnabled = resaveEnabled;
        State.rescaleEnabled = rescaleEnabled;
    }
}
