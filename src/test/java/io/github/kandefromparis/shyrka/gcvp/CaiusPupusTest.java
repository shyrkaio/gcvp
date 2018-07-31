/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.openshift.client.NamespacedOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import static io.github.kandefromparis.shyrka.ShyrkaLabel.L_PROJECT_STAGE;
import io.github.kandefromparis.shyrka.projectchecker.model.Checker;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

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
public class CaiusPupusTest {

    public CaiusPupusTest() {
    }

    private final Logger logger = LoggerFactory.getLogger(CaiusPupusTest.class);

    @Rule
    public OpenShiftServer ocpServer = new OpenShiftServer(true, true);

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

        this.ocpServer = tools.createFakeServer(ocpServer, NS);

    }

    @After
    public void cleanUp() throws IOException {
        this.ocpServer.getMockServer().close();
    }

    /**
     * Test of validateQuery method, of class CaiusPupus.
     */
    @Test
    public void testValidateQuery_3args() {
        System.out.println("validateQuery");
//        OpenShiftClient osClient = null;
//        String nameSpace = "";
//        List<RessourceQuery> lstQuery = null;
//        CaiusPupus instance = new CaiusPupus();
//        List<RessourceQuery> expResult = null;
//        List<RessourceQuery> result = instance.validateQuery(osClient, nameSpace, lstQuery);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of validateQuery method, of class CaiusPupus.
     */
    @Test
    public void testValidateQueryOnDeploy() {
        System.out.println("validateQuery");
        NamespacedOpenShiftClient client = ocpServer.getOpenshiftClient().inNamespace(NS);
        String ressourceType = "deploy";
        //Notice that here we use JSonPath on the item 
        String[] query = {"$.spec.[?((@.replicas >= 1) && (@.replicas <= 3))]"};

        CaiusPupus instance = new CaiusPupus();
        Map<String, String> labels = new HashMap<>();
        labels.put(L_PROJECT_STAGE.getlabel(), "dev");

        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.validateQuery(client, NS, ressourceType, labels, query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of validateQuery method, of class CaiusPupus.
     */
    @Test
    public void testValidateQueryOnDC() {
        System.out.println("validateQuery");
        NamespacedOpenShiftClient client = ocpServer.getOpenshiftClient().inNamespace(NS);
        String ressourceType = "dc";
        String[] query = {"$.spec.[?((@.replicas >= 1) && (@.replicas <= 3))]",
            "$.spec.template.spec.containers[*].env[*].[?((@.name == 'NAME0') && (@.value == 'VALUE0'))]"};

        CaiusPupus instance = new CaiusPupus();
        Map<String, String> labels = new HashMap<>();
        labels.put(L_PROJECT_STAGE.getlabel(), "dev");

        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.validateQuery(client, NS, ressourceType, labels, query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    @Test
    public void testJsonPathSample() {
        try {
            String json = "{\"apiVersion\":\"v1\",\"kind\":\"List\",\"items\":[{\"metadata\":{\"finalizers\":[],\"name\":\"sample-dev\",\"namespace\":\"groumphfs\",\"annotations\":{},\"labels\":{\"io.shyrka.erebus/end-date\":\"2018-01-01\",\"io.shyrka.erebus/pjt-stage\":\"dev\",\"io.shyrka.gcvp/scaledown\":\"true\"},\"ownerReferences\":[]},\"apiVersion\":\"extensions/v1beta1\",\"kind\":\"Deployment\",\"spec\":{\"replicas\":1}},{\"metadata\":{\"finalizers\":[],\"name\":\"sample-pprod\",\"namespace\":\"groumphfs\",\"annotations\":{},\"labels\":{\"io.shyrka.erebus/end-date\":\"2018-04-21\",\"io.shyrka.erebus/pjt-stage\":\"pprod\",\"io.shyrka.gcvp/scaledown\":\"true\"},\"ownerReferences\":[]},\"apiVersion\":\"extensions/v1beta1\",\"kind\":\"Deployment\",\"spec\":{\"replicas\":1}},{\"metadata\":{\"finalizers\":[],\"name\":\"sample-prod\",\"namespace\":\"groumphfs\",\"annotations\":{},\"labels\":{\"io.shyrka.erebus/end-date\":\"2018-09-21\",\"io.shyrka.erebus/pjt-stage\":\"prod\",\"io.shyrka.gcvp/scaledown\":\"true\"},\"ownerReferences\":[]},\"apiVersion\":\"extensions/v1beta1\",\"kind\":\"Deployment\",\"spec\":{\"replicas\":1}}]}";
            ReadContext ctx = JsonPath.parse(json);
            //Notice that here we use JSonPath on the item List
            Integer read = ctx.read("$.items.[0].spec.replicas", Integer.class);
            List<Map<String, Object>> spec = ctx.read("$.items.[0].spec.[?((@.replicas >= 1) && (@.replicas <= 3))]");

            //List<Map<String, Object>> books =  JsonPath.parse(json).read("$.store.book[?(@.price < 10)]");
            assertTrue(spec.size() >= 1);
            assertTrue(spec.size() <= 3);

        } catch (InvalidPathException ipe) {
            logger.error(ipe.toString());
            fail(ipe.getMessage());
        }
    }

    @Test
    public void testWithJsonPathProbpath() {
        DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).list();
        String query = "$.items.[0].spec.template.spec.containers[*].readinessProbe";
        //.items[]?.spec.template.spec.containers[0].readinessProbe
        if (null == list) {
            fail("null == list");
        }
        //@todo this might not be the correct way to do it since It feel that I do serelize in and out too much
        ObjectMapper mapper = new ObjectMapper();
        {
            try {
                JSONObject jso = new JSONObject(mapper.writeValueAsString(list));
                //System.out.println(jso.toString());
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                ReadContext ctx = JsonPath.parse(jso.toString());

                List<Map<String, Object>> spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

                query = "$.items.[0].spec.template.spec.containers[*].readinessProbe.httpGet.[?(@.path == '/prob-path')]";
                spec = null;
                spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

                query = "$.items.[0].spec.template.spec.containers[*].env[*].[?((@.name == 'NAME0') && (@.value == 'VALUE0'))]";
                spec = null;
                spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void testWithJsonPathProbpath2() {
        DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).list();
        String query = "$.items.[0].spec.template.spec.containers[*].readinessProbe";
        //.items[]?.spec.template.spec.containers[0].readinessProbe
        if (null == list) {
            fail("null == list");
        }
        //@todo this might not be the correct way to do it since It feel that I do serelize in and out too much
        ObjectMapper mapper = new ObjectMapper();
        {
            try {
                JSONObject jso = new JSONObject(mapper.writeValueAsString(list));
                //System.out.println(jso.toString());
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                ReadContext ctx = JsonPath.parse(jso.toString());

                List<Map<String, Object>> spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

                query = "$.items.[0].spec.template.spec.containers[*].readinessProbe.httpGet.[?(@.path == '/prob-path')]";
                spec = null;
                spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                fail(ex.getMessage());
            }
        }
    }

    /**
     *
     */
    @Test
    public void testWithJsonReadinessProbe() {
        DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).list();
        String query = "$.items.[0].spec.template.spec.containers[*].readinessProbe";
        //.items[]?.spec.template.spec.containers[0].readinessProbe
        if (null == list) {
            fail("null == list");
        }
        //@todo this might not be the correct way to do it since It feel that I do serelize in and out too much
        ObjectMapper mapper = new ObjectMapper();
        {
            try {
                JSONObject jso = new JSONObject(mapper.writeValueAsString(list));
                //System.out.println(jso.toString());
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                ReadContext ctx = JsonPath.parse(jso.toString());

                List<Map<String, Object>> spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

                query = "$.items.[0].spec.template.spec.containers[*].readinessProbe.httpGet.[?(@.path == '/prob-path')]";
                spec = null;
                spec = ctx.read(query);
                assertTrue(spec.size() >= 1);

            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void testRessourceLimits() {
        JSONObject json = new JSONObject("{\"namespace\":\"groumphfs\",\n"
                + "\"name\":\"test\",\n"
                + "\"ressourceType\":\"deploy\",\n"
                + "\"labels\":\n"
                + "{\"io.shyrka.erebus/pjt-stage\":\"test\"},\n"
                + "\"validate\":\n"
                + " {\"query\":\"$.spec.template.spec.containers[0].[?(@.image=='foo.server.org/somevalue/someothervalue:1.2')]\",\n"
                + " \"result\":\"\"}\n"
                + "}");
        JSONObject labels = json.getJSONObject("labels");
        Iterator<String> keys = labels.keys();
        Map<String, String> mapLabel = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            mapLabel.put(key, labels.getString(key));
        }

        // DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).withLabels(mapLabel).list();
        // this Raise a exception so I add ah expect
        //DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).withLabel("io.shyrka.erebus/pjt-stage","test").list();
        this.ocpServer.expect().withPath("/apis/extensions/v1beta1/namespaces/"
                + NS
                + "/deployments?labelSelector=io.shyrka.erebus/pjt-stage=test")
                .andReturn(200, tools.getDeploy(NS, "2018-01-01", "true", "test", 1, "foo.server.org/somevalue/someothervalue:1.2", "smooth"));

        DeploymentList list = this.ocpServer.getKubernetesClient().extensions().deployments().inNamespace(NS).list();
        Iterator<Deployment> iterator = list.getItems().iterator();
        while (iterator.hasNext()) {
            Deployment next = iterator.next();
            if (StringUtils.equals(next.getMetadata().getLabels().get("io.shyrka.erebus/pjt-stage"), "test")) {
                String query = json.getJSONObject("validate").getString("query");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JSONObject jso = new JSONObject(mapper.writeValueAsString(next));
                    //System.out.println(jso.toString());
                    java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.WARNING, jso.toString());
                    ReadContext ctx = JsonPath.parse(jso.toString());

                    List<Map<String, Object>> spec = ctx.read(query);
                    assertTrue(spec.size() >= 1);
                } catch (JsonProcessingException ex) {
                    java.util.logging.Logger.getLogger(CaiusPupus.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    fail(ex.getMessage());
                }
            }
        }
    }

    /**
     * Test of load method, of class CaiusPupus.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");

        CaiusPupus instance = new CaiusPupus();
        String expResult = "ProjectChecker";
        String expResultName = "imageSourceValidation";

        String confPath = System.getProperty("user.dir") + "/target/test-classes/checker-sample.yaml";
        Checker checkerconf = instance.load(confPath);

        assertEquals(expResult, checkerconf.getKind());
        assertEquals(3, checkerconf.getSpec().getChecks().size());
        assertEquals(expResultName, checkerconf.getSpec().getChecks().get(0).getName());
        assertEquals(1, checkerconf.getSpec().getChecks().get(0).getQueries().size());
    }

    /**
     * Test of audit method, of class CaiusPupus.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testAudit() throws IOException {
        System.out.println("audit");
        CaiusPupus instance = new CaiusPupus();

        String confPath = System.getProperty("user.dir") + "/target/test-classes/checker-sample.yaml";
        Checker checkerconf = instance.load(confPath);

        JsonObject expResult = new JsonObject("{\"test\":{\"stack\":\"no-stack\",\"nbOk\":0,\"project\":\"test\",\"nbChecks\":3,\"labels\":{\"io.shyrka.erebus/pjt-stage\":\"test\"}}}");
        JsonObject result = instance.audit(checkerconf, this.ocpServer.getOpenshiftClient());
        //java.lang.AssertionError: expected:<null> but was:<>
        assertEquals(expResult, result);
    }

    /**
     * Test of audit method, of class CaiusPupus.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testAuditWithGroumpfsNS() throws IOException {
        System.out.println("audit");
        CaiusPupus instance = new CaiusPupus();

        String confPath = System.getProperty("user.dir") + "/target/test-classes/checker-groumphfs.yaml";
        Checker checkerconf = instance.load(confPath);

        JsonObject expResult = new JsonObject("{\"test\":{\"stack\":\"no-stack\",\"nbOk\":0,\"project\":\"test\",\"nbChecks\":3,\"labels\":{\"io.shyrka.erebus/pjt-stage\":\"test\"}},\"groumphfs\":{\"stack\":\"no-stack\",\"nbOk\":3,\"project\":\"groumphfs\",\"nbChecks\":4,\"labels\":{\"io.shyrka.erebus/pjt-stage\":\"test\"}}}");
        JsonObject result = instance.audit(checkerconf, this.ocpServer.getOpenshiftClient());
        //java.lang.AssertionError: expected:<null> but was:<{"test":{"stack":"no-stack","nbOk":0,"project":"test","nbCheck":1,"nbChecks":0,"labels":{"io.shyrka.erebus/pjt-stage":"test"}}}>
        assertEquals(expResult, result);
    }

    /**
     * Test of auditForLabel method, of class CaiusPupus.
     */
    @Test
    public void testAuditForLabel() throws IOException {
        System.out.println("auditForLabel");

        CaiusPupus instance = new CaiusPupus();

        String confPath = System.getProperty("user.dir") + "/target/test-classes/checker-sample.yaml";
        Checker checkerconf = instance.load(confPath);

        JsonObject expResult = new JsonObject("{\"test\":{\"stack\":\"no-stack\",\"nbOk\":0,\"project\":\"test\",\"nbChecks\":3,\"labels\":{\"io.shyrka.erebus/pjt-stage\":\"test\"}}}>");
        String caiusLabel = "";

        JsonObject result = instance.auditForLabel(checkerconf, this.ocpServer.getOpenshiftClient(), StringUtils.EMPTY, caiusLabel);
        assertEquals(expResult, result);
    }

    /**
     * Test of auditForLabel method, of class CaiusPupus.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testAuditForGroumphfsWithLabel() throws IOException {
        System.out.println("auditForLabel");

        CaiusPupus instance = new CaiusPupus();

        String confPath = System.getProperty("user.dir") + "/target/test-classes/checker-groumphfs.yaml";
        Checker checkerconf = instance.load(confPath);

        JsonObject expResult = new JsonObject("{\"groumphfs\":{\"stack\":\"no-stack\",\"nbOk\":3,\"project\":\"groumphfs\",\"nbChecks\":4,\"labels\":{\"io.shyrka.erebus/pjt-stage\":\"test\"}}}>");
        String caiusLabel = "";

        JsonObject result = instance.auditForLabel(checkerconf, this.ocpServer.getOpenshiftClient(), NS, caiusLabel);
        assertEquals(expResult, result);
    }

}
