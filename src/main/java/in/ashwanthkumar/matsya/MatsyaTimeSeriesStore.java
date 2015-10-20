package in.ashwanthkumar.matsya;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import in.ashwanthkumar.aws.AZ;
import in.ashwanthkumar.aws.EC2InstanceType;
import in.ashwanthkumar.aws.Region;
import in.ashwanthkumar.tsdb.Key;
import in.ashwanthkumar.tsdb.Metric;
import in.ashwanthkumar.tsdb.TSDB;

import java.nio.ByteBuffer;
import java.util.List;

public class MatsyaTimeSeriesStore implements AutoCloseable {
    private TSDB delegate;

    public MatsyaTimeSeriesStore(TSDB delegate) {
        this.delegate = delegate;
    }

    public MatsyaTimeSeriesStore addPoints(final String machineType, final String region, final List<Point> points) throws Exception {
        List<Metric> metrics = Lists.transform(points, new Function<Point, Metric>() {
            @Override
            public Metric apply(Point input) {
                ByteBuffer value = ByteBuffer.allocate(Doubles.BYTES)
                        .putDouble(input.getPrice())
                        .putLong(input.getTimestamp());
                return new Metric()
                        .setKey(Key.of(toKey(machineType, region, input.getAvailabilityZone()), input.getTimestamp()))
                        .setValue(value.array());
            }
        });
        delegate.batchPut(metrics);
        delegate.flush();
        return this;
    }

    public List<Point> historyFor(String machineType, String region, String az, long start, long end) throws Exception {
        byte[][] values = delegate.scan(start, end, toKey(machineType, region, az));
        List<Point> points = Lists.newArrayList();
        for (int i = 0; i < values.length; i++) {
            ByteBuffer value = ByteBuffer.wrap(values[i]);
            Point point = new Point()
                    .setAvailabilityZone(az)
                    .setPrice(value.getDouble())
                    .setTimestamp(value.getLong());
            points.add(point);
        }

        return points;
    }

    public void close() throws Exception {
        delegate.close();
    }

    /**
     * Key format is 3 bytes
     * [   MachineType    |   Region  |   Availability Zone    ]
     *
     * @see EC2InstanceType
     * @see Region
     * @see AZ.US_EAST_1
     */
    private byte[] toKey(String machineType, String regionStr, String az) {
        Region region = Region.fromString(regionStr);
        byte machineBit = EC2InstanceType.fromString(machineType).id;
        byte regionBit = region.id;
        byte azBit = region.getAvailabilityZone(az).id();

        ByteBuffer buffer = ByteBuffer.allocate(3);
        return buffer
                .put(machineBit)
                .put(regionBit)
                .put(azBit)
                .array();
    }
}
