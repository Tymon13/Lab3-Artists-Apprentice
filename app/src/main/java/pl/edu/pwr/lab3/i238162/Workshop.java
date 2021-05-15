package pl.edu.pwr.lab3.i238162;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Size;

import java.util.Arrays;

public class Workshop {
    MainActivity parentActivity;
    // TODO: yes, yes, magic values. They will be replaced when upgrades or saving comes
    private final WorkshopBucket redBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket greenBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket blueBucket = new WorkshopBucket(0.1, 0, 1);

    Bitmap currentSource;
    int[] finalPainting;
    int[] currentPaintingState;
    int currentPaintedPixel;

    public Workshop(MainActivity activity) {
        parentActivity = activity;
        restorePaintingState();
    }

    private void restorePaintingState() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        currentSource = BitmapFactory.decodeResource(parentActivity.getResources(),
                R.drawable.painting_tutorial, options);
        int width = currentSource.getWidth();
        int height = currentSource.getHeight();
        int size = height * width;

        finalPainting = new int[size];
        currentSource.getPixels(finalPainting, 0, width, 0, 0, width, height);

        currentPaintingState = new int[size];
        Arrays.fill(currentPaintingState, 0xFF000000);

        currentPaintedPixel = 0;

        parentActivity.updateUi();
    }

    public void transferPaint(Colour colour, PlayerBucket bucket) {
        switch (colour) {
            case Red:
                redBucket.fillFrom(bucket);
                break;
            case Green:
                greenBucket.fillFrom(bucket);
                break;
            case Blue:
                blueBucket.fillFrom(bucket);
                break;
        }
    }

    public void updateTick(long timeSpentInMs) {

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

    public int[] getCurrentPaintingState() {
        return currentPaintingState;
    }

    public Size getCurrentPaintingSize() {
        return new Size(currentSource.getWidth(), currentSource.getHeight());
    }
}
