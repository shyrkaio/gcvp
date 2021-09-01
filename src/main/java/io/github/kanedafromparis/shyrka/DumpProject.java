/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.client.OpenShiftClient;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>DumpProject class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
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
    private HasMetadata cleanForExport(HasMetadata next) {
        //next.getMetadata().remove("status");
        ObjectMeta metadata = next.getMetadata();
        metadata.setUid(StringUtils.EMPTY);
        metadata.setNamespace(StringUtils.EMPTY);
        metadata.setCreationTimestamp(StringUtils.EMPTY);
        metadata.setResourceVersion(StringUtils.EMPTY);
        metadata.setSelfLink(StringUtils.EMPTY);
        metadata.setUid(StringUtils.EMPTY);

        Map<String, String> annotations = metadata.getAnnotations();
        annotations.remove("deployment.kubernetes.io/revision");
        annotations.remove("kubectl.kubernetes.io/last-applied-configuration");

        return next;

    }

    /**
     * <p>dumpDeploy.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param NS a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean dumpDeploy(OpenShiftClient osClient, String NS, File dir, String outPutFormat) throws IOException {
        ListIterator<Deployment> lst = osClient.apps().deployments().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Deployment next = lst.next();
            next.setStatus(null);
            this.dumpJson(next, "deploy", next.getMetadata().getName(), dir, outPutFormat);
        }
        return Boolean.TRUE;
    }

    /**
     * <p>dumpConfigmap.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param NS a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean dumpConfigmap(OpenShiftClient osClient, String NS, File dir, String outPutFormat) throws IOException {
        ListIterator<ConfigMap> lst = osClient.configMaps().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();
        while (lst.hasNext()) {
            ConfigMap next = lst.next();
            this.dumpJson(next, "configmap", next.getMetadata().getName(), dir, outPutFormat);
        }
        return Boolean.TRUE;
    }

    /**
     * <p>dumpSecret.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param NS a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean dumpSecret(OpenShiftClient osClient, String NS, File dir, String outPutFormat) throws IOException {
        return this.dumpSecret(osClient, NS, dir, null);
    }

    /**
     * <p>dumpSecret.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param NS a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @param encode an array of char.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean dumpSecret(OpenShiftClient osClient, String NS, File dir, String outPutFormat, char[] encode) throws IOException {
        ListIterator<Secret> lst = osClient.secrets().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Secret next = lst.next();
            this.dumpJson(next, "secret", next.getMetadata().getName(), dir, outPutFormat);
        }
        return Boolean.TRUE;
    }

    /**
     * <p>dumpService.</p>
     *
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param NS a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean dumpService(OpenShiftClient osClient, String NS, File dir, String outPutFormat) throws IOException {
        ListIterator<Service> lst = osClient.services().inNamespace(NS).list().getItems().listIterator();
        ObjectMapper mapper = new ObjectMapper();

        while (lst.hasNext()) {
            Service next = lst.next();
            next.setStatus(null);
            this.dumpJson(next, "service", next.getMetadata().getName(), dir, outPutFormat);
        }
        return Boolean.TRUE;
    }

    /**
     * <p>dumpJson.</p>
     *
     * @param jso a {@link io.fabric8.kubernetes.api.model.HasMetadata} object.
     * @param type a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param dir a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public void dumpJson(HasMetadata jso, String type, String name, File dir, String outPutFormat) throws IOException {

        this.cleanForExport(jso);
        if (StringUtils.equalsIgnoreCase("yaml", outPutFormat)) {
            File f = new File(dir, name + "." + type + ".yaml");
            FileUtils.writeStringToFile(f, Serialization.asYaml(jso), "UTF-8");
        } else {
            File f = new File(dir, name + "." + type + ".json");
            FileUtils.writeStringToFile(f, Serialization.asJson(jso), "UTF-8");
        }
        logger.debug(type + " : {} ", jso.toString());

    }

}
