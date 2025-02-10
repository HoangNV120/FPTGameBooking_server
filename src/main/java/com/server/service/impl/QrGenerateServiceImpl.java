package com.server.service.impl;

import com.server.config.qrcode.CrcCalculator;
import com.server.config.qrcode.QrCodeGenerator;
import com.server.config.qrcode.QrDataBuilder;
import com.server.constants.QrConstants;
import com.server.dto.request.transaction.TransactionRequest;
import com.server.enums.QrGenerateData;
import com.server.service.QrGenerateService;
import com.server.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrGenerateServiceImpl implements QrGenerateService {
    @Override
    public String generateQr(TransactionRequest request) throws Exception {

        String acquirerId = QrConstants.IDENTIFIER + QrDataBuilder.getLengthAndValue(QrGenerateData.valueOf(request.getBank()).getAcquirerId());
        String consumerId = QrConstants.VALUE + QrDataBuilder.getLengthAndValue(request.getConsumerId());
        String strTotalAccount = QrConstants.VALUE + QrDataBuilder.getLengthAndValue(acquirerId + consumerId);
        String consumerAccountInformation = "38" + QrDataBuilder.getLengthAndValue(QrConstants.GUID + strTotalAccount + QrConstants.SERVICE_CODE);
        String transactionAmount ="54"+QrDataBuilder.getLengthAndValue(request.getAmount());
        String additionalDataField = "62340107NPS68690819thanh toan don hang";

        String qrDataWithoutCRC = QrDataBuilder.buildQrData(
                QrConstants.PAYLOAD, QrConstants.POINT_OF_METHOD, consumerAccountInformation,
                QrConstants.TRAN_CURRENCY, transactionAmount, QrConstants.COUNTRY_CODE, additionalDataField).toString();

        String crc = CrcCalculator.calculateCRC(qrDataWithoutCRC);
        String qrDataWithCRC = qrDataWithoutCRC + crc;

        BufferedImage qrImage = QrCodeGenerator.generateQrCode(qrDataWithCRC);
        ImageUtils.saveImage(qrImage, "dynamic_qr_with.png");
        return qrDataWithCRC;
    }
}
