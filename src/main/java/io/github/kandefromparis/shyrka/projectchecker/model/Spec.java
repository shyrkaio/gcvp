
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
    "checks"
})
public class Spec {

    @JsonProperty("checks")
    private List<Check> checks = new ArrayList<Check>();
    
    @JsonProperty("checks")
    public List<Check> getChecks() {
        return checks;
    }

    @JsonProperty("checks")
    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Spec.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("checks");
        sb.append('=');
        sb.append(((this.checks == null)?"<null>":this.checks));
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
        result = ((result* 31)+((this.checks == null)? 0 :this.checks.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Spec) == false) {
            return false;
        }
        Spec rhs = ((Spec) other);
        return (((this.checks == rhs.checks)||((this.checks!= null)&&this.checks.equals(rhs.checks))));
    }

}
