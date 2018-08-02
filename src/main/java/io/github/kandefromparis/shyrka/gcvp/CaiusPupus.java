/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigList;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigList;
import io.fabric8.openshift.client.OpenShiftClient;
import io.github.kandefromparis.shyrka.ShyrkaLabel;
import io.github.kandefromparis.shyrka.projectchecker.model.Check;
import io.github.kandefromparis.shyrka.projectchecker.model.Checker;
import io.github.kandefromparis.shyrka.projectchecker.model.Query;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * https://github.com/json-path/JsonPath
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public class CaiusPupus {

    private final Logger logger = LoggerFactory.getLogger(CaiusPupus.class);
    private JsonObject conf = new JsonObject();
    private JsonObject fullRepport = new JsonObject();

    /**
     * <p>validateQuery.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param nameSpace a {@link java.lang.String} object.
     * @param ressourcetype a {@link java.lang.String} object.
     * @param labels a {@link java.util.Map} object.
     * @param query an array of {@link java.lang.String} objects.
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean validateQuery(OpenShiftClient osClient, String nameSpace, String ressourcetype, Map<String, String> labels, String[] query) {
        switch (ressourcetype) {
            case "all":
                logger.warn("ressource not handle");
                return Boolean.FALSE;

            case "configmap":
                ConfigMapList listCM = osClient.configMaps().inNamespace(nameSpace).list();
                Iterator<ConfigMap> iteratorCM = listCM.getItems().iterator();
                while (iteratorCM.hasNext()) {
                    ConfigMap next = iteratorCM.next();
                    Map<String, String> objlabels = next.getMetadata().getLabels();
                    if (CollectionUtils.containsAll(objlabels.keySet(), labels.keySet())
                            && CollectionUtils.containsAll(objlabels.values(), labels.values())) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonObject jso = new JsonObject(mapper.writeValueAsString(next));
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                            ReadContext ctx = JsonPath.parse(jso.toString());
                            for (String query1 : query) {
                                List<Map<String, Object>> spec = ctx.read(query1);
                                if ((spec.size() >= 1) == Boolean.FALSE) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;
                        } catch (JsonProcessingException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        } catch (PathNotFoundException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.FALSE;

            case "deploy":
                DeploymentList listD = osClient.apps().deployments().inNamespace(nameSpace).list();
                Iterator<Deployment> iterator = listD.getItems().iterator();
                while (iterator.hasNext()) {
                    Deployment next = iterator.next();
                    Map<String, String> objlabels = next.getMetadata().getLabels();
                    if (CollectionUtils.containsAll(objlabels.keySet(), labels.keySet())
                            && CollectionUtils.containsAll(objlabels.values(), labels.values())) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonObject jso = new JsonObject(mapper.writeValueAsString(next));
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                            ReadContext ctx = JsonPath.parse(jso.toString());

                            for (String query1 : query) {
                                List<Map<String, Object>> spec = ctx.read(query1);
                                if ((spec.size() >= 1) == Boolean.FALSE) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;

                        } catch (JsonProcessingException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        } catch (PathNotFoundException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.FALSE;

            case "secret":
                SecretList listS = osClient.secrets().inNamespace(nameSpace).list();
                Iterator<Secret> iteratorS = listS.getItems().iterator();
                while (iteratorS.hasNext()) {
                    Secret next = iteratorS.next();
                    Map<String, String> objlabels = next.getMetadata().getLabels();
                    if (CollectionUtils.containsAll(objlabels.keySet(), labels.keySet())
                            && CollectionUtils.containsAll(objlabels.values(), labels.values())) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonObject jso = new JsonObject(mapper.writeValueAsString(next));
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                            ReadContext ctx = JsonPath.parse(jso.toString());
                            for (String query1 : query) {
                                List<Map<String, Object>> spec = ctx.read(query1);
                                if ((spec.size() >= 1) == Boolean.FALSE) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;
                        } catch (JsonProcessingException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        } catch (PathNotFoundException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.FALSE;

            case "svc":
                logger.debug("ressource : svc not implemented yet");
                return Boolean.FALSE;

            case "endpoints":
                logger.debug("ressource : endpoints not implemented yet");
                return Boolean.FALSE;

            case "hpa":
                logger.debug("ressource : hpa not implemented yet");
                return Boolean.FALSE;

            //openshift
            case "bc":
                BuildConfigList listBC = osClient.buildConfigs().inNamespace(nameSpace).list();
                Iterator<BuildConfig> iteratorBC = listBC.getItems().iterator();
                while (iteratorBC.hasNext()) {
                    BuildConfig next = iteratorBC.next();
                    Map<String, String> objlabels = next.getMetadata().getLabels();
                    if (CollectionUtils.containsAll(objlabels.keySet(), labels.keySet())
                            && CollectionUtils.containsAll(objlabels.values(), labels.values())) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonObject jso = new JsonObject(mapper.writeValueAsString(next));
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                            ReadContext ctx = JsonPath.parse(jso.toString());
                            for (String query1 : query) {
                                List<Map<String, Object>> spec = ctx.read(query1);
                                if ((spec.size() >= 1) == Boolean.FALSE) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;
                        } catch (JsonProcessingException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        } catch (PathNotFoundException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.FALSE;

            case "dc":
                DeploymentConfigList listDC = osClient.deploymentConfigs().inNamespace(nameSpace).list();
                Iterator<DeploymentConfig> iteratorDC = listDC.getItems().iterator();
                while (iteratorDC.hasNext()) {
                    DeploymentConfig next = iteratorDC.next();
                    Map<String, String> objlabels = next.getMetadata().getLabels();
                    if (CollectionUtils.containsAll(objlabels.keySet(), labels.keySet())
                            && CollectionUtils.containsAll(objlabels.values(), labels.values())) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonObject jso = new JsonObject(mapper.writeValueAsString(next));
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                            ReadContext ctx = JsonPath.parse(jso.toString());
                            for (String query1 : query) {
                                List<Map<String, Object>> spec = ctx.read(query1);
                                if ((spec.size() >= 1) == Boolean.FALSE) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;
                        } catch (JsonProcessingException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        } catch (PathNotFoundException ex) {
                            java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.FALSE;
            case "routes":
                logger.debug("ressource : routes not implemented yet");
                return Boolean.FALSE;

            default:
                logger.warn("ressource not handle");
                return Boolean.FALSE;

        }

        //return Boolean.FALSE;
    }

    /**
     * This Method will load the CauisPupus configuration file
     *
     * @param confPath a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     * @return a {@link io.github.kandefromparis.shyrka.projectchecker.model.Checker} object.
     */
    public Checker load(String confPath) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()); // jackson databind
        Checker obj = mapper.readValue(new File(confPath), Checker.class);
        return obj;
    }

    /**
     * <p>audit.</p>
     *
     * @param conf a {@link io.github.kandefromparis.shyrka.projectchecker.model.Checker} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @return a {@link io.vertx.core.json.JsonObject} object.
     */
    public JsonObject audit(Checker conf, OpenShiftClient osClient) {
        return auditForLabel(conf, osClient, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    /**
     * <p>audit.</p>
     *
     * @param conf a {@link io.github.kandefromparis.shyrka.projectchecker.model.Checker} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param namespace a {@link java.lang.String} object.
     * @return a {@link io.vertx.core.json.JsonObject} object.
     */
    public JsonObject audit(Checker conf, OpenShiftClient osClient, String namespace) {
        return auditForLabel(conf, osClient, namespace, StringUtils.EMPTY);
    }

    /**
     * <p>auditForLabel.</p>
     *
     * @param conf a {@link io.github.kandefromparis.shyrka.projectchecker.model.Checker} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param namespace a {@link java.lang.String} object.
     * @param caiusLabel a {@link java.lang.String} object.
     * @return a {@link io.vertx.core.json.JsonObject} object.
     */
    public JsonObject auditForLabel(Checker conf, OpenShiftClient osClient, String namespace, String caiusLabel) {
        List<Check> checkArray = conf.getSpec().getChecks();

        for (Check check : checkArray) {

            if (StringUtils.isNotEmpty(namespace)) {
                if (!StringUtils.equals(namespace, check.getNamespace())) {
                    continue;
                }
            }
            //@Todo have better label handling
            if (StringUtils.isNotEmpty(caiusLabel)) {
                String[] labelsSplit = StringUtils.splitPreserveAllTokens(caiusLabel, "=");

                if (!StringUtils.equals(check.getLabels().get(labelsSplit[0]), labelsSplit[1])) {
                    continue;
                }
            }
            //@todo Serialisation ?
//             - namespace: test
//               name: envVarValidation
//               comment : Check that containers has an environement variable NAME0 set to VALUE0
//               ressourceType: deploy
//               labels:
//                 io.shyrka.erebus/pjt-stage: test
//               validate:
//                 weight: 10
//                 query:
//                   - "$.spec.template.spec.containers[*].env[*].[?((@.name == 'NAME0') && (@.value == 'VALUE0'))]"
//                 expectedResult: "true"
            String nameSpace = check.getNamespace();
            String name = check.getName();
            String ressourceType = check.getRessourceType();
            Map<String, String> labels = check.getLabels();

            List<Query> queries = check.getQueries();
            List<String> sQueries = new ArrayList<>(queries.size());

            for (Query query : queries) {
                sQueries.add(query.getQuery());
            }
            JsonObject result = new JsonObject();
            result.put("name", name);
            JsonObject report = new JsonObject();
            if (fullRepport.containsKey(nameSpace)) {
                report = fullRepport.getJsonObject(nameSpace);
            } else {
                report.put("nbOk", 0);
                report.put("nbChecks", 0);
            }
            report.put("project", nameSpace);
            report.put("labels", labels);
            String stackName = labels.get(ShyrkaLabel.L_STACK_NAME.getlabel());
            if (StringUtils.isEmpty(stackName)) {
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, "" + ShyrkaLabel.L_STACK_NAME + " is missing");
                stackName = "no-stack";
            }
            JsonObject put = report.put("stack", stackName);
            Boolean res = this.validateQuery(osClient, nameSpace, ressourceType, labels, sQueries.toArray(new String[sQueries.size()]));
            if (res) {
                int aInt = report.getInteger("nbOk") + 1;
                report.put("nbOk", aInt);
            }
            int aInt = report.getInteger("nbChecks") + 1;
            report.put("nbChecks", aInt);
            JsonArray rules = new JsonArray();
            if (report.containsKey("rules")) {
                rules = report.getJsonArray("rules");
            }
            result.put("status", name);
            rules.add(result);
// not sure this is usefull ;-)
            fullRepport.put(nameSpace, report);
        }
        return fullRepport;
    }

    /**
     * <p>jsonResult.</p>
     *
     * @return a {@link io.vertx.core.json.JsonObject} object.
     */
    public JsonObject
            jsonResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
