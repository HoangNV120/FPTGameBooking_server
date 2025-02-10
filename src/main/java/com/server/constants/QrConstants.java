package com.server.constants;

import static com.server.config.qrcode.QrDataBuilder.getLengthAndValue;

public class QrConstants {
    public static final String IDENTIFIER = "00";
    public static final String LENGTH = "02";
    public static final String VALUE = "01";
    public static final String CRC_ID = "6304";
    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;

    public static final String QRIBFTTA = "QRIBFTTA";
    public static final String QRIBFTTC = "QRIBFTTC";

    public static final String PAYLOAD = "000201";
    public static final String POINT_OF_METHOD = "010212";
    public static final String GUID = "00" + getLengthAndValue("A000000727");
    public static final String SERVICE_CODE = "02" + getLengthAndValue(QRIBFTTA);
    public static final String COUNTRY_CODE = "5802VN";
    public static final String TRAN_CURRENCY = "5303704";
}
