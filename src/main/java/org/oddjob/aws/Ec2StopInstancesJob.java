package org.oddjob.aws;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Ec2StopInstancesJob extends Ec2Base {

    private String[] instanceIds;

    private List<InstanceState> instanceStates;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String[] instanceIds = Optional.ofNullable(this.instanceIds)
                .orElseThrow(() -> new IllegalArgumentException("No Instance Id"));

        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();

        StopInstancesResponse response = ec2.stopInstances(request);

        if (response.hasStoppingInstances()) {
            List<InstanceStateChange> stateChanges = response.stoppingInstances();

            this.instanceStates = stateChanges.stream()
                    .map(InstanceStateChange::currentState)
                    .collect(Collectors.toList());
        }
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(String... instanceIds) {
        this.instanceIds = instanceIds;
    }

    public List<InstanceState> getInstanceStates() {
        return instanceStates;
    }
}
