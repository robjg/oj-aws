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
import software.amazon.awssdk.services.ec2.model.IpPermission;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class Ec2AuthorizeSecurityGroupJobTest {

    DesignInstance design;

    @Test
    public void testDesign() throws ArooaParseException {

        String xml =
                "<aws:ec2-authorize-security-group xmlns:aws=\"oddjob:aws\"\n" +
                        "name='Our Test' groupId='123' groupName='ABC'>\n" +
                        "  <inboundPermissions>\n" +
                        "    <aws:ec2-ip-permission ipProtocol=\"tcp\" portRange=\"8080-8090\"\n" +
                        "                                        ipRanges=\"1.2.3.4/32\"/>\n" +
                        "  </inboundPermissions>\n" +
                        "</aws:ec2-authorize-security-group>";

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
        assertThat(test.get("groupId"), is("123"));
        assertThat(test.get("groupName"), is("ABC"));
        IpPermission[] permissions = (IpPermission[]) test.get("inboundPermissions");
        assertThat(permissions[0].toPort(), is(8090));
    }

    public static void main(String args[]) throws ArooaParseException {

        Ec2AuthorizeSecurityGroupJobTest test = new Ec2AuthorizeSecurityGroupJobTest();
        test.testDesign();

        ViewMainHelper view = new ViewMainHelper(test.design);
        view.run();

    }
}