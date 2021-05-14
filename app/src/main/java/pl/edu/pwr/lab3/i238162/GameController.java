package pl.edu.pwr.lab3.i238162;

import android.os.Handler;
import android.util.Log;

public class GameController {
    private final Handler handler = new Handler();
    private Runnable gameTick;
    private final long gameTickLength = 1000;


    private final Object visibleColourLock = new Object();
    private int currentRedVisible;
    private int currentGreenVisible;
    private int currentBlueVisible;

    // TODO: yes, yes, magic values. They will be replaced when upgrades or saving comes
    private final Bucket redBucket = new Bucket(0.1, 0, 1);
    private final Bucket greenBucket = new Bucket(0.1, 0, 1);
    private final Bucket blueBucket = new Bucket(0.1, 0, 1);

    public GameController() {
        handler.postDelayed(gameTick = () -> {
            calculateGameTick();
            handler.postDelayed(gameTick, gameTickLength);
        }, gameTickLength);
    }

    // an observer pattern would be an overkill here
    public void setCurrentColour(int[] rgbColour) {
        synchronized (visibleColourLock) {
            currentRedVisible = rgbColour[0];
            currentGreenVisible = rgbColour[1];
            currentBlueVisible = rgbColour[2];
        }
    }

    public Bucket getBucket(Colour colour) {
        switch (colour) {
            case Red:
                return redBucket;
            case Green:
                return greenBucket;
            case Blue:
                return blueBucket;
            default:
                throw new IllegalStateException("Unexpected value: " + colour);
        }
    }

    private void calculateGameTick() {
        synchronized (visibleColourLock) {
            Colour c = getMostVisibleColour();
            long timeSpent = gameTickLength; // TODO: fix with actual time spent
            switch (c) {
                case Red:
                    redBucket.updateTick(timeSpent, colourSaturation(currentRedVisible));
                    break;
                case Green:
                    greenBucket.updateTick(timeSpent, colourSaturation(currentGreenVisible));
                    break;
                case Blue:
                    blueBucket.updateTick(timeSpent, colourSaturation(currentBlueVisible));
                    break;
            }
        }
    }

    private Colour getMostVisibleColour() {
        Log.d(getClass().getSimpleName(), String.format("Current colours: %d %d %d",
                currentRedVisible, currentGreenVisible, currentBlueVisible));
        if (currentRedVisible > currentGreenVisible && currentRedVisible > currentBlueVisible) {
            return Colour.Red;
        } else if (currentGreenVisible > currentBlueVisible) {
            return Colour.Green;
        } else {
            return Colour.Blue;
        }

    }

    private double colourSaturation(int colourValue) {
        return colourValue / 255.0;
    }
}
