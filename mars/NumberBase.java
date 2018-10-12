package mars;

import mars.util.Binary;

public enum NumberBase {
    BINARY("Binary", 2),
    TERNARY("Ternary", 3),
    QUATERNARY("Quaternary", 4),
    QUINARY("Quinary", 5),
    SENARY("Senary", 6),
    OCTAL("Octal", 8),
    DECIMAL("Decimal", 10),
    HEXADECIMAL("Hexadecimal", 16),
    ASCII("ASCII", 0);

    String key;
    int value;

    NumberBase(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public static NumberBase getBase(int radix) {
        switch(radix) {
            case 2: return BINARY;
            case 3: return TERNARY;
            case 4: return QUATERNARY;
            case 5: return QUINARY;
            case 6: return SENARY;
            case 8: return OCTAL;
            case 10: return DECIMAL;
            case 16: return HEXADECIMAL;
            case 0: return ASCII;

            default: throw new IllegalArgumentException("Unsupported radix");
        }
    }

    public String getKey() {
        return key;
    }

    /**
     * Produces a string form of an integer given the value and the
     * numerical base to convert it to.  There is an instance
     * method that uses the internally stored base.  This class
     * method can be used by anyone anytime.
     *
     * @param number the number (in base 10) to be converted
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatNumber(int number) {
        String result;

        switch(this) {
            case BINARY:
                result = Binary.intToBinaryString(number);
                break;

            case TERNARY:
                result = Integer.toString(number,3);
                break;

            case QUATERNARY:
                result = Integer.toString(number,4);
                break;

            case QUINARY:
                result = Integer.toString(number,5);
                break;

            case SENARY:
                result = Integer.toString(number,6);
                break;

            case OCTAL:
                result = Integer.toOctalString(number);
                break;

            case DECIMAL:
                result = Integer.toString(number);
                break;

            case HEXADECIMAL:
                result = Binary.intToHexString(number);
                break;

            case ASCII:
                result = Binary.intToAscii(number);
                break;

            default:
                result = Integer.toString(value);
                break;
        }

        return result;
    }

    /**
     * Produces a string form of an unsigned given the value and the
     * numerical base to convert it to.  This class
     * method can be used by anyone anytime.  If base is 16, result
     * is same as for formatNumber().  If base is 10, will produce
     * string version of unsigned value.  E.g. 0xffffffff will produce
     * "4294967295" instead of "-1".
     *
     * @param value the number to be converted
     * @param base  the numerical base to use (currently 10 or 16)
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatUnsignedInteger(int value) {
        if (this == NumberBase.HEXADECIMAL) {
            return Binary.intToHexString(value);
        } else {
            return Binary.unsignedIntToIntString(value);
        }
    }


    /**
     * Produces a string form of a float given the value and the
     * numerical base to convert it to.  There is an instance
     * method that uses the internally stored base.  This class
     * method can be used by anyone anytime.
     *
     * @param value the number to be converted
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatNumber(float value) {
        if (this == NumberBase.HEXADECIMAL) {
            return Binary.intToHexString(Float.floatToIntBits(value));
        } else {
            return Float.toString(value);
        }
    }


    /**
     * Produces a string form of a double given the value and the
     * numerical base to convert it to.  There is an instance
     * method that uses the internally stored base.  This class
     * method can be used by anyone anytime.
     *
     * @param value the number to be converted
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatNumber(double value) {
        if (this == NumberBase.HEXADECIMAL) {
            long lguy = Double.doubleToLongBits(value);
            return Binary.intToHexString(Binary.highOrderLongToInt(lguy)) +
                    Binary.intToHexString(Binary.lowOrderLongToInt(lguy)).substring(2);
        } else {
            return Double.toString(value);
        }
    }

    /**
     * Produces a string form of a float given an integer containing
     * the 32 bit pattern and the numerical base to use (10 or 16).  If the
     * base is 16, the string will be built from the 32 bits.  If the
     * base is 10, the int bits will be converted to float and the
     * string constructed from that.  Seems an odd distinction to make,
     * except that contents of floating point registers are stored
     * internally as int bits.  If the int bits represent a NaN value
     * (of which there are many!), converting them to float then calling
     * formatNumber(float, int) above, causes the float value to become
     * the canonical NaN value 0x7fc00000.  It does not preserve the bit
     * pattern!  Then converting it to hex string yields the canonical NaN.
     * Not an issue if display base is 10 since result string will be NaN
     * no matter what the internal NaN value is.
     *
     * @param value the int bits to be converted to string of corresponding float.
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatFloatNumber(int value) {
        if (this == NumberBase.HEXADECIMAL) {
            return Binary.intToHexString(value);
        } else {
            return Float.toString(Float.intBitsToFloat(value));
        }
    }

    /**
     * Produces a string form of a double given a long containing
     * the 64 bit pattern and the numerical base to use (10 or 16).  If the
     * base is 16, the string will be built from the 64 bits.  If the
     * base is 10, the long bits will be converted to double and the
     * string constructed from that.  Seems an odd distinction to make,
     * except that contents of floating point registers are stored
     * internally as int bits.  If the int bits represent a NaN value
     * (of which there are many!), converting them to double then calling
     * formatNumber(double, int) above, causes the double value to become
     * the canonical NaN value.  It does not preserve the bit
     * pattern!  Then converting it to hex string yields the canonical NaN.
     * Not an issue if display base is 10 since result string will be NaN
     * no matter what the internal NaN value is.
     *
     * @param value the long bits to be converted to string of corresponding double.
     * @return a String equivalent of the value rendered appropriately.
     */
    public String formatDoubleNumber(long value) {
        if (this == NumberBase.HEXADECIMAL) {
            return Binary.longToHexString(value);
        } else {
            return Double.toString(Double.longBitsToDouble(value));
        }
    }
}
