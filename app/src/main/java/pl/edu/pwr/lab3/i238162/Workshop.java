package pl.edu.pwr.lab3.i238162;

public class Workshop {
    // TODO: yes, yes, magic values. They will be replaced when upgrades or saving comes
    private final WorkshopBucket redBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket greenBucket = new WorkshopBucket(0.1, 0, 1);
    private final WorkshopBucket blueBucket = new WorkshopBucket(0.1, 0, 1);

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
}
