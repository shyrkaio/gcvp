/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka;

import static io.github.kanedafromparis.shyrka.ShyrkaLabel.*;

/**
 * This Enum contains the possible conformity issue of a Shyrka Project With
 * error code 0x - for technical error 1x - for Project Owner issues 2x - for
 * backup issues
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public enum ConformityIssue {

    //@Todo have a smart code organisation
    NO_SHYRKA_CONFIGMAP(01, "MISSING_SHYRKA_CONFIGMAP", "The project need a shyrka confimap"),
    PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT_WRONG_FORMAT(02, "PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT_WRONG_FORMAT", "The annotation for " + L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT+" is in a wrong format, please use ISO_DATE_FORMAT: \"yyyy-MM-dd\" "),
    PROJECT_OWNER_ANNOTATION_WRONG_FORMAT(03, "PROJECT_OWNER_ANNOTATION_WRONG_FORMAT", "The annotation for " + A_PRODUCT_OWNER+ " is in a wrong format"),
    
    NO_PROJECT_OWNER_LABEL(11, "MISSING_PROJECT_OWNER_LABEL", "The project need a shyrka confimap, with label " + L_PRODUCT_OWNER),
    NO_PROJECT_OWNER_ANNOTATION(12, "MISSING_PROJECT_OWNER_ANNOTATION", "The project need a shyrka confimap, with annotation " + A_PRODUCT_OWNER),
    NO_PROJECT_OWNER_CONFIRMATION(13, "MISSING_PROJECT_OWNER_CONFIRMATION", "The project need a shyrka confimap, with label  " + L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT),
    NO_PROJECT_NAME_LABEL(14, "NO_PROJECT_NAME_LABEL", "The project need a labeled name"),

    
    PROJECT_CONFIRMATION_EXPIRED(24, "PROJECT_CONFIRMATION_EXPIRED", "The project has not been confirm for too long " + L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT),
    PROJECT_BACKUP_NOT_SET(22, "PROJECT_CONFIRMATION_EXPIRED", "The project has not been confirm for too long " + L_BACKUP),
    PROJECT_LAST_BACKUP_TOO_OLD(23, "PROJECT_CONFIRMATION_EXPIRED", "The project has not been confirm for too long " + A_BACKUP_LAST),

    /** the purpose is to check for conformity  to test */
    PROJECT_NOT_CONFORME_TO_TEST(31, "PROJECT_NOT_CONFORME_TO_TEST", "The project is not conform to the json test");

    /**
     * <p>Constructor for ConformityIssue.</p>
     *
     * @param errorCode a {@link java.lang.Integer} object.
     * @param errorMessageCode a {@link java.lang.String} object.
     * @param errorMessage a {@link java.lang.String} object.
     */
    ConformityIssue(Integer errorCode, String errorMessageCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorMessageCode = errorMessageCode;
    }
    private final Integer errorCode;
    private final String errorMessageCode;
    private final String errorMessage;

    /**
     * <p>Getter for the field <code>errorCode</code>.</p>
     *
     * @return the errorCode
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * <p>Getter for the field <code>errorMessageCode</code>.</p>
     *
     * @return the errorMessageCode
     */
    public String getErrorMessageCode() {
        return errorMessageCode;
    }

    /**
     * <p>Getter for the field <code>errorMessage</code>.</p>
     *
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "" + this.getErrorCode() + "-" + this.getErrorMessageCode() + ":" + this.getErrorMessage();
    }

}
