/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka.gcvp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.client.OpenShiftClient;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import io.vertx.core.json.JsonObject;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Ephyra class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public class Ephyra {

    private final Logger logger = LoggerFactory.getLogger(Ephyra.class);

    /**
     * <p>load.</p>
     *
     * @param confPath a {@link java.lang.String} object.
     * @return a {@link io.vertx.core.json.JsonObject} object.
     * @throws java.io.IOException if any.
     */
    public JsonObject load(String confPath) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()); // jackson databind
        JsonNode root = mapper.readValue(new File(confPath), JsonNode.class);
        return new JsonObject(root.toString());
    }

    /**
     * <p>terminateOldPodOpenshift.</p>
     *
     * @param conf a {@link io.vertx.core.json.JsonObject} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param namespace a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean terminateOldPodOpenshift(JsonObject conf, OpenShiftClient osClient, String namespace, String label) {
        Iterator<DeploymentConfig> iterator;
        if (StringUtils.isBlank(label)) {
            iterator = osClient.deploymentConfigs().inNamespace(namespace)
                    .list().getItems()
                    .iterator();
        } else {
            String key = StringUtils.split(label, "=")[0];
            String value = StringUtils.split(label, "=")[1];
            logger.warn("Filter on Label is working only with one label. we use '" + key + "=" + value+"'");
            iterator = osClient.deploymentConfigs().inNamespace(namespace)
                    .withLabel(key, value)
                    .list().getItems()
                    .iterator();

        }
        while (iterator.hasNext()) {
            DeploymentConfig dc = iterator.next();
            if (dc != null &&
                    dc.getMetadata() != null &&
                    dc.getMetadata().getName() != null &&
                    dc.getSpec() != null &&
                    dc.getSpec().getReplicas() != null &&
                    dc.getSpec().getReplicas() > 0) {
                logger.info("dc : " + dc.getMetadata().getName());
                Map<String, String> labels = new HashMap<>();
                labels.put("deploymentconfig", dc.getMetadata().getName());
                //This unfortunalty do not works with Mock
                Iterator<Pod> pods = osClient.pods().withLabels(labels).list().getItems().iterator();
                deletePods(conf, pods, osClient);
            }
        }

        return Boolean.TRUE;
    }

    /**
     * <p>terminateOldPod.</p>
     *
     * @param conf a {@link io.vertx.core.json.JsonObject} object.
     * @param osClient a {@link io.fabric8.kubernetes.client.KubernetesClient} object.
     * @param namespace a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean terminateOldPod(JsonObject conf, KubernetesClient osClient, String namespace, String label) {
        Iterator<Deployment> iterator;
        if (StringUtils.isBlank(label)) {
            iterator = osClient.extensions().deployments().inNamespace(namespace)
                    .list().getItems()
                    .iterator();
        } else {
            String key = StringUtils.split(label, "=")[0];
            String value = StringUtils.split(label, "=")[1];
            logger.warn("Filter on Label in working only with one label. we use " + key + "=" + value);
            iterator = osClient.extensions().deployments().inNamespace(namespace)
                    .withLabel(key, value).list().getItems()
                    .iterator();

        }

        while (iterator.hasNext()) {
            Deployment dc = iterator.next();
            if (dc.getSpec().getReplicas() > 1) {
                Map<String, String> labels = new HashMap<>();
                labels.put("deployment", dc.getMetadata().getName());
                Iterator<Pod> pods = osClient.pods().withLabels(labels).list().getItems().iterator();
                deletePods(conf, pods, osClient);
            }
        }

        return Boolean.TRUE;
    }

    private void deletePods(JsonObject conf, Iterator<Pod> pods, KubernetesClient osClient) {
        int maxDuration = conf.getInteger("maxDuration", 5);
        int nbPodCleaned = 0;
        if (logger.isInfoEnabled()) {
            logger.info("maxDuration : " + maxDuration);
        }

        while (pods.hasNext()) {
            Pod next = pods.next();
            if (StringUtils.equals(next.getStatus().getPhase(), "Running")) {
                if (logger.isInfoEnabled()) {
                    logger.info("Pod : " + next.getMetadata().getName() + " started at  " + next.getStatus().getStartTime());
                }
                if (nbPodCleaned++ > conf.getInteger("nbPodsToClean", 0)) {
                    logger.info("nbPodCleaned : " + nbPodCleaned + " nbPodsToClean : " + conf.getInteger("nbPodsToClean", 0));
                    return;
                }
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime newDate = ZonedDateTime.parse(next.getStatus().getStartTime());
                if (Duration.between(newDate, now).toMinutes() > maxDuration) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Duration : " + Duration.between(newDate, now).toMinutes() + " deleting pod");
                    }
                    Boolean delete = osClient.pods().delete(next);
                    if (logger.isInfoEnabled()) {
                        logger.info("Pod : " + next.getMetadata().getName() + " deleted :" + delete);
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("Duration : " + Duration.between(now, newDate).toDays() + " not deleting pod");
                    }
                }
            }

        }
    }

}
