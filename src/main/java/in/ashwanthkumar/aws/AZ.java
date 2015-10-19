package in.ashwanthkumar.aws;


public class AZ {
    public interface AZType {
        byte id();
    }

    // FIXME - Remove this dummy implementation
    public static class NotImplementedAZ implements AZType {

        @Override
        public byte id() {
            throw new RuntimeException("id() not implemented for this AZ");
        }

        public static AZType[] values() {
            throw new RuntimeException("values() not implemented for this AZ");
        }

        public static AZType fromString(String str) {
            throw new RuntimeException("fromString(String) not implemented for this AZ");
        }
    }

    public enum US_EAST_1 implements AZType {
        US_EAST_1A(0),
        US_EAST_1B(1),
        US_EAST_1C(2),
        US_EAST_1D(3),
        US_EAST_1E(4);

        public byte id;

        US_EAST_1(int id) {
            this.id = (byte) id;
        }

        public byte id() {
            return id;
        }

        /**
         * We follow the convention of Upper casing the AZ name and replace - (hypen) with _ (underscore).
         */
        public static US_EAST_1 fromString(String az) {
            return US_EAST_1.valueOf(az.toUpperCase().replace('-', '_'));
        }
    }

    public enum US_WEST_1 implements AZType {
        US_WEST_1A(0),
        US_WEST_1C(0);

        public byte id;

        US_WEST_1(int id) {
            this.id = (byte) id;
        }

        public byte id() {
            return id;
        }

        public static US_WEST_1 fromString(String az) {
            return US_WEST_1.valueOf(az.toUpperCase().replace('-', '_'));
        }

    }

    public enum US_WEST_2 implements AZType {
        US_WEST_2A(0),
        US_WEST_2B(1),
        US_WEST_2C(2);

        public byte id;

        US_WEST_2(int id) {
            this.id = (byte) id;
        }

        @Override
        public byte id() {
            return 0;
        }

        public static US_WEST_2 fromString(String az) {
            return US_WEST_2.valueOf(az.toUpperCase().replace('-', '_'));
        }
    }

}
