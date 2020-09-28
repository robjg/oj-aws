package org.oddjob.aws;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Ec2DescribeInstancesJob extends Ec2Base {

    private List<Reservation> reservations;

    private List<Instance> instances;

    private String[] instanceIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        DescribeInstancesRequest.Builder builder = DescribeInstancesRequest.builder();

        Optional.ofNullable(this.instanceIds)
                .ifPresent(ids -> builder.instanceIds(ids));

        DescribeInstancesRequest instancesRequest = builder.build() ;

        DescribeInstancesResponse response = ec2.describeInstances(instancesRequest);

        if (response.hasReservations()) {

            this.reservations = response.reservations();

            this.instances = reservations.stream()
                    .filter(r -> r.hasInstances())
                    .flatMap(r -> r.instances().stream())
                    .collect(Collectors.toList());
        }
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(String[] instanceIds) {
        this.instanceIds = instanceIds;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Instance> getInstances() {
        return instances;
    }
}
