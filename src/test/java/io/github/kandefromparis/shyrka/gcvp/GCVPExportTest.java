/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.fabric8.openshift.client.NamespacedOpenShiftClient;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import static io.github.kandefromparis.shyrka.ShyrkaLabel.L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT;
import static io.github.kandefromparis.shyrka.ShyrkaLabel.L_PROJECT_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @todo refactor for mutualised setup ?
 * @author csabourdin
 */
public class GCVPExportTest {

    private final Logger logger = LoggerFactory.getLogger(GCVPExportTest.class);

    @Rule
    public KubernetesServer kbeServer = new KubernetesServer(true, true);

    @Rule
    public OpenShiftServer ocpServer = new OpenShiftServer(true, true);

    Config conf = new ConfigBuilder().build();

    final String NS = "groumphfs";

    final String dumpDir = System.getProperty("user.dir") + "/target/test-yaml-backup";

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Utils tools = new Utils();
        this.ocpServer = tools.createFakeServer(ocpServer, NS);
        File f = new File(dumpDir);
        f.mkdirs();

        //conf 
    }

    @After
    public void cleanUp() throws IOException {
        this.kbeServer.getMockServer().close();
        this.ocpServer.getMockServer().close();
    }

    @org.junit.Test
    public void testGCVPGetConfigMap() throws Exception {
        Map<String, String> labels = new HashMap<>();
        labels.put(L_PROJECT_NAME.getlabel(), "sample");
        String ackDate = DateFormatUtils.ISO_DATE_FORMAT.format(DateUtils.addDays(Calendar.getInstance().getTime(), 10));
        labels.put(L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT.getlabel(), ackDate);

        ConfigMap map = new ConfigMapBuilder().withNewMetadata().withNamespace(NS).withName("shyrka").withLabels(labels).endMetadata().build();
        ocpServer.getOpenshiftClient().configMaps().inNamespace(NS).create(map);
        //String[] args = {"-d", "configmap", "-dir", dumpDir};
        //GCVP.main(args);
        String optionValue = "configmap";
        NamespacedOpenShiftClient osClient = ocpServer.getOpenshiftClient();
        //osClient.getConfiguration().setNamespace(NS);
        File dumpFolder = new File(dumpDir);
        String outPutFormat = "yaml";
        GCVP.dumpswitch(optionValue, osClient, NS, dumpFolder, outPutFormat);
        File f = new File(dumpDir, "shyrka.configmap.yaml");
        Assert.assertTrue(f.exists());

    }

    @org.junit.Test
    public void testGCVPGetDeploy() throws Exception {
//        String[] args = {"-d", "deploy", "-dir", dumpDir};
//        GCVP.main(args);

        String optionValue = "deploy";
        NamespacedOpenShiftClient osClient = ocpServer.getOpenshiftClient();

        File dumpFolder = new File(dumpDir);

        GCVP.dumpswitch(optionValue, osClient, NS, dumpFolder);
        File f = new File(dumpDir, "sample-dev.deploy.json");
        Assert.assertTrue(f.exists());
        String outPutFormat = "yaml";
        GCVP.dumpswitch(optionValue, osClient, NS, dumpFolder, outPutFormat);
        f = new File(dumpDir, "sample-dev.deploy.yaml");
        Assert.assertTrue(f.exists());
    }

    @org.junit.Test
    public void testGCVPGetSecrets() throws Exception {
        String[] args = {"-d", "secrets", "-dir", dumpDir};

        GCVP.main(args);
        File f = new File(dumpDir, "dev.secrets.json");
        Assert.assertTrue(!f.exists());

    }

}
