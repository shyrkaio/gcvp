
package io.github.kanedafromparis.shyrka.projectchecker.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <p>Metadata class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "labels",
    "annotations"
})
public class Metadata {

    /**
     * The Name Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("name")
    private java.lang.String name = "";
    @JsonProperty("labels")
    private Map<String, String> labels;
    @JsonProperty("annotations")
    private Map<String, String> annotations;
   
    /**
     * The Name Schema
     * <p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    /**
     * The Name Schema
     * <p>
     *
     * @param name a {@link java.lang.String} object.
     */
    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>labels</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    @JsonProperty("labels")
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * <p>Setter for the field <code>labels</code>.</p>
     *
     * @param labels a {@link java.util.Map} object.
     */
    @JsonProperty("labels")
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * <p>Getter for the field <code>annotations</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    @JsonProperty("annotations")
    public Map<String, String> getAnnotations() {
        return annotations;
    }

    /**
     * <p>Setter for the field <code>annotations</code>.</p>
     *
     * @param annotations a {@link java.util.Map} object.
     */
    @JsonProperty("annotations")
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    /** {@inheritDoc} */
    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Metadata.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("labels");
        sb.append('=');
        sb.append(((this.labels == null)?"<null>":this.labels));
        sb.append(',');
        sb.append("annotations");
        sb.append('=');
        sb.append(((this.annotations == null)?"<null>":this.annotations));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.annotations == null)? 0 :this.annotations.hashCode()));
        result = ((result* 31)+((this.labels == null)? 0 :this.labels.hashCode()));
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Metadata) == false) {
            return false;
        }
        Metadata rhs = ((Metadata) other);
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.annotations == rhs.annotations)||((this.annotations!= null)&&this.annotations.equals(rhs.annotations)))))&&((this.labels == rhs.labels)||((this.labels!= null)&&this.labels.equals(rhs.labels))));
    }

}
