
package io.github.kanedafromparis.shyrka.projectchecker.model;

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

/**
 * <p>Query class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "query",
    "weight",
    "expectedResult",
    "result"
})
public class Query {

    @JsonProperty("query")
    private String query;
    @JsonProperty("weight")
    private Integer weight;
    @JsonProperty("expectedResult")
    private Boolean expectedResult;
    @JsonProperty("result")
    private Boolean result; 
    
    /**
     * <p>Getter for the field <code>query</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    /**
     * <p>Setter for the field <code>query</code>.</p>
     *
     * @param query a {@link java.lang.String} object.
     */
    @JsonProperty("query")
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * <p>Getter for the field <code>expectedResult</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    @JsonProperty("expectedResult")
    public Boolean getExpectedResult() {
        return expectedResult;
    }

    /**
     * <p>Setter for the field <code>expectedResult</code>.</p>
     *
     * @param expectedResult a {@link java.lang.Boolean} object.
     */
    @JsonProperty("Boolean expectedResult")
    public void setExpectedResult(Boolean expectedResult) {
        this.expectedResult = expectedResult;
    }
    /**
     * <p>Getter for the field <code>result</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    @JsonProperty("result")
    public Boolean getResult() {
        return result;
    }

    /**
     * <p>Setter for the field <code>result</code>.</p>
     *
     * @param result a {@link java.lang.Boolean} object.
     */
    @JsonProperty("result")
    public void setResult(Boolean result) {
        this.result = result;
    }
    

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Query.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("query");
        sb.append('=');
        sb.append(((this.query == null)?"<null>":this.query));
        sb.append(',');
        sb.append("weight");
        sb.append('=');
        sb.append(((this.weight == 0)?"<null>":this.weight));
        sb.append(',');
        sb.append("expectedResult");
        sb.append('=');
        sb.append(((this.expectedResult == null)?"<null>":this.expectedResult));
        sb.append(',');
        sb.append("result");
        sb.append('=');
        sb.append(((this.result == null)?"<null>":this.result));
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
        result = ((result* 31)+((this.query == null)? 0 :this.query.hashCode()));
        result = ((result* 31)+((this.weight == null)? 0 :this.weight.hashCode()));
        result = ((result* 31)+((this.expectedResult == null)? 0 :this.expectedResult.hashCode()));
        result = ((result* 31)+((this.result == null)? 0 :this.result.hashCode()));
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Query) == false) {
            return false;
        }
        Query rhs = ((Query) other);
        return (((((((this.query == rhs.query)||((this.query!= null)&&this.query.equals(rhs.query)))&&((this.weight == rhs.weight)||((this.weight!= null)&&this.weight.equals(rhs.weight))))&&((this.expectedResult == rhs.expectedResult)||((this.expectedResult!= null)&&this.expectedResult.equals(rhs.expectedResult))))&&((this.result == rhs.result)||((this.result!= null)&&this.result.equals(rhs.result))))));
    }

}
