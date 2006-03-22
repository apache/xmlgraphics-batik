package org.w3c.dom.window;

public interface WindowTimers {
    int setTimeout(TimerListener listener, int milliseconds);
    void clearTimeout(int timerID);

    int setInterval(TimerListener listener, int milliseconds);
    void clearInterval(int timerID);
}
