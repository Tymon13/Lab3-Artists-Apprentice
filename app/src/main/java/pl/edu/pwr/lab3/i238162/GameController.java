package pl.edu.pwr.lab3.i238162;

public class GameController {
    private final Object colourLock = new Object();
    private int currentRed;
    private int currentGreen;
    private int currentBlue;

    // an observer pattern would be an overkill here
    public void setCurrentColour(int[] rgbColour) {
        synchronized (colourLock) {
            currentRed = rgbColour[0];
            currentGreen = rgbColour[1];
            currentBlue = rgbColour[2];
        }
    }
}
