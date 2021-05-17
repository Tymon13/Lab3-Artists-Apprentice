package pl.edu.pwr.lab3.i238162;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Workshop {
    MainActivity parentActivity;
    Random random;
    // TODO: yes, yes, magic values. They will be replaced when upgrades or saving comes
    private final WorkshopBucket redBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket greenBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket blueBucket = new WorkshopBucket(0.1, 0, 1);

    private final ArrayList<Integer> images8;
    private final ArrayList<Integer> images16;
    private final ArrayList<Integer> images32;
    private final ArrayList<Integer> images64;

    Bitmap currentSource;
    int[] finalPainting;
    int[] currentPaintingState;
    int currentPaintedPixel;
    boolean isPaintingFinished;

    private int moneyToCollect = 0;

    public Workshop(MainActivity activity) {
        parentActivity = activity;
        random = new Random();
        loadNextPainting(R.drawable.painting_tutorial);
        images8 = discoverImages("8x8");
        images16 = discoverImages("16x16");
        images32 = discoverImages("32x32");
        images64 = discoverImages("64x64");
    }

    @SuppressLint("DefaultLocale")
    private ArrayList<Integer> discoverImages(String size) {
        Resources res = parentActivity.getResources();
        String pkg = parentActivity.getPackageName();
        ArrayList<Integer> result = new ArrayList<>();
        int index = 0;
        int resId;
        do {
            resId = res.getIdentifier(String.format("pixel_%s_%d", size, index), "drawable", pkg);
            result.add(resId);
            ++index;
        } while (resId != 0);
        result.remove(Integer.valueOf(0));
        return result;
    }

    private void loadNextPainting(int paintingId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        currentSource = BitmapFactory.decodeResource(parentActivity.getResources(), paintingId, options);
        int width = currentSource.getWidth();
        int height = currentSource.getHeight();
        int size = height * width;

        finalPainting = new int[size];
        currentSource.getPixels(finalPainting, 0, width, 0, 0, width, height);

        currentPaintingState = new int[size];
        Arrays.fill(currentPaintingState, 0xFF000000);

        currentPaintedPixel = 0;
        isPaintingFinished = false;

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

    public WorkshopBucket getBucket(Colour colour) {
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

    public void updateTick(long timeSpentInMs) {
        if (isPaintingFinished) {
            return;
        }

        updatePixel(timeSpentInMs, Colour.Red);
        updatePixel(timeSpentInMs, Colour.Green);
        updatePixel(timeSpentInMs, Colour.Blue);
        movePixelPointer();
    }

    private void movePixelPointer() {
        if (currentPaintingState[currentPaintedPixel] == finalPainting[currentPaintedPixel]) {
            ++currentPaintedPixel;
        }

        if (currentPaintedPixel == currentPaintingState.length) {
            isPaintingFinished = true;
            moneyToCollect += currentPaintingState.length;
        }
    }

    private void updatePixel(long timeSpentInMs, Colour colour) {
        int currentPixelValue = getPixelFromPainting(currentPaintingState, colour);
        int expectedPixelValue = getPixelFromPainting(finalPainting, colour);
        double missingAmount = getRequiredPaintAmount(expectedPixelValue, currentPixelValue);

        WorkshopBucket currentBucket = getBucket(colour);
        double paintToAdd = currentBucket.extractPerTick(timeSpentInMs, missingAmount);
        int pixelValueToAdd = ImageProcessingHelper.pixelValue(paintToAdd);
        int clampedPixelValue = Math.min(currentPixelValue + pixelValueToAdd, expectedPixelValue);

        setPixelToPainting(currentPaintingState, colour, clampedPixelValue);
    }

    private double getRequiredPaintAmount(int expectedPixelValue, int currentPixelValue) {
        return ImageProcessingHelper.colourSaturation(expectedPixelValue - currentPixelValue);
    }

    private int getPixelFromPainting(int[] painting, Colour colour) {
        int shift = getPixelShift(colour);
        return (painting[currentPaintedPixel] >> shift) & 0xFF;
    }

    private void setPixelToPainting(int[] painting, Colour colour, int value) {
        int shift = getPixelShift(colour);
        int pixel = painting[currentPaintedPixel];
        int mask = 0xFF << shift;
        value = value << shift;
        pixel = (pixel & ~mask) | value;
        painting[currentPaintedPixel] = pixel;
    }

    private int getPixelShift(Colour colour) {
        switch (colour) {
            case Red:
                return 16;
            case Green:
                return 8;
            case Blue:
                return 0;
        }
        return 0;
    }

    public int getMoneyToCollect() {
        return moneyToCollect;
    }

    public int collectMoney() {
        int money = moneyToCollect;
        moneyToCollect = 0;
        if (isPaintingFinished) {
            int nextPainting = getNextPaintingId();
            loadNextPainting(nextPainting);
        }
        return money;
    }

    private int getNextPaintingId() {
        if (!images8.isEmpty()) {
            return getPaintingIdFromArray(images8);
        } else if (!images16.isEmpty()) {
            return getPaintingIdFromArray(images16);
        } else if (!images32.isEmpty()) {
            return getPaintingIdFromArray(images32);
        } else if (!images64.isEmpty()) {
            return getPaintingIdFromArray(images64);
        }
        throw new RuntimeException("Unimplemented, sorry not sorry");
    }

    private int getPaintingIdFromArray(ArrayList<Integer> images) {
        int index = random.nextInt(images.size());
        int id = images.get(index);
        images.remove(index);
        return id;
    }
}
