package org.oddjob.aws;

import org.apache.commons.beanutils.DynaBean;
import org.hamcrest.Matchers;
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

class Ec2StopInstancesJobTest {

    DesignInstance design;

    @Test
    public void testDesign() throws ArooaParseException {

        String xml =
                "<aws:ec2-stop-instances instanceIds=\"12, 34\" name=\"Our Test\" xmlns:aws=\"oddjob:aws\"/>";

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
        assertThat((String[]) test.get("instanceIds"), Matchers.arrayContaining("12", "34"));
    }

    public static void main(String args[]) throws ArooaParseException {

        Ec2StopInstancesJobTest test = new Ec2StopInstancesJobTest();
        test.testDesign();

        ViewMainHelper view = new ViewMainHelper(test.design);
        view.run();

    }
}