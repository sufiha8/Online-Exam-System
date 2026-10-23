package util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.function.IntConsumer;

public class ExamTimer {
    private final Timeline timeline;
    private int remainingSeconds;
    public ExamTimer(int durationMinutes, IntConsumer onTick, Runnable onFinished) {
        remainingSeconds = Math.max(1, durationMinutes) * 60;
        onTick.accept(remainingSeconds);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            onTick.accept(Math.max(0, remainingSeconds));
            if (remainingSeconds <= 0) {
                timelineStop();
                onFinished.run();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }
    public void start() { timeline.play(); }
    public void stop() { timeline.stop(); }
    private void timelineStop() { timeline.stop(); }
}
