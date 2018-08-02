/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.projectchecker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <p>Checker class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "apiVersion",
    "kind",
    "metadata",
    "spec"
})
public class Checker {
    @JsonProperty("apiVersion")
    private java.lang.String apiVersion = "erebus.shyrka.io/v1beta1";
    
    @JsonProperty("kind")
    private java.lang.String kind = "ProjectChecker";
    
    @JsonProperty("metadata")
    private Metadata metadata;
    
    @JsonProperty("spec")
    private Spec spec;

    /**
     * <p>Getter for the field <code>apiVersion</code>.</p>
     *
     * @return the apiVersion
     */
    @JsonProperty("apiVersion")
    public java.lang.String getApiVersion() {
        return apiVersion;
    }

    /**
     * <p>Setter for the field <code>apiVersion</code>.</p>
     *
     * @param apiVersion the apiVersion to set
     */
    @JsonProperty("apiVersion")
    public void setApiVersion(java.lang.String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * <p>Getter for the field <code>kind</code>.</p>
     *
     * @return the kind
     */
    @JsonProperty("kind")
    public java.lang.String getKind() {
        return kind;
    }

    /**
     * <p>Setter for the field <code>kind</code>.</p>
     *
     * @param kind the kind to set
     */
    @JsonProperty("kind")
    public void setKind(java.lang.String kind) {
        this.kind = kind;
    }

    /**
     * <p>Getter for the field <code>metadata</code>.</p>
     *
     * @return the metadata
     */
    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * <p>Setter for the field <code>metadata</code>.</p>
     *
     * @param metadata the metadata to set
     */
    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * <p>Getter for the field <code>spec</code>.</p>
     *
     * @return the spec
     */
    @JsonProperty("spec")
    public Spec getSpec() {
        return spec;
    }

    /**
     * <p>Setter for the field <code>spec</code>.</p>
     *
     * @param spec the spec to set
     */
    @JsonProperty("spec")
    public void setSpec(Spec spec) {
        this.spec = spec;
    }
}
