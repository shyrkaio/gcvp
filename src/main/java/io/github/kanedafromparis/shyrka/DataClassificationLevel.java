/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka;

import static io.github.kanedafromparis.shyrka.ShyrkaLabel.*;

/**
 * This Enum contains the possible Security Data level of a Shyrka Project With
 * error code
 * https://en.wikipedia.org/wiki/Classified_information#Confidential
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public enum DataClassificationLevel {

    //For fun, they should never be used
    TOP_SECRET(-99, "TOP_SECRET"),
    SECRET(-1, "SECRET"),
    //More classical entreprise classification
    CONFIDENTIAL(0, "CONFIDENTIAL"),
    RESTRICTED(1, "RESTRICTED"),
    OFFICIAL(2, "OFFICIAL"),
    //Secific
    UNCLASSIFIED(7, "UNCLASSIFIED"),
    CLEARANCE(8, "CLEARANCE"),
    COMPARTMENTED_INFORMATION(80, "COMPARTMENTED_INFORMATION");

    /**
     * <p>Constructor for DataClassificationLevel.</p>
     *
     * @param securityLevel a {@link java.lang.Integer} object.
     * @param securityLevelName a {@link java.lang.String} object.
     */
    DataClassificationLevel(Integer securityLevel, String securityLevelName) {
        this.securityLevel = securityLevel;
        this.securityLevelName = securityLevelName;
    }
    private final Integer securityLevel;

    private final String securityLevelName;

}
