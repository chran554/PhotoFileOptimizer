package se.cha;

import org.apache.commons.lang.StringUtils;

import java.io.File;

public abstract class LogUtil {

    private LogUtil() {
        // Nothing...
    }

    public static void configureSlf4jSimpleLogger() {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "hh:MM:ss");

        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        System.setProperty("org.slf4j.simpleLogger.showLogName", "false");
    }

    public static String format(String action, File file, String message) {
        final String fileText = (file != null) ? " \"" + file.getName() + "\"" : "";
        return "\t[" + fixActionText(action, 11) + "]" + fileText + " \t" + message;
    }

    private static String fixActionText(String text, int size) {
        return StringUtils.rightPad(StringUtils.left(text, size), size);
    }

    public static String format(String action, String message) {
        return format(action, null, message);
    }
}
