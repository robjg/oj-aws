package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.types.ValueFactory;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @oddjob.description Ip Permission Type for Security Group Rules.
 */
public class Ec2IpPermissionType implements ValueFactory<IpPermission> {

    public static String DEFAULT_PROTOCOL = "tcp";

    public static String DEFAULT_RANGE = "0.0.0.0/0";

    /**
     * @oddjob.property
     * @oddjob.description The protocol for this permission.
     * @oddjob.required No, defaults to 'tcp'.
     */
    private String ipProtocol = DEFAULT_PROTOCOL;

    /**
     * @oddjob.property
     * @oddjob.description The port range for this permission. In the form nnnn or nnnnn-mmmmm.
     * @oddjob.required Yes.
     */
    private String portRange;

    /**
     * @oddjob.property
     * @oddjob.description Comma separated port ranges in
     * <a href="https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing">CIDR</a> format.
     * @oddjob.required No, defaults to '0.0.0.0/0'.
     */
    private String[] ipRanges = { DEFAULT_RANGE };

    @Override
    public IpPermission toValue() {

        String ipProtocol = Objects.requireNonNull(this.ipProtocol);

        IpPermission.Builder builder  = IpPermission.builder()
                .ipProtocol(ipProtocol);

        Optional.ofNullable(portRange)
                .ifPresent(range -> {
                    Integer from;
                    Integer to;
                    String[] split = range.split("-");
                    from = Integer.valueOf(split[0]);
                    if (split.length == 1) {
                        to = from;
                    }
                    else {
                        to = Integer.valueOf(split[1]);
                    }
                    builder.fromPort(from);
                    builder.toPort(to);
                });

        Optional.of(ipRanges)
                .ifPresent(ips -> {
                    IpRange[] ranges =
                            Arrays.stream(ips)
                            .map(ip -> IpRange.builder().cidrIp(ip).build())
                            .toArray(IpRange[]::new);
                    builder.ipRanges(ranges);
                });

        return builder.build();
    }

    public String getIpProtocol() {
        return ipProtocol;
    }

    public void setIpProtocol(String ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    public String getPortRange() {
        return portRange;
    }

    public void setPortRange(String portRange) {
        this.portRange = portRange;
    }

    public String[] getIpRanges() {
        return ipRanges;
    }

    @ArooaAttribute
    public void setIpRanges(String... ipRanges) {
        this.ipRanges = ipRanges;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", protocol " + ipProtocol + ", ports" + portRange ;
    }
}
