package se.cha;

import lombok.Getter;
import se.cha.processor.ResaveImageFileProcessor;
import se.cha.processor.ResizeImageFileProcessor;

public final class State {

    private static final double DEFAULT_RESAVE_COMPRESSION_QUALITY = 0.9;
    private static final double DEFAULT_RESAVE_FILE_SIZE_CHANGE_FACTOR_THRESHOLD = 0.8;
    private static final int DEFAULT_RESIZE_MAX_WIDTH = 1600;
    private static final int DEFAULT_RESIZE_MAX_HEIGHT = 1600;

    @Getter
    private static ResaveImageFileProcessor.ResaveConfig resaveConfig = new ResaveImageFileProcessor.ResaveConfig(DEFAULT_RESAVE_COMPRESSION_QUALITY, DEFAULT_RESAVE_FILE_SIZE_CHANGE_FACTOR_THRESHOLD);
    @Getter
    private static ResizeImageFileProcessor.ResizeConfig resizeConfig = new ResizeImageFileProcessor.ResizeConfig(DEFAULT_RESIZE_MAX_WIDTH, DEFAULT_RESIZE_MAX_HEIGHT, DEFAULT_RESAVE_COMPRESSION_QUALITY);

    @Getter
    private static boolean renameEnabled = true;
    @Getter
    private static boolean resaveEnabled = true;
    @Getter
    private static boolean resizeEnabled = false;

    public static void setConfigs(ResaveImageFileProcessor.ResaveConfig resaveConfig, ResizeImageFileProcessor.ResizeConfig resizeConfig) {
        State.resaveConfig = resaveConfig;
        State.resizeConfig = resizeConfig;
    }

    public static void setEnabledProcessors(boolean renameEnabled, boolean resaveEnabled, boolean resizeEnabled) {
        State.renameEnabled = renameEnabled;
        State.resaveEnabled = resaveEnabled;
        State.resizeEnabled = resizeEnabled;
    }
}
