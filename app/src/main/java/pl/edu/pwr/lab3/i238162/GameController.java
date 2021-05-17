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
    private final PlayerBucket redBucket = new PlayerBucket(0.1, 0, 1);
    private final PlayerBucket greenBucket = new PlayerBucket(0.1, 0, 1);
    private final PlayerBucket blueBucket = new PlayerBucket(0.1, 0, 1);

    private final MainActivity parentActivity;
    private final Workshop workshop;
    private int money = 0;

    public GameController(MainActivity parentActivity) {
        this.parentActivity = parentActivity;
        workshop = new Workshop(parentActivity);

        handler.postDelayed(gameTick = () -> {
            calculateGameTick();
            parentActivity.updateUi();
            handler.postDelayed(gameTick, gameTickLength);
        }, gameTickLength);
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    // an observer pattern would be an overkill here
    public void setCurrentColour(int[] rgbColour) {
        synchronized (visibleColourLock) {
            currentRedVisible = rgbColour[0];
            currentGreenVisible = rgbColour[1];
            currentBlueVisible = rgbColour[2];
        }
    }

    public void transferPaintToWorkshop() {
        workshop.transferPaint(Colour.Red, redBucket);
        workshop.transferPaint(Colour.Green, greenBucket);
        workshop.transferPaint(Colour.Blue, blueBucket);
    }

    public void collectMoney() {
        money += workshop.collectMoney();
        parentActivity.updateUi();
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
        long timeSpent = gameTickLength; // TODO: fix with actual time spent
        workshop.updateTick(timeSpent);

        synchronized (visibleColourLock) {
            Colour c = getMostVisibleColour();
            switch (c) {
                case Red:
                    redBucket.updateTick(timeSpent,
                            ImageProcessingHelper.colourSaturation(currentRedVisible));
                    greenBucket.updateTick(timeSpent, 0);
                    blueBucket.updateTick(timeSpent, 0);
                    break;
                case Green:
                    greenBucket.updateTick(timeSpent,
                            ImageProcessingHelper.colourSaturation(currentGreenVisible));
                    redBucket.updateTick(timeSpent, 0);
                    blueBucket.updateTick(timeSpent, 0);
                    break;
                case Blue:
                    blueBucket.updateTick(timeSpent,
                            ImageProcessingHelper.colourSaturation(currentBlueVisible));
                    redBucket.updateTick(timeSpent, 0);
                    greenBucket.updateTick(timeSpent, 0);
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

    public int getMoney() {
        return money;
    }
}
