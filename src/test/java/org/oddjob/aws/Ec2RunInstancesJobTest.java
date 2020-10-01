package org.oddjob.aws;

import org.apache.commons.beanutils.DynaBean;
import org.junit.jupiter.api.Test;
import org.oddjob.OddjobDescriptorFactory;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.tools.OddjobTestHelper;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class Ec2RunInstancesJobTest {

    DesignInstance design;

    @Test
    public void testDesign() throws ArooaParseException {

        String xml =
                "<aws:ec2-run-instances imageId=\"some-linux\" instanceType=\"t2.micro\" maxCount=\"4\" minCount=\"3\" name=\"Our Test\" xmlns:aws=\"oddjob:aws\">\n" +
                        "    <tags>\n" +
                        "        <map>\n" +
                        "            <values>\n" +
                        "                <value key=\"colour\" value=\"green\"/>\n" +
                        "                <value key=\"flavour\" value=\"chocolate\"/>\n" +
                        "            </values>\n" +
                        "        </map>\n" +
                        "    </tags>\n" +
                        "</aws:ec2-run-instances>";

        ArooaDescriptor descriptor =
                new OddjobDescriptorFactory().createDescriptor(null);

        DesignParser parser = new DesignParser(
                new StandardArooaSession(descriptor));
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(new XMLConfiguration("TEST", xml));

        design = parser.getDesign();

        assertThat(design.getClass(), not(Unknown.class));

        DynaBean test = (DynaBean) OddjobTestHelper
                .createComponentFromConfiguration(
                design.getArooaContext().getConfigurationNode());

        assertThat(test.get("name"), is("Our Test"));
        assertThat(test.get("instanceType"), is("t2.micro"));
        assertThat(test.get("minCount"), is(3));
        assertThat(test.get("maxCount"), is(4));
        Map<String, String> tags = (Map<String, String>) test.get("tags");
        assertThat(tags.get("flavour"), is("chocolate"));
    }

    public static void main(String args[]) throws ArooaParseException {

        Ec2RunInstancesJobTest test = new Ec2RunInstancesJobTest();
        test.testDesign();

        ViewMainHelper view = new ViewMainHelper(test.design);
        view.run();

    }
}