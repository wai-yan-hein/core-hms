/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.exception;

/**
 *
 * @author winswe
 */
public class PharmacyDataAccessLayerException extends RuntimeException{
    public PharmacyDataAccessLayerException() {
    }

    public PharmacyDataAccessLayerException(String message) {
        super(message);
    }

    public PharmacyDataAccessLayerException(Throwable cause) {
        super(cause);
    }

    public PharmacyDataAccessLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
