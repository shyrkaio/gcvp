/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka.gcvp;

import io.github.kanedafromparis.shyrka.gcvp.Ephyra;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.NamespacedOpenShiftClient;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author csabourdin
 */
public class EphyraTest {

    public EphyraTest() {
    }

    private final Logger logger = LoggerFactory.getLogger(CaiusPupusTest.class);

    @Rule
    public OpenShiftServer ocpServer;
    JsonObject conf = new JsonObject().put("maxDuration", 2);

    Utils tools = new Utils();
    final String NS = "groumphfs";

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.ocpServer = new OpenShiftServer(true, true);
        this.ocpServer.before();
        this.ocpServer = tools.createFakeServer(this.ocpServer, NS);

    }

    @After
    public void cleanUp() throws IOException {
        this.ocpServer.getMockServer().close();
    }

    /**
     * Test of load method, of class Ephyra.
     */
    @Test
    @Ignore
    //@DisabledIfSystemProperty(named = "ci-server", matches = "true")

    public void testLoad() throws Exception {
        System.out.println("load");
        String confPath = "";
        Ephyra instance = new Ephyra();
        JsonObject expResult = null;
        JsonObject result = instance.load(confPath);
        assertEquals(expResult, result);
    }

    /**
     * Test of terminateOldPodOpenshift method, of class Ephyra.
     */
    @Test
    public void testTerminateOldPodOpenshift() {
        System.out.println("terminateOldPodOpenshift");
        NamespacedOpenShiftClient client = ocpServer.getOpenshiftClient().inNamespace(NS);

        Ephyra instance = new Ephyra();
        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.terminateOldPodOpenshift(conf, client, NS, StringUtils.EMPTY);
        assertEquals(expResult, result);
    }

    /**
     * Test of terminateOldPodOpenshift method, of class Ephyra.
     */
    @Test
    public void testDate() {

        int maxDuration = 5;
        ZonedDateTime now = ZonedDateTime.now();

        ZonedDateTime newDate000 = ZonedDateTime.parse("2018-07-25T18:58:44Z");
        
        ZonedDateTime newDate001 = ZonedDateTime.parse("2018-07-25T18:52:44Z");
        ZonedDateTime newDate002 = ZonedDateTime.parse("2018-07-25T18:54:44Z");

        assertTrue(newDate001.isBefore(now));
        
        assertEquals(Duration.between(newDate001, newDate000).toMinutes(),  6);
        
        assertTrue(Duration.between(newDate001,newDate000).toMinutes() > maxDuration);
        
        assertEquals(Duration.between(newDate002, newDate000).toMinutes(), 4);
        assertFalse(Duration.between(newDate002, newDate000).toMinutes() > maxDuration);
    }

    /**
     * Test of terminateOldPod method, of class Ephyra.
     */
    @Test
    public void testTerminateOldPod() {
        System.out.println("terminateOldPod");

        KubernetesClient osClient = ocpServer.getKubernetesClient();

        Ephyra instance = new Ephyra();
        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.terminateOldPod(conf, osClient, NS,StringUtils.EMPTY);
        assertEquals(expResult, result);
    }

}
