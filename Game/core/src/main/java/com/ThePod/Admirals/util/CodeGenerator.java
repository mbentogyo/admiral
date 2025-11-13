package com.ThePod.Admirals.util;

public class CodeGenerator {

    /**
     * Encodes an IP address into an 8-digit code
     * @param ip String of IP address
     * @return 8-character code
     * @throws IllegalArgumentException if IP is not valid
     */
    public static String encode(String ip) throws IllegalArgumentException {
        if (!ip.matches("^(?:(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) throw new IllegalArgumentException("Invalid IP address");

        String[] nums = ip.split("\\.");
        char[] chars = new char[8];
        int previous = 0, c = 0;

        for (int i = 0; i < 4; i++) {
            int num = Integer.parseInt(nums[i]);
            int i1 = num >> 4;
            int i2 = num & 15;
            int pmod = previous % 26;
            chars[c++] = (char) ((i1 + pmod) % 26 + 65);
            chars[c++] = (char) ((i2 + pmod) % 26 + 65);
            previous += i1 + i2;
        }

        return new String(chars);
    }

    /**
     * Decodes an 8-digit code back into the IP address
     * @param code the 8-digit code
     * @return original IP address
     * @throws IllegalArgumentException if the code is invalid
     */
    public static String decode(String code) throws IllegalArgumentException {
        if (code.length() != 8) throw new IllegalArgumentException("Invalid code");
        StringBuilder sb = new StringBuilder(15);
        int previous = 0;

        for (int i = 0; i < 8; i += 2) {
            int pmod = previous % 26;
            int i1 = code.charAt(i) - 65 - pmod;
            int i2 = code.charAt(i + 1) - 65 - pmod;
            if (i1 < 0) i1 += 26;
            if (i2 < 0) i2 += 26;
            previous += i1 + i2;
            sb.append((i1 << 4) + i2).append('.');
        }
        sb.setLength(sb.length() - 1);

        if (!sb.toString().matches("^(?:(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) throw new IllegalArgumentException("Invalid code");
        else return sb.toString();
    }
}
