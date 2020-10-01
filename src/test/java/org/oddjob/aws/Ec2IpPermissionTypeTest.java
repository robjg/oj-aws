package org.oddjob.aws;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.IpPermission;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class Ec2IpPermissionTypeTest {

    @Test
    public void testWithSomeFields() {

        Ec2IpPermissionType test = new Ec2IpPermissionType();
        test.setIpProtocol("tcp");
        test.setIpRanges("1.2.3.4/32");
        test.setPortRange("8080-8090");

        IpPermission result = test.toValue();

        assertThat(result.ipProtocol(), is("tcp"));
        assertThat(result.fromPort(), is(8080));
        assertThat(result.toPort(), is(8090));
        assertThat(result.ipRanges().get(0).cidrIp(), is("1.2.3.4/32"));
    }
}