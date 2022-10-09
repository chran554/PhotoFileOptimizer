package se.cha;

import java.awt.*;

public interface ProgressListener {

    void setImage(Image image);

    void resetProgress(String message);

    void setProgress(int current, int max, String message);
}
