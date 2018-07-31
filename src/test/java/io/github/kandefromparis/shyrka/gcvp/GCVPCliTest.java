/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kandefromparis.shyrka.gcvp;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.mail.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author csabourdin
 */
public class GCVPCliTest {

    private final Logger logger = LoggerFactory.getLogger(GCVP.class);

    public GCVPCliTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void cleanUp() throws IOException {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    public void testHelp() throws Exception {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();
        String[] args = new String[]{"--block-size=10"};
        Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        options.addOption("b", "escape", false, "print octal escapes for nongraphic "
                + "characters");
        options.addOption(Option.builder().longOpt("block-size")
                .desc("use SIZE-byte blocks")
                .hasArg()
                .argName("SIZE")
                .build());
        options.addOption("B", "ignore-backups", false, "do not list implied entried "
                + "ending with ~");
        options.addOption("c", false, "with -lt: sort by, and show, ctime (time of last "
                + "modification of file status information) with "
                + "-l:show ctime and sort by name otherwise: sort "
                + "by ctime");
        options.addOption("C", false, "list entries by columns");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            Assert.assertTrue(line.hasOption("block-size"));
            Assert.assertEquals("10", line.getOptionValue("block-size"));

        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    @org.junit.Test
    public void testGCVP() throws Exception {
        Options options = new Options();
        options.addOption("s", "scaleDown", false, "This commande will scale down Deployment and DeploymentConfig according to label policy");
        options.addOption("S", "scaleUP", false, "This commande will scale up Deployment and DeploymentConfig according to label policy [not implemented yet]");
        options.addOption("c", "check", false, "This commande will display conformity issue");
        options.addOption("h", "help", false, "This commande will display this message");
        options.addOption("o", "output", true, "This commande use option for output, email, webhoock, events [not implemented yet]");
        options.addOption("l", "logfile", true, "This commande allow to overwrite default logfile]");

        CommandLineParser parser = new DefaultParser();
        try {
            String[] args = new String[]{"-s", "-S", "--check", "-l simplelogger.properties"};
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            Assert.assertTrue(line.hasOption("s"));
            Assert.assertTrue(line.hasOption("c"));
            Assert.assertTrue(line.hasOption("l"));
            Assert.assertTrue(line.hasOption("S"));
            Assert.assertEquals("simplelogger.properties", line.getOptionValue("l").trim());

        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    /**
     * Test of main method, of class GCVP.
     */
    @org.junit.Test
    @Ignore
    public void testSendMail() throws Exception {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();
        Email email = new SimpleEmail();
        email.setHostName("192.168.99.100");
        email.setSmtpPort(2525);
        email.setStartTLSEnabled(true);
        email.setFrom("user@gmail.com");
        email.setSubject("Simple email");
        email.setMsg("This is a simple plain text email :-)");
        email.addTo("foo@bar.com");
        email.send();
    }

}
