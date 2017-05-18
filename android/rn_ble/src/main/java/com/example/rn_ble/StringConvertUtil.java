package com.example.rn_ble;

/**
 * Created by admin on 2017/5/8.
 */

public class StringConvertUtil {
    public StringConvertUtil() {
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if(bytes != null && bytes.length > 0) {
            for(int i = 0; i < bytes.length; ++i) {
                int v = bytes[i] & 255;
                String hv = Integer.toHexString(v);
                if(hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if(hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    public static byte[] intToByte(int res) {
        byte[] targets = new byte[4];

        for(int i = 0; i < targets.length; ++i) {
            targets[i] = (byte)(res >> 8 * (3 - i) & 255);
        }

        return targets;
    }

    public static int byteToInt(byte[] res) {
        int targets = 0;

        for(int i = 0; i < res.length; ++i) {
            targets += (res[i] & 255) << 8 * (3 - i);
        }

        return targets;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] byteMergerMultiple(byte[]... params) {
        byte[] targets = null;
        byte[][] var5 = params;
        int var4 = params.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte[] item = var5[var3];
            if(targets == null) {
                targets = item;
            } else {
                targets = byteMerger(targets, item);
            }
        }

        return targets;
    }

    public static String PadLeft(String res, int size) {
        String temp = res;
        int len = res.length();
        if(len < size) {
            for(int i = 0; i < size - len; ++i) {
                temp = "0" + temp;
            }
        }

        return temp;
    }

    public static byte[] uint16ToByte(int res) {
        try {
            byte[] ex = hexStringToBytes(PadLeft(Integer.toHexString(res), 4));
            byte[] targets = new byte[]{ex[1], ex[0]};
            return targets;
        } catch (Exception var3) {
            return null;
        }
    }

    public static byte[] uint8ToByte(int res) {
        try {
            byte[] ex = hexStringToBytes(PadLeft(Integer.toHexString(res), 2));
            return ex;
        } catch (Exception var2) {
            return null;
        }
    }

    public static int byteToUint16(byte[] res) {
        try {
            byte[] ex = new byte[]{res[1], res[0]};
            return Integer.parseInt(bytesToHexString(ex), 16);
        } catch (Exception var2) {
            return 0;
        }
    }
}
