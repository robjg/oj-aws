package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @oddjob.description Describe EC2 Instances.
 */
public class Ec2DescribeInstancesJob extends Ec2InstancesResponseBase {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DescribeInstancesJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The Ids of the Instances to describe.
     * @oddjob.required Yes, or the filters.
     */
    private String[] instanceIds;

    /**
     * @oddjob.property
     * @oddjob.description Filters to find instances to describe.
     * @oddjob.required Yes, or the Ids.
     */
    private Filter[] filters;

    /**
     * @oddjob.property
     * @oddjob.description Provided by the response. They contain instances.
     * @oddjob.required Read only.
     */
    private List<Reservation> reservations;

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

            List<Instance> instances = reservations.stream()
                    .filter(Reservation::hasInstances)
                    .flatMap(r -> r.instances().stream())
                    .collect(Collectors.toList());

            populateInstances(instances);

            logger.info("Received response for {} Instances", getSize());
        } else {
            logger.info("No Instances for search criteria");
        }
    }

    @Override
    protected void moreRest() {
        this.reservations = null;
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

}
