package com.bbf.cruise.service;

import com.bbf.cruise.helpers.QRScanFeedback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import model.Car;

public class QRProcessingService {

    public static QRScanFeedback processScannedQR(Result rawResult) {
        try {
            String carRegNumber = extractCarRegistrationNumber(rawResult.getText());
            return new QRScanFeedback(true, carRegNumber, "Successfully parsed registration number.");
        } catch (JSONException e) {
            return handleParsingError(e);
        } catch (Exception e) {
            return handleUnspecificError(e);
        }

    }


    private static String extractCarRegistrationNumber(String rawText) throws JSONException {
        JSONObject o = new JSONObject(rawText);
        return o.get("car").toString();
    }

    private static QRScanFeedback handleParsingError(Throwable e) {
        e.printStackTrace();
        return new QRScanFeedback(false, "QR Code is not valid.");
    }

    private static QRScanFeedback handleUnspecificError(Exception e) {
        e.printStackTrace();
        return new QRScanFeedback(false, "Something wrong happened. Please try again or report the issue.");
    }

}
