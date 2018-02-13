/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.github.kandefromparis.shyrka.ConformityIssue;
import static io.github.kandefromparis.shyrka.ConformityIssue.*;
import static io.github.kandefromparis.shyrka.ConformityIssue.NO_PROJECT_OWNER_CONFIRMATION;
import static io.github.kandefromparis.shyrka.ConformityIssue.NO_PROJECT_OWNER_LABEL;
import static io.github.kandefromparis.shyrka.ConformityIssue.NO_SHYRKA_CONFIGMAP;
import static io.github.kandefromparis.shyrka.ShyrkaLabel.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @todo check this #$#$#% logging
 * @author csabourdin
 */
public class GCVP {

    private final Logger logger = LoggerFactory.getLogger(GCVP.class);
    OpenShiftClient osClient;

    final String SHYRKA_CONFIGMAP_NAME = "shyrka";

    GCVP(Config config) {
        this.osClient = new DefaultOpenShiftClient(config);

    }

    public static void main(String[] args) throws InterruptedException {
        Logger startuplogger = LoggerFactory.getLogger(GCVP.class);
        startuplogger.info("info");
        startuplogger.warn("warn");
        startuplogger.error("error");

        Iterator<String> keys_ = System.getenv().keySet().iterator();
        while (keys_.hasNext()) {
            Object key = keys_.next();
            Object value = System.getenv().get(key.toString());
            startuplogger.debug(String.valueOf(key) + " " + String.valueOf(value));
        }
        Config config = new ConfigBuilder().build();
        GCVP robot = new GCVP(config);
        robot.scaleDown(config.getNamespace());
    }

    public void scaleDown(String NS) {

        if (StringUtils.isBlank(NS)) {
            NS = this.osClient.getNamespace();
        }
        this.scaleDownKube(NS);        
        this.scaleDownOcp(NS);
        

    }

    public void scaleDownKube(String NS) {

        Iterator<Deployment> iterator = this.osClient.extensions().deployments().inNamespace(NS).list().getItems().iterator();
        while (iterator.hasNext()) {
            Deployment next = iterator.next();
            if (scaleDownDeploy(next)) {
                continue;
            }
        }

    }

    public void scaleDownOcp(String NS) {

        Iterator<DeploymentConfig> iterator = this.osClient.deploymentConfigs().inNamespace(NS).list().getItems().iterator();
        while (iterator.hasNext()) {
            DeploymentConfig next = iterator.next();
            if (scaleDownDC(next)) {
                continue;
            }
        }

    }

    private boolean scaleDownDC(DeploymentConfig next) {
        if (next.getSpec().getReplicas() == 0) {
            logger.debug("no modification (already scaledown) {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }
        if (next.getMetadata()
                .getLabels()
                .containsKey(L_PROJECT_STAGE.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_PROJECT_STAGE.getlabel())
                        .equalsIgnoreCase("prod")) {
            logger.debug("no modification (DC has " + L_PROJECT_STAGE.getlabel() + "=prod) of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }
        if (next.getMetadata()
                .getLabels()
                .containsKey(L_SCALEDOWN.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_SCALEDOWN.getlabel())
                        .equalsIgnoreCase("false")) {
            logger.debug("no modification (DC has " + L_SCALEDOWN.getlabel() + "=false) {} of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }

        if (next.getMetadata()
                .getLabels()
                .containsKey(L_SCALEDOWN.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_SCALEDOWN.getlabel())
                        .equalsIgnoreCase("true")) {
            try {

                if (next.getMetadata()
                        .getLabels()
                        .containsKey(L_END_DATE.getlabel())) {
                    Date now = Calendar.getInstance().getTime();
                    Date limit = DateFormatUtils.ISO_DATE_FORMAT
                            .parse(next.getMetadata()
                                    .getLabels()
                                    .get(L_END_DATE.getlabel()));
                    if (now.after(limit)) {
                        logger.info("scale down (current date after " + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());

                        next.getSpec().setReplicas(0);
                        this.osClient.deploymentConfigs().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);
                        return true;
                    } else {
                        logger.debug("no modification (current ["+DateFormatUtils.ISO_DATE_FORMAT.format(now)+"+]date before " + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
                        return true;
                    }
                }
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
                logger.warn("no modification (Label value has wrong format" + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
                return true;
            }
        } else {
            logger.info("scale down (no value for " + L_END_DATE.getlabel() + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            next.getSpec().setReplicas(0);
            this.osClient.deploymentConfigs().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);
            return true;
        }

        return false;
    }

    /**
     * @todo have only one method
     * @param next
     * @return
     */
    private boolean scaleDownDeploy(Deployment next) {
        if (next.getSpec().getReplicas() == 0) {
            logger.debug("no modification (already scaledown) {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }
        if (next.getMetadata()
                .getLabels()
                .containsKey(L_PROJECT_STAGE.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_PROJECT_STAGE.getlabel())
                        .equalsIgnoreCase("prod")) {
            logger.debug("no modification (DC has " + L_PROJECT_STAGE.getlabel() + "=prod) of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }
        if (next.getMetadata()
                .getLabels()
                .containsKey(L_SCALEDOWN.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_SCALEDOWN.getlabel())
                        .equalsIgnoreCase("false")) {
            logger.debug("no modification (DC has " + L_SCALEDOWN.getlabel() + "=false) {} of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            return true;
        }

        if (next.getMetadata()
                .getLabels()
                .containsKey(L_SCALEDOWN.getlabel())
                && next.getMetadata()
                        .getLabels()
                        .get(L_SCALEDOWN.getlabel())
                        .equalsIgnoreCase("true")) {
            try {

                if (next.getMetadata()
                        .getLabels()
                        .containsKey(L_END_DATE.getlabel())) {
                    Date now = Calendar.getInstance().getTime();
                    Date limit = DateFormatUtils.ISO_DATE_FORMAT
                            .parse(next.getMetadata()
                                    .getLabels()
                                    .get(L_END_DATE.getlabel()));
                    if (now.after(limit)) {
                        logger.info("scale down (current date after " + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());

                        next.getSpec().setReplicas(0);
                        this.osClient.extensions().deployments().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);
                        return true;
                    } else {
                        logger.debug("no modification (current date before " + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
                        return true;
                    }
                }
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
                logger.warn("no modification (Label value has wrong format" + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
                return true;
            }
        } else {
            logger.info("scale down (no value for " + L_END_DATE.getlabel() + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
            next.getSpec().setReplicas(0);
            this.osClient.extensions().deployments().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);

            return true;
        }

        return false;

    }

    /**
     * this method check that the configmap has correct values
     *
     * @return
     */
    public List<ConformityIssue> conformityCheck(String NS) {

        List<ConformityIssue> issues = new ArrayList<>();
        ConfigMap conf = this.osClient.configMaps().inNamespace(NS).withName(SHYRKA_CONFIGMAP_NAME).get();
        if (conf == null) {
            issues.add(NO_SHYRKA_CONFIGMAP);
            logger.warn("conformity issue {} ", NO_SHYRKA_CONFIGMAP.toString());
            return issues;
        }
        if (!conf.getMetadata().getLabels().containsKey(L_PROJECT_NAME.getlabel())
                || conf.getMetadata().getLabels().get(L_PROJECT_NAME.getlabel()) == null
                || conf.getMetadata().getLabels().get(L_PROJECT_NAME.getlabel()).isEmpty()) {
            logger.info("conformity issue {} ", L_PROJECT_NAME.toString());
            issues.add(NO_PROJECT_NAME_LABEL);
        }
        if (!conf.getMetadata().getLabels().containsKey(L_PRODUCT_OWNER.getlabel())
                || conf.getMetadata().getLabels().get(L_PRODUCT_OWNER.getlabel()) == null
                || conf.getMetadata().getLabels().get(L_PRODUCT_OWNER.getlabel()).isEmpty()) {
            logger.info("conformity issue {} ", NO_PROJECT_OWNER_LABEL.toString());
            issues.add(NO_PROJECT_OWNER_LABEL);
        }
        if (!conf.getMetadata().getAnnotations().containsKey(A_PRODUCT_OWNER.getlabel())
                || conf.getMetadata().getAnnotations().get(A_PRODUCT_OWNER.getlabel()) == null
                || conf.getMetadata().getAnnotations().get(A_PRODUCT_OWNER.getlabel()).isEmpty()) {
            logger.info("conformity issue {} ", NO_PROJECT_OWNER_ANNOTATION.toString());
            issues.add(NO_PROJECT_OWNER_ANNOTATION);
        }
        if (!conf.getMetadata().getLabels().containsKey(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel())
                || conf.getMetadata().getLabels().get(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel()) == null
                || conf.getMetadata().getLabels().get(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel()).isEmpty()) {
            logger.info("conformity issue {} ", NO_PROJECT_OWNER_CONFIRMATION.toString());
            issues.add(NO_PROJECT_OWNER_CONFIRMATION);
        }
        if (conf.getMetadata().getLabels().containsKey(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel())) {
            Date lastValidation;
            Date now=Calendar.getInstance().getTime();
            try {
                lastValidation = DateFormatUtils.ISO_DATE_FORMAT.parse(conf.getMetadata().getLabels().get(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel()));
                long difMillis = now.getTime()-lastValidation.getTime();
                long difDay = difMillis / (60*60*1000*24);
                
                if (difDay > 120) {
                    logger.info("conformity issue {} for more then 120 days", PROJECT_CONFIRMATION_EXPIRED.toString());
                    issues.add(PROJECT_CONFIRMATION_EXPIRED);
                } else if (difDay > 60) {
                    logger.info("conformity issue {} for more then 60 days", PROJECT_CONFIRMATION_EXPIRED.toString());
                    issues.add(PROJECT_CONFIRMATION_EXPIRED);
                } else if (difDay > 30) {
                    logger.info("conformity issue {} for more then 30 days", PROJECT_CONFIRMATION_EXPIRED.toString());
                    issues.add(PROJECT_CONFIRMATION_EXPIRED);
                } else if (difDay > 15) {
                    logger.info("conformity issue {} for more then 15 days", PROJECT_CONFIRMATION_EXPIRED.toString());
                    //issues.add(PROJECT_CONFIRMATION_EXPIRED);
                }
            } catch (ParseException ex) {
                logger.info("conformity issue {} for more then 15 days", PROJECT_CONFIRMATION_EXPIRED.toString());
                issues.add(PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT_WRONG_FORMAT);
                java.util.logging.Logger.getLogger(GCVP.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (conf.getMetadata().getLabels().containsKey(L_BACKUP.getlabel())) {
            //@todo
            //PROJECT_BACKUP_NOT_SET        
            //PROJECT_LAST_BACKUP_TOO_OLD

        }

        return issues;

    }

    private void defaultCall() {

        //ConfigMap configMap = this.getShyrkaConfigMap();
        //LabelSelector selector = new LabelSelector();
        //FilterWatchListDeletable<ConfigMap, ConfigMapList, Boolean, Watch, Watcher<ConfigMap>> withLabelSelector = configMap.withLabelSelector(selector);
        //logger.info("Upserted ConfigMap at " + configMap.getMetadata().getSelfLink() + " data " + configMap.getData());
    }

}
