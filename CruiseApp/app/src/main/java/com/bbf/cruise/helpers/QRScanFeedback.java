package com.bbf.cruise.helpers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

public class QRScanFeedback {

    private boolean success;
    private String parsed;
    private String message;

    public QRScanFeedback() {
    }

    public QRScanFeedback(boolean success, String parsed, String message) {
        this.success = success;
        this.parsed = parsed;
        this.message = message;
    }

    public QRScanFeedback(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getParsed() {
        return parsed;
    }

    public void setParsed(String parsed) {
        this.parsed = parsed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
