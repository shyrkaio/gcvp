/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.openshift.client.OpenShiftClient;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author csabourdin
 */
public class DumpProject {

    private final Logger logger = LoggerFactory.getLogger(DumpProject.class);

    /**
     * // properties to clean
     * //kubectl.kubernetes.io/last-applied-configuration //namespace
     * //resourceVersion //selfLink //uid //ownerReferences
     *
     * @param next
     * @return
     */
    private JSONObject cleanForExport(JSONObject next) {
        next.remove("status");
        JSONObject metadata = next.getJSONObject("metadata");
        metadata.remove("uid");
        metadata.remove("namespace");
        metadata.remove("creationTimestamp");
        metadata.remove("resourceVersion");
        metadata.remove("selfLink");
        metadata.remove("uid");
        
        JSONObject annotations = metadata.getJSONObject("annotations");
        annotations.remove("deployment.kubernetes.io/revision");
        annotations.remove("kubectl.kubernetes.io/last-applied-configuration");

        return next;

    }

    public Boolean dumpDeploy(OpenShiftClient osClient, String NS, File dir) throws JSONException, IOException {
        ListIterator<Deployment> lst = osClient.extensions().deployments().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Deployment next = lst.next();
            JSONObject jso = new JSONObject(mapper.writeValueAsString(next));
            dumpJson(jso, "deploy", next.getMetadata().getName(), dir);
        }
        return Boolean.TRUE;
    }

    public Boolean dumpConfigmap(OpenShiftClient osClient, String NS, File dir) throws JSONException, IOException {
        ListIterator<ConfigMap> lst = osClient.configMaps().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            ConfigMap next = lst.next();
            JSONObject jso = new JSONObject(mapper.writeValueAsString(next));
            dumpJson(jso, "configmap", next.getMetadata().getName(), dir);
        }
        return Boolean.TRUE;
    }

    public Boolean dumpSecret(OpenShiftClient osClient, String NS, File dir) throws JSONException, IOException {
        return this.dumpSecret(osClient, NS, dir, null);
    }

    public Boolean dumpSecret(OpenShiftClient osClient, String NS, File dir, char[] encode) throws JSONException, IOException {
        ListIterator<Secret> lst = osClient.secrets().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Secret next = lst.next();
            JSONObject jso = new JSONObject(mapper.writeValueAsString(next));
            dumpJson(jso, "secret", next.getMetadata().getName(), dir);
        }
        return Boolean.TRUE;
    }

    public Boolean dumpService(OpenShiftClient osClient, String NS, File dir) throws JSONException, IOException {
        ListIterator<Service> lst = osClient.services().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Service next = lst.next();
            JSONObject jso = new JSONObject(mapper.writeValueAsString(next));
            dumpJson(jso, "service", next.getMetadata().getName(), dir);
        }
        return Boolean.TRUE;
    }

    public void dumpJson(JSONObject jso, String type, String name, File dir) throws JSONException, IOException {

        this.cleanForExport(jso);
        File f = new File(dir, name + "." + type + ".json");
        FileUtils.writeStringToFile(f, jso.toString(), "UTF-8");
        logger.debug(type + " : {} ", jso.toString());

    }
}
