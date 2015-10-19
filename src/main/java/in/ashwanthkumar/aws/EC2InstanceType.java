package in.ashwanthkumar.aws;

/*
    Byte ids for each instance type so that we can store these instead of the machine types as String
 */
public enum EC2InstanceType {
    M1_SMALL(0),
    M1_MEDIUM(1),
    M1_LARGE(2),
    M1_XLARGE(3),
    C1_MEDIUM(4),
    C1_XLARGE(5),
    CG1_4XLARGE(6),
    M2_XLARGE(7),
    M2_2XLARGE(8),
    M2_4XLARGE(9),
    CR1_8XLARGE(10),
    HI1_4XLARGE(11),
    HS1_8XLARGE(12),
    T1_MICRO(13),
    T2_MICRO(14),
    T2_SMALL(15),
    T2_MEDIUM(16),
    T2_LARGE(17),
    M4_LARGE(18),
    M4_XLARGE(19),
    M4_2XLARGE(20),
    M4_4XLARGE(21),
    M4_10XLARGE(22),
    M3_MEDIUM(23),
    M3_LARGE(24),
    M3_XLARGE(25),
    M3_2XLARGE(26),
    C4_LARGE(27),
    C4_XLARGE(28),
    C4_2XLARGE(29),
    C4_4XLARGE(30),
    C4_8XLARGE(31),
    C3_LARGE(32),
    C3_XLARGE(33),
    C3_2XLARGE(34),
    C3_4XLARGE(35),
    C3_8XLARGE(36),
    CC2_8XLARGE(37),
    G2_2XLARGE(38),
    G2_8XLARGE(39),
    R3_LARGE(40),
    R3_XLARGE(41),
    R3_2XLARGE(42),
    R3_4XLARGE(43),
    R3_8XLARGE(44),
    I2_XLARGE(45),
    I2_2XLARGE(46),
    I2_4XLARGE(47),
    I2_8XLARGE(48),
    D2_XLARGE(49),
    D2_2XLARGE(50),
    D2_4XLARGE(51),
    D2_8XLARGE(52);

    public byte id;

    EC2InstanceType(int id) {
        this.id = (byte) id;
    }

    /**
     * We follow an convention that all insance types are UPPERCASED and . (dot) are placed with an _ (underscore).
     *
     * @param instanceType
     * @return
     */
    public static EC2InstanceType fromString(String instanceType) {
        return EC2InstanceType.valueOf(instanceType.toUpperCase().replace('.', '_'));
    }
}
