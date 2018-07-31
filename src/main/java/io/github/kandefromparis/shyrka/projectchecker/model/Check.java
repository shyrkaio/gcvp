
package io.github.kandefromparis.shyrka.projectchecker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "namespace",
    "name",
    "comment",
    "ressourceType",
    "labels",
    "weight",
    "queries"
})
public class Check {

    /**
     * The Namespace Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("namespace")
    private java.lang.String namespace = "";
    /**
     * The Name Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("name")
    private java.lang.String name = "";
    /**
     * The Comment Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("comment")
    private java.lang.String comment = "";
    /**
     * The Ressourcetype Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("ressourceType")
    private java.lang.String ressourceType = "";
    @JsonProperty("labels")
    private Map<String, String> labels;
    /**
     * The Weight Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("weight")
    private Integer weight = 0;
    @JsonProperty("queries")
    private List<Query> queries = new ArrayList<Query>();
    @JsonIgnore
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    /**
     * The Namespace Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("namespace")
    public java.lang.String getNamespace() {
        return namespace;
    }

    /**
     * The Namespace Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("namespace")
    public void setNamespace(java.lang.String namespace) {
        this.namespace = namespace;
    }

    /**
     * The Name Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    /**
     * The Name Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * The Comment Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("comment")
    public java.lang.String getComment() {
        return comment;
    }

    /**
     * The Comment Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("comment")
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }

    /**
     * The Ressourcetype Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("ressourceType")
    public java.lang.String getRessourceType() {
        return ressourceType;
    }

    /**
     * The Ressourcetype Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("ressourceType")
    public void setRessourceType(java.lang.String ressourceType) {
        this.ressourceType = ressourceType;
    }

    @JsonProperty("labels")
    public Map<String, String> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The Weight Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("weight")
    public Integer getWeight() {
        return weight;
    }

    /**
     * The Weight Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("weight")
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @JsonProperty("queries")
    public List<Query> getQueries() {
        return queries;
    }

    @JsonProperty("queries")
    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Check.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("namespace");
        sb.append('=');
        sb.append(((this.namespace == null)?"<null>":this.namespace));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("comment");
        sb.append('=');
        sb.append(((this.comment == null)?"<null>":this.comment));
        sb.append(',');
        sb.append("ressourceType");
        sb.append('=');
        sb.append(((this.ressourceType == null)?"<null>":this.ressourceType));
        sb.append(',');
        sb.append("labels");
        sb.append('=');
        sb.append(((this.labels == null)?"<null>":this.labels));
        sb.append(',');
        sb.append("weight");
        sb.append('=');
        sb.append(((this.weight == null)?"<null>":this.weight));
        sb.append(',');
        sb.append("queries");
        sb.append('=');
        sb.append(((this.queries == null)?"<null>":this.queries));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.namespace == null)? 0 :this.namespace.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.ressourceType == null)? 0 :this.ressourceType.hashCode()));
        result = ((result* 31)+((this.weight == null)? 0 :this.weight.hashCode()));
        result = ((result* 31)+((this.comment == null)? 0 :this.comment.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.queries == null)? 0 :this.queries.hashCode()));
        result = ((result* 31)+((this.labels == null)? 0 :this.labels.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Check) == false) {
            return false;
        }
        Check rhs = ((Check) other);
        return (((((((((this.namespace == rhs.namespace)||((this.namespace!= null)&&this.namespace.equals(rhs.namespace)))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.ressourceType == rhs.ressourceType)||((this.ressourceType!= null)&&this.ressourceType.equals(rhs.ressourceType))))&&((this.weight == rhs.weight)||((this.weight!= null)&&this.weight.equals(rhs.weight))))&&((this.comment == rhs.comment)||((this.comment!= null)&&this.comment.equals(rhs.comment))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.queries == rhs.queries)||((this.queries!= null)&&this.queries.equals(rhs.queries))))&&((this.labels == rhs.labels)||((this.labels!= null)&&this.labels.equals(rhs.labels))));
    }

}
