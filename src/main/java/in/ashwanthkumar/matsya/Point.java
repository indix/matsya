package in.ashwanthkumar.matsya;

public class Point {
    private double price;
    private long timestamp;
    private String availabilityZone;

    public double getPrice() {
        return price;
    }

    public Point setPrice(double price) {
        this.price = price;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Point setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public Point setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
        return this;
    }
}
