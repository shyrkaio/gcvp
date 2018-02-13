/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.DoneableConfigMap;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeSpec;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.api.model.extensions.DoneableDeployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.ScalableResource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Rule;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigList;
import io.fabric8.openshift.api.model.DoneableDeploymentConfig;
import io.fabric8.openshift.client.NamespacedOpenShiftClient;
import io.fabric8.openshift.client.dsl.DeployableScalableResource;
import io.github.kandefromparis.shyrka.ConformityIssue;
import static io.github.kandefromparis.shyrka.ConformityIssue.*;
import static io.github.kandefromparis.shyrka.ShyrkaLabel.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author csabourdin
 */
public class GCVPTest {

    private final Logger logger = LoggerFactory.getLogger(GCVP.class);

    @Rule
    public KubernetesServer kbeServer = new KubernetesServer(true, true);

    @Rule
    public OpenShiftServer ocpServer = new OpenShiftServer(true, true);

    final String NS = "groumphfs";

    public GCVPTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        kbeServer.getClient().extensions().deployments().inNamespace(NS).create(this.getDeploy(NS,"2018-01-01", "true", "dev", 1));
        ocpServer.getOpenshiftClient().deploymentConfigs().inNamespace(NS).create(this.getDC(NS,"2018-01-01", "true", "dev", 1));

        String pprodDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addMonths(Calendar.getInstance().getTime(), 1));

        kbeServer.getClient().extensions().deployments().inNamespace(NS).create(this.getDeploy(NS,pprodDate, "true", "pprod", 1));
        ocpServer.getOpenshiftClient().deploymentConfigs().inNamespace(NS).create(this.getDC(NS,pprodDate, "true", "pprod", 1));

        String prodDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addMonths(Calendar.getInstance().getTime(), 6));

        kbeServer.getClient().extensions().deployments().inNamespace(NS).create(this.getDeploy(NS,prodDate, "true", "prod", 1));
        ocpServer.getOpenshiftClient().deploymentConfigs().inNamespace(NS).create(this.getDC(NS,prodDate, "true", "prod", 1));

        
    }
    @After
    public void cleanUp() throws IOException {
        this.kbeServer.getMockServer().close();
        this.ocpServer.getMockServer().close();
    }

    public Deployment getDeploy(String ns, String lEndDate, String lScaleDown, String lStage) {
        return this.getDeploy(ns,lEndDate, lScaleDown, lStage, 1);
    }

    public Deployment getDeploy(String ns,String lEndDate, String lScaleDown, String lStage, Integer replicas) {
        Map<String, String> labels = getDCLabels(lEndDate, lScaleDown, lStage);

        Deployment depDev = new DeploymentBuilder()
                .withNewMetadata().withNamespace(ns).withName("sample" + lStage)
                .withLabels(labels).and().withNewSpec()
                .withReplicas(replicas).endSpec().build();

        return depDev;
    }

    public DeploymentConfig getDC(String ns, String lEndDate, String lScaleDown, String lStage) {
        return this.getDC(ns,lEndDate, lScaleDown, lStage, 1);
    }

    /**
     *
     * @param lEndDate
     * @param lScaleDown
     * @param lStage
     * @param replicas
     * @return
     */
    public DeploymentConfig getDC(String ns, String lEndDate, String lScaleDown, String lStage, Integer replicas) {
        Map<String, String> labels = getDCLabels(lEndDate, lScaleDown, lStage);

        DeploymentConfig dcDev = new DeploymentConfigBuilder()
                .withNewMetadata().withNamespace(ns).withName("sample" + lStage)
                .withLabels(labels).and().withNewSpec()
                .withReplicas(replicas).endSpec().build();

        return dcDev;

    }

    private Map<String, String> getDCLabels(String lEndDate, String lScaleDown, String lStage) {
        Map<String, String> labels = new HashMap<>();
        labels.put(L_END_DATE.getlabel(), lEndDate);
        labels.put(L_SCALEDOWN.getlabel(), lScaleDown);
        labels.put(L_PROJECT_STAGE.getlabel(), lStage);
        return labels;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testMain() throws Exception {
        kbeServer.expect().withPath("/api/v1/nodes/node1").andReturn(200, new PodBuilder().build()).once();
        kbeServer.expect().withPath("/api/v1/nodes/node2").andReturn(200, new PodBuilder().build()).once();
        kbeServer.expect().withPath("/api/v1/nodes/node3").andReturn(404, null).once();

        KubernetesClient client = kbeServer.getClient();

        Node node = client.nodes().withName("node1").get();
        assertNotNull(node);

        node = client.nodes().withName("node2").get();
        assertNotNull(node);

        NodeSpec spec = client.nodes().withName("node3").get().getSpec();
        assertNull(spec);
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testScaleDownDeployement() throws Exception {
        KubernetesClient client = kbeServer.getClient();
        MixedOperation<Deployment, DeploymentList, DoneableDeployment, ScalableResource<Deployment, DoneableDeployment>> deployments = client.extensions().deployments();
        Iterator<Deployment> iterator = deployments.list().getItems().iterator();
        while (iterator.hasNext()) {
            Deployment next = iterator.next();
            Assert.assertTrue(next.getSpec().getReplicas() > 0);
        }

        GCVP robot = new GCVP(client.getConfiguration());
        robot.scaleDown(NS);
        iterator = deployments.inNamespace(NS).list().getItems().iterator();
        while (iterator.hasNext()) {
            Deployment next = iterator.next();
            if (next.getMetadata().getLabels().containsKey(L_PROJECT_STAGE.getlabel())
                    && !next.getMetadata().getLabels().get(L_PROJECT_STAGE.getlabel()).equalsIgnoreCase("prod")
                    && !next.getMetadata().getLabels().get(L_PROJECT_STAGE.getlabel()).equalsIgnoreCase("pprod")) {
                logger.error(next.getMetadata().getName()+" "+next.toString());
                Assert.assertTrue(next.getSpec().getReplicas() < 1);
            } else {
                logger.error(next.getMetadata().getName()+" "+next.toString());
                Assert.assertTrue(next.getSpec().getReplicas() > 0);
            }
        }

    }

    /**
     * Test of main method, of class GCVP.
     *
     * @throws java.lang.Exception
     */
    @org.junit.Test
    public void testScaleDownDC() throws Exception {
        NamespacedOpenShiftClient client = ocpServer.getOpenshiftClient().inNamespace(NS);
        

        MixedOperation<DeploymentConfig, DeploymentConfigList, DoneableDeploymentConfig, DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig>> deploymentConfigs = client.deploymentConfigs();
        Iterator<DeploymentConfig> iterator = deploymentConfigs.list().getItems().iterator();
        while (iterator.hasNext()) {
            DeploymentConfig next = iterator.next();
            Assert.assertTrue(next.getSpec().getReplicas() > 0);
        }

        GCVP robot = new GCVP(client.getConfiguration());
        robot.scaleDown(NS);
        iterator = client.deploymentConfigs().inNamespace(NS).list().getItems().iterator();
        while (iterator.hasNext()) {
            DeploymentConfig next = iterator.next();
            if (next.getMetadata().getLabels().containsKey(L_PROJECT_STAGE.getlabel())
                    && !next.getMetadata().getLabels().get(L_PROJECT_STAGE.getlabel()).equalsIgnoreCase("prod")
                    && !next.getMetadata().getLabels().get(L_PROJECT_STAGE.getlabel()).equalsIgnoreCase("pprod")) {
                logger.error(next.getMetadata().getName()+" "+next.toString());
                Assert.assertTrue(next.getSpec().getReplicas() < 1);
            } else {
                logger.error(next.getMetadata().getName()+" "+next.toString());
                Assert.assertTrue(next.getSpec().getReplicas() > 0);
            }
        }

    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testNoShyrkaConfigMap() throws Exception {
        GCVP robot = new GCVP(ocpServer.getKubernetesClient().getConfiguration());
        List<ConformityIssue> conformityCheck = robot.conformityCheck(ocpServer.getOpenshiftClient().getNamespace());
        Assert.assertTrue(conformityCheck.size() == 1);
        Assert.assertTrue(NO_SHYRKA_CONFIGMAP.equals(conformityCheck.get(0)));
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testNoShyrkaProjectOwner() throws Exception {

        ConfigMap configmap1 = new ConfigMapBuilder().withNewMetadata().withName("configmap1").endMetadata()
                .addToData("one", "1")
                .build();
        Map<String, String> labels = new HashMap<>();
        labels.put(L_PROJECT_NAME.getlabel(), "sample");
        String ackDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addDays(Calendar.getInstance().getTime(), 25));
        labels.put(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel(), ackDate);

        ConfigMap map = new ConfigMapBuilder().withNewMetadata().withName("shyrka").withLabels(labels).endMetadata().build();
        map.getMetadata().getLabels().put(L_PROJECT_NAME.getlabel(), "sample");
        //map.getMetadata().getLabels().put(L_PRODUCT_OWNER.getlabel(), "");
        //map.getMetadata().getAnnotations().put(A_PRODUCT_OWNER.getlabel(), "marie.curie@yopmail.com");

        ocpServer.expect().withPath("/api/v1/namespaces/shyrka/configmaps/shyrka").andReturn(200, map).always();

        KubernetesClient kubernetesClient = ocpServer.getOpenshiftClient();
        kubernetesClient.configMaps().inNamespace("shyrka").create(map);

        kubernetesClient.getConfiguration().setNamespace("shyrka");
        Config configuration = kubernetesClient.getConfiguration();
        configuration.setNamespace("shyrka");
        GCVP robot = new GCVP(configuration);
        List<ConformityIssue> conformityCheck = robot.conformityCheck(this.ocpServer.getOpenshiftClient().getNamespace());
        MixedOperation<ConfigMap, ConfigMapList, DoneableConfigMap, Resource<ConfigMap, DoneableConfigMap>> configMaps = kubernetesClient.configMaps();
        Resource<ConfigMap, DoneableConfigMap> withName = configMaps.withName("shyrka");

        ConfigMap get = withName.get();

        //    Assert.assertTrue(conformityCheck.contains(NO_PROJECT_OWNER_LABEL));
        //    Assert.assertEquals(2, conformityCheck.size());
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testShyrkaProjectOwnerConfirmationTooOld() throws Exception {
        //
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testScaleDownAllAndSendMail() throws Exception {
        //
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testScaleDownAllAndCreateEvent() throws Exception {
        //
    }

}
