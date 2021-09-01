/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka.gcvp;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.HTTPGetAction;
import io.fabric8.kubernetes.api.model.HTTPHeader;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.openshift.api.model.Build;
import io.fabric8.openshift.api.model.BuildList;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DoneableBuild;
import io.fabric8.openshift.client.dsl.BuildResource;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import static io.github.kanedafromparis.shyrka.ShyrkaLabel.L_END_DATE;
import static io.github.kanedafromparis.shyrka.ShyrkaLabel.L_PROJECT_STAGE;
import static io.github.kanedafromparis.shyrka.ShyrkaLabel.L_SCALEDOWN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author csabourdin
 */
class Utils {

    OpenShiftServer createFakeServer(OpenShiftServer ocpServer, String NS) {
        ocpServer.before();
        createDeployement(ocpServer, NS, "2018-01-01", "true", "test", 1, "foo.server.org/somevalue/someothervalue:1.2", "smooth");
        createDeployement(ocpServer, NS, "2018-01-01", "true", "dev", 1, "foo.server.org/somevalue/someothervalue:1.2", "smooth");

        String pprodDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addMonths(Calendar.getInstance().getTime(), 1));

        createDeployement(ocpServer, NS, pprodDate, "true", "pprod", 3, "foo.server.org/somevalue/someothervalue:1.2", "smooth");

        String prodDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addMonths(Calendar.getInstance().getTime(), 6));
        createDeployement(ocpServer, NS, prodDate, "true", "prod", 3, "foo.server.org/somevalue/someothervalue:1.1", "smooth");

        return ocpServer;

    }

    private void createDeployement(OpenShiftServer ocpServer, String NS, String lEndDate, String lScaleDown, String lStage, Integer replicas, String image, String name) {
        Deployment deploy = this.getDeploy(NS, lEndDate, lScaleDown, lStage, replicas, image, name);
        
        ocpServer.getOpenshiftClient().namespaces().create(new NamespaceBuilder().withNewMetadata().withName(NS).and().withNewSpec().endSpec().build());
        ocpServer.getOpenshiftClient().apps().deployments().inNamespace(NS).create(deploy);
        //
        ocpServer.getOpenshiftClient().deploymentConfigs().inNamespace(NS).create(this.getDC(NS, lEndDate, lScaleDown, lStage, replicas, image, name));
    }

    public Deployment getDeploy(String ns, String lEndDate, String lScaleDown, String lStage, String image, String name) {
        return this.getDeploy(ns, lEndDate, lScaleDown, lStage, 1, image, name);
    }

    public Deployment getDeploy(String ns, String lEndDate, String lScaleDown, String lStage, Integer replicas, String image, String name) {
        Map<String, String> labels = getDCLabels(lEndDate, lScaleDown, lStage);

        String depName = "sample-" + lStage;
        Deployment depDev = new DeploymentBuilder()
                .withNewMetadata().withNamespace(ns).withName(depName)
                .withLabels(labels).and().withNewSpec()
                .withReplicas(replicas)
                .withTemplate(this.getPodTemplate(depName, image, name))
                .endSpec().build();

        return depDev;
    }

    public DeploymentConfig getDC(String ns, String lEndDate, String lScaleDown, String lStage, String image, String name) {
        return this.getDC(ns, lEndDate, lScaleDown, lStage, 1, image, name);
    }

    /**
     *
     * @param lEndDate
     * @param lScaleDown
     * @param lStage
     * @param replicas
     * @return
     */
    public DeploymentConfig getDC(String ns, String lEndDate, String lScaleDown, String lStage, Integer replicas, String image, String name) {
        Map<String, String> labels = getDCLabels(lEndDate, lScaleDown, lStage);

        String dcName = "sample-" + lStage;
        DeploymentConfig dcDev = new DeploymentConfigBuilder()
                .withNewMetadata().withNamespace(ns).withName(dcName)
                .withLabels(labels).and().withNewSpec()
                .withReplicas(replicas)
                .withTemplate(this.getPodTemplate(dcName + lStage, image, name))
                .endSpec().build();

        return dcDev;

    }

    private Map<String, String> getDCLabels(String lEndDate, String lScaleDown, String lStage) {
        Map<String, String> labels = new HashMap<>();
        labels.put(L_END_DATE.getlabel(), lEndDate);
        labels.put(L_SCALEDOWN.getlabel(), lScaleDown);
        labels.put(L_PROJECT_STAGE.getlabel(), lStage);
        return labels;
    }

    public ResourceRequirements getResources(String rCPU, String rMemory, String lCPU, String lMemory) {

        Map<String, Quantity> requests = new HashMap<>();
        requests.put("cpu", new Quantity(rCPU));
        requests.put("memory", new Quantity(rMemory));

        Map<String, Quantity> limits = new HashMap<>();
        limits.put("cpu", new Quantity(lCPU));
        limits.put("memory", new Quantity(lMemory));

        ResourceRequirementsBuilder resourcesReq = new ResourceRequirementsBuilder()
                .withRequests(requests)
                .withLimits(limits);

        return resourcesReq.build();
    }

    public Probe getProbe(String host, List<HTTPHeader> httpHeaders, String path, Integer port, String scheme, Integer initDelay, Integer period, Integer timeout, Integer success) {
        ProbeBuilder prob = new ProbeBuilder()
                .withHttpGet(new HTTPGetAction(host, null, path, new IntOrString(port), scheme))
                .withInitialDelaySeconds(initDelay)
                .withPeriodSeconds(period)
                .withTimeoutSeconds(timeout)
                .withSuccessThreshold(success);
        return prob.build();
    }

    public Probe getDefProbe() {

        return this.getProbe("", null, "/prob-path", 8080, "http", 15, 5, 2, 3);
    }

    public PodTemplateSpec getPodTemplate(String deploymentName, String image, String name) {
        String def_0 = "foo.server.org/somevalue/someothervalue:1.2";
        String def_1 = "defaultName";

        List<ContainerPort> ports = new ArrayList<>();
        ports.add(new ContainerPort(8080, "", 33456, "8080-tcp", "HTTP"));

        EnvVar envs[] = new EnvVar[2];
        envs[0] = new EnvVarBuilder().withName("NAME0").withValue("VALUE0").build();
        envs[1] = new EnvVarBuilder().withName("NAME1").withValue("VALUE1").build();

//        envs[2] = new EnvVar("NAME2", "VALUE2", new EnvVarSourceBuilder().build());
//        envs[3] = new EnvVar("NAME3", "VALUE3", new EnvVarSourceBuilder().build());
        Container containers[] = new Container[1];
        containers[0] = new ContainerBuilder()
                .withImage(image).withName(name)
                .withResources(this.getResources("500m", "256Mi", "1000m", "2048Mi"))
                .withPorts(ports)
                .withLivenessProbe(this.getDefProbe())
                .withReadinessProbe(this.getDefProbe())
                .withEnv(envs)
                .build();

        ObjectMeta metadata = new ObjectMeta();
        Map<String, String> labels = new HashMap<>();
        labels.put("deploymentconfig", name);
        labels.put("deployment", name);
        metadata.setLabels(labels);

        PodSpecBuilder podSpec = new PodSpecBuilder()
                .withContainers(Arrays.asList(containers));

        PodTemplateSpecBuilder templateSpec = new PodTemplateSpecBuilder()
                .withMetadata(metadata)
                .withSpec(podSpec.build());
        return templateSpec.build();

    }

    Node createNode(String name) {
        ObjectMeta meta = new ObjectMeta();
        meta.setName(name);
        Node node00 = new Node();
        node00.setMetadata(meta);
        return node00;
    
    }

}
