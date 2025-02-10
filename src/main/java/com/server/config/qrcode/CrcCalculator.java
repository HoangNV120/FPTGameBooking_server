package com.server.config.qrcode;

import java.nio.charset.StandardCharsets;

public class CrcCalculator {
    private static final int POLYNOMIAL = 0x11021;
    private static final int INITIAL_VALUE = 0xFFFF;

    public static String calculateCRC(String qrData) {
        byte[] data = qrData.getBytes(StandardCharsets.UTF_8);
        int crc = compute(data);
        return String.format("%04X", crc); // Return as 4-digit hex
    }

    private static int compute(byte[] data) {
        int crc = INITIAL_VALUE;
        for (byte b : data) {
            crc ^= (b << 8);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
