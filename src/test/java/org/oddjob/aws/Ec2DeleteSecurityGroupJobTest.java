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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class Ec2DeleteSecurityGroupJobTest {

    DesignInstance design;

    @Test
    public void testDesign() throws ArooaParseException {

        String xml =
                "<aws:ec2-delete-security-group  groupId=\"12\" groupName=\"ABC\" name=\"Our Test\" xmlns:aws=\"oddjob:aws\">\n" +
                        "</aws:ec2-delete-security-group>";

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
        assertThat(test.get("groupId"), is("12"));
        assertThat(test.get("groupName"), is("ABC"));
    }

    public static void main(String args[]) throws ArooaParseException {

        Ec2DeleteSecurityGroupJobTest test = new Ec2DeleteSecurityGroupJobTest();
        test.testDesign();

        ViewMainHelper view = new ViewMainHelper(test.design);
        view.run();

    }
}