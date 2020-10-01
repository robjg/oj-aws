package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @oddjob.description Describe EC2 Instances.
 */
public class Ec2DescribeInstancesJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DescribeInstancesJob.class);

    private String[] instanceIds;

    private Filter[] filters;

    private List<Reservation> reservations;

    private List<Instance> instances;

    private Map<String, InstanceBean> detailById;

    private int size;

    private String[] responseInstanceIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        logger.info("Making Describe Instances request");

        DescribeInstancesRequest.Builder builder = DescribeInstancesRequest.builder();

        Optional.ofNullable(this.instanceIds)
                .ifPresent(builder::instanceIds);

        Optional.ofNullable(this.filters)
                .ifPresent(builder::filters);

        DescribeInstancesRequest request = builder.build();

        logger.info("Requesting Instances for: {}", request);

        DescribeInstancesResponse response = ec2.describeInstances(request);

        if (response.hasReservations()) {

            this.reservations = response.reservations();

            this.instances = reservations.stream()
                    .filter(Reservation::hasInstances)
                    .flatMap(r -> r.instances().stream())
                    .collect(Collectors.toList());

            this.size = instances.size();

            this.detailById = this.instances
                    .stream()
                    .collect(Collectors.toMap(Instance::instanceId,
                            InstanceBean::new));

            this.responseInstanceIds = this.instances
                    .stream()
                    .map(Instance::instanceId)
                    .toArray(String[]::new);

            logger.info("Received response for {} Instances", this.size);
        } else {
            logger.info("No Instances for search criteria");
        }
    }

    @SoftReset
    @HardReset
    public void reset() {
        this.reservations = null;
        this.instances = null;
        this.size = 0;
        this.responseInstanceIds = null;
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    @ArooaAttribute
    public void setInstanceIds(String[] instanceIds) {
        this.instanceIds = instanceIds;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter... filters) {
        this.filters = filters;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public InstanceBean getDetailById(String groupId) {
        return Optional.ofNullable(this.detailById)
                .map(d -> d.get(groupId))
                .orElse(null);
    }

    public int getSize() {
        return size;
    }

    public String[] getResponseInstanceIds() {
        return responseInstanceIds;
    }

    public static class InstanceBean {

        private final String state;

        private final String publicIpAddress;

        private final String publicDnsName;

        InstanceBean(Instance instance) {

            this.state = Optional.ofNullable(instance.state())
                    .map(InstanceState::nameAsString)
                    .orElse(null);
            this.publicIpAddress = instance.publicIpAddress();
            this.publicDnsName = instance.publicDnsName();

        }

        public String getState() {
            return state;
        }

        public String getPublicIpAddress() {
            return publicIpAddress;
        }

        public String getPublicDnsName() {
            return publicDnsName;
        }
    }
}
