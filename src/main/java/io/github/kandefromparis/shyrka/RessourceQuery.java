/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka;

/**
 * <p>RessourceQuery class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public class RessourceQuery {
    private Boolean status;
    private String query; 
    private String expectedResult;

    /**
     * <p>Constructor for RessourceQuery.</p>
     */
    public RessourceQuery() {
        this.status = null;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return the status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status the status to set
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * <p>Getter for the field <code>query</code>.</p>
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * <p>Setter for the field <code>query</code>.</p>
     *
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * <p>Getter for the field <code>expectedResult</code>.</p>
     *
     * @return the expectedResult
     */
    public String getExpectedResult() {
        return expectedResult;
    }

    /**
     * <p>Setter for the field <code>expectedResult</code>.</p>
     *
     * @param expectedResult the expectedResult to set
     */
    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }
    
       
}
