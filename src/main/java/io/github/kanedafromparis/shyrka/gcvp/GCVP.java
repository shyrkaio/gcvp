/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka.gcvp;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.github.kanedafromparis.shyrka.ConformityIssue;
import static io.github.kanedafromparis.shyrka.ConformityIssue.*;
import static io.github.kanedafromparis.shyrka.ConformityIssue.NO_PROJECT_OWNER_CONFIRMATION;
import static io.github.kanedafromparis.shyrka.ConformityIssue.NO_PROJECT_OWNER_LABEL;
import static io.github.kanedafromparis.shyrka.ConformityIssue.NO_SHYRKA_CONFIGMAP;
import io.github.kanedafromparis.shyrka.DumpProject;
import static io.github.kanedafromparis.shyrka.ShyrkaLabel.*;
import io.github.kanedafromparis.shyrka.projectchecker.model.Checker;
import io.github.kanedafromparis.shyrka.gcvp.Ephyra;
import io.github.kanedafromparis.shyrka.gcvp.CaiusPupus;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * g
 *
 * @author csabourdin
 * @version $Id: $Id
 */
//@TODO check this #$#$#% loggin
public class GCVP {

    private final Logger logger = LoggerFactory.getLogger(GCVP.class);
    OpenShiftClient osClient;

    final String SHYRKA_CONFIGMAP_NAME = "shyrka";

    GCVP(Config config) {
        this.osClient = new DefaultOpenShiftClient(config);

    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.InterruptedException if any.
     */
    public static void main(String[] args) throws InterruptedException {
        Logger log = LoggerFactory.getLogger(GCVP.class);
        // create Options object
        Options options = new Options();
        options.addOption("s", "scaleDown", false, "This commande will scale down Deployment and DeploymentConfig according to label policy");
        options.addOption("k", "iskubernetes", false, "This commande will scale down Deployment and DeploymentConfig according to label policy");
        options.addOption("o", "isOpenshift", false, "This commande will scale down Deployment and DeploymentConfig according to label policy");
        options.addOption("S", "scaleUP", false, "This commande will scale up Deployment and DeploymentConfig according to label policy [not implemented yet]");
        options.addOption("c", "check", false, "This commande will display conformity issue");
        options.addOption("h", "help", false, "This commande will display this message");
        options.addOption("t", "trigger", true, "This commande use option for trigger, email, webhoock, events [not implemented yet]");
        options.addOption("l", "logfile", true, "This commande allow to overwrite default logfile]");

        //@todo put that into another file/project?
        options.addOption("d", "dump", true, "This commande dump the targeted ressources dc, deploy, route, svc, ");
        options.addOption("output", true, "Output format. One of json|yaml");

        options.addOption("dir", true, "This commande define the directory to dump the files");
        options.addOption("git", true, "This commande define the git parameter");
        options.addOption("gitoken", true, "This commande define the git parameter");

        options.addOption("caius", true, "This commande will execute the caius pupus project checker");
        options.addOption("caiusLabels", true, "This commande will execute the caius pupus project checker only on this label");

        options.addOption("caiusConfPath", true, "This commande allow to overwrite default configurationfile");
        options.addOption("caiusNS", true, "This commande allow to overwrite default all Namespace target");

        options.addOption("ephyra", false, "This commande will execute the ephyra pod killer");
        options.addOption("ephyraConfPath", true, "This commande allow to overwrite default configurationfile");
        options.addOption("ephyraNS", true, "This commande allow to overwrite default all Namespace target");
        options.addOption("ephyraLabels", true, "This commande will execute the ephyra only on this label");

        options.addOption("key", true, "This options define the key to be use to encrypt the base64 fields [data, .dockercfg] of secrets");

        CommandLineParser parser = new DefaultParser();
        try {
            Config config = new ConfigBuilder().build();
            //config.setOauthToken(oauthToken);
            GCVP robot = new GCVP(config);

            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java $JVM_OPTIONS \\\n"
                        + "                                        -cp .:lib \\\n"
                        + "                                        -Djava.util.logging.config.file=./conf/logging.properties \\\n"
                        + "                                        -jar /opt/${project.artifactId}/${project.artifactId}-${project.version}.jar", options);
                return;
            }

            //validation
            if (line.hasOption("s") && line.hasOption("S")) {
                System.out.println(" You can not both s and S, choose one");
                return;
            }
            if (line.hasOption("c") && line.hasOption("s")) {
                System.out.println(" You can not both check and scaledown, choose one");
                return;
            }
            if (line.hasOption("c") && line.hasOption("S")) {
                System.out.println(" You can not both check and scaleup, choose one");
                return;
            }
            if (line.hasOption("S")) {
                System.out.println(" Feature not implemented yet");
                return;
            }
            if (line.hasOption("key")) {
                System.out.println(" Feature not implemented yet");
                return;
            }

            if (line.hasOption("d")) {
                String dir = line.getOptionValue("dir", System.getProperty("user.dir") + "/yaml-backup");
                File f = new File(dir);
                f.mkdirs();
                if (!f.exists()) {
                    System.out.println(" " + f.getCanonicalPath() + " does not existe");
                    return;
                }
                if (!f.isDirectory()) {
                    System.out.println(" " + f.getCanonicalPath() + " is not a directory");
                    return;
                }
                String optionValue = line.getOptionValue("d");
                String outPutFormat = line.getOptionValue("output", "json");
                dumpswitch(optionValue, robot.osClient, config.getNamespace(), f, outPutFormat);

                return;

            }
            if (line.hasOption("s")) {
                if (line.hasOption("k")) {
                    robot.scaleDownKube(config.getNamespace());
                } else if (line.hasOption("o")) {
                    robot.scaleDownOcp(config.getNamespace());
                } else {
                    System.out.println(" You can need to select either o (openshift) or k (kubernetes)");
                    return;
                }
            }

            if (line.hasOption("c")) {
                List<ConformityIssue> conformityCheck = robot.conformityCheck(config.getNamespace());
                Iterator<ConformityIssue> iter = conformityCheck.iterator();
                while (iter.hasNext()) {
                    ConformityIssue next = iter.next();
                    System.out.println(next.getErrorCode() + " : " + next.getErrorMessageCode() + " - " + next.getErrorMessage());

                }
                return;
            }

            if (line.hasOption("caius")) {

                String confPath = line.getOptionValue("caiusConfPath", System.getProperty("user.dir") + "/conf/checker.yaml");
                String caiusLabel = line.getOptionValue("caiusLabels", StringUtils.EMPTY);
                String caiusNS = line.getOptionValue("caiusNS", StringUtils.EMPTY);
                CaiusPupus auditor = new CaiusPupus();
                //@todo add checker
                Checker conf = auditor.load(confPath);
                JsonObject auditResult;
                if (StringUtils.EMPTY.equals(caiusLabel)) {
                    auditResult = auditor.audit(conf, robot.osClient, caiusNS);
                } else {
                    auditResult = auditor.auditForLabel(conf, robot.osClient, caiusNS, caiusLabel);
                }
                System.out.println(auditResult.toString());
                return;

            }
            if (line.hasOption("ephyra")) {
                String confPath = line.getOptionValue("ephyraConfPath", System.getProperty("user.dir") + "/conf/ephyra.yaml");
                String ephyraNS = line.getOptionValue("ephyraNS", StringUtils.EMPTY);
                String ephyraLabels = line.getOptionValue("ephyraLabels", StringUtils.EMPTY);

                Ephyra eph = new Ephyra();
                JsonObject conf = eph.load(confPath);

                if (line.hasOption("k")) {
                    eph.terminateOldPod(conf, robot.osClient, ephyraNS, ephyraLabels);
                    System.out.println(" Cleanning pod for " + ephyraNS + " done ");
                    log.info(" Cleanning kubernetes pod for " + ephyraNS + " done ");
                    return;
                } else if (line.hasOption("o")) {
                    eph.terminateOldPodOpenshift(conf, robot.osClient, ephyraNS, ephyraLabels);
                    System.out.println(" Cleanning openshift pod for " + ephyraNS + " done ");
                    log.info(ephyraNS);
                    return;
                } else {
                    System.out.println(" You can need to select either o (openshift) or k (kubernetes)");
                    return;
                }

            }

        } catch (org.apache.commons.cli.ParseException ex) {
            java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //    public void scaleDown(String NS) {
    //
    //        if (StringUtils.isBlank(NS)) {
    //            NS = this.osClient.getNamespace();
    //        }
    //        this.scaleDownKube(NS);
    //        if (this.osClient.apps().supportsApiPath("/oapi/v1")) {
    //            this.scaleDownOcp(NS);
    //        }
    //
    //    }

    /**
     * <p>dumpswitch.</p>
     *
     * @param optionValue a {@link java.lang.String} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param nameSpace a {@link java.lang.String} object.
     * @param f a {@link java.io.File} object.
     */
    protected static void dumpswitch(String optionValue, OpenShiftClient osClient, String nameSpace, File f) {
        dumpswitch(optionValue, osClient, nameSpace, f, "json");
    }

    /**
     * <p>dumpswitch.</p>
     *
     * @param optionValue a {@link java.lang.String} object.
     * @param osClient a {@link io.fabric8.openshift.client.OpenShiftClient} object.
     * @param nameSpace a {@link java.lang.String} object.
     * @param f a {@link java.io.File} object.
     * @param outPutFormat a {@link java.lang.String} object.
     */
    protected static void dumpswitch(String optionValue, OpenShiftClient osClient, String nameSpace, File f, String outPutFormat) {
        try {
            DumpProject dumpProject = new DumpProject();

            switch (optionValue) {
                case "all":
                    System.out.println(" Feature not implemented yet");

                case "configmap":
                    System.out.println(dumpProject.dumpConfigmap(osClient, nameSpace, f, outPutFormat));

                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "deploy":
                    System.out.println(dumpProject.dumpDeploy(osClient, nameSpace, f, outPutFormat));
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "secret":
                    System.out.println(dumpProject.dumpSecret(osClient, nameSpace, f, outPutFormat));
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "svc":
                    System.out.println(dumpProject.dumpService(osClient, nameSpace, f, outPutFormat));
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "endpoints":
                    System.out.println(" Feature not implemented yet");
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "hpa":
                    System.out.println(" Feature not implemented yet");
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                //openshift
                case "bc":
                    System.out.println(" Feature not implemented yet");
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "dc":
                    System.out.println(" Feature not implemented yet");
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;
                case "routes":
                    System.out.println(" Feature not implemented yet");
                    if (!optionValue.equals("all")) {
                        break;
                    }
                    ;

                default:
                    System.out.println(" Feature not implemented yet");

            }
        } catch (JSONException ex) {
            java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GCVP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>scaleDownKube.</p>
     *
     * @param NS a {@link java.lang.String} object.
     */
    public void scaleDownKube(String NS) {

        Iterator<Deployment> iterator = this.osClient.apps().deployments().inNamespace(NS).list().getItems().iterator();
        while (iterator.hasNext()) {
            Deployment next = iterator.next();
            if (scaleDownDeploy(next)) {
                continue;
            }
        }

    }

    /**
     * <p>scaleDownOcp.</p>
     *
     * @param NS a {@link java.lang.String} object.
     */
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
                        logger.debug("no modification (current [" + DateFormatUtils.ISO_DATE_FORMAT.format(now) + "+]date before " + L_END_DATE.getlabel() + "=" + next.getMetadata().getLabels().get(L_END_DATE.getlabel()) + ") of {} : {}", next.getMetadata().getNamespace(), next.getMetadata().getName());
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
                        this.osClient.apps().deployments().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);
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
            this.osClient.apps().deployments().inNamespace(next.getMetadata().getNamespace()).createOrReplace(next);

            return true;
        }

        return false;

    }

    /**
     * this method check that the configmap has correct values
     *
     * @param NS a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
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
            Date now = Calendar.getInstance().getTime();
            try {
                lastValidation = DateFormatUtils.ISO_DATE_FORMAT.parse(conf.getMetadata().getLabels().get(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel()));
                long difMillis = now.getTime() - lastValidation.getTime();
                long difDay = difMillis / (60 * 60 * 1000 * 24);

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
