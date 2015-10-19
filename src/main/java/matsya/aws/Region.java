package matsya.aws;

// Full list from http://docs.aws.amazon.com/general/latest/gr/rande.html#ec2_region
public enum Region {
    US_EAST_1(0),      /* North Virginia        */
    US_WEST_1(0),      /* Northern California   */
    US_WEST_2(0),      /* Oregon                */
    SA_EAST_1(0),      /* Sao Paulo             */
    EU_WEST_1(0),      /* Ireland               */
    EU_CENTRAL_1(0),   /* Frankfurt             */
    AP_SOUTHEAST_1(0), /* Singapore             */
    AP_SOUTHEAST_2(0), /* Sydney                */
    AP_NORTHEAST_1(0); /* Tokyo                 */

    public byte id;

    Region(int id) {
        this.id = (byte) id;
    }

    public AZ.AZType getAvailabilityZone(String azName) {
        switch (this) {
            case US_EAST_1:
                return AZ.US_EAST_1.fromString(azName);
            case US_WEST_1:
                return AZ.US_WEST_1.fromString(azName);
            case US_WEST_2:
                return AZ.US_WEST_2.fromString(azName);
            case SA_EAST_1:
                return AZ.NotImplementedAZ.fromString(azName);
            case EU_WEST_1:
                return AZ.NotImplementedAZ.fromString(azName);
            case EU_CENTRAL_1:
                return AZ.NotImplementedAZ.fromString(azName);
            case AP_SOUTHEAST_1:
                return AZ.NotImplementedAZ.fromString(azName);
            case AP_SOUTHEAST_2:
                return AZ.NotImplementedAZ.fromString(azName);
            case AP_NORTHEAST_1:
                return AZ.NotImplementedAZ.fromString(azName);
            default:
                throw new RuntimeException("Invalid Region " + this);
        }
    }

    public static Region fromString(String region) {
        return Region.valueOf(region.toUpperCase().replace('-', '_'));
    }

}
