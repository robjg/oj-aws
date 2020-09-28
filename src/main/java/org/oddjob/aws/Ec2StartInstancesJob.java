package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Ec2StartInstancesJob extends Ec2Base {

    private String[] instanceIds;

    private List<InstanceState> instanceStates;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String[] instanceIds = Optional.ofNullable(this.instanceIds)
                .orElseThrow(() -> new IllegalArgumentException("No Instance Ids"));

            StartInstancesRequest request = StartInstancesRequest.builder()
                    .instanceIds(instanceIds)
                    .build();

            StartInstancesResponse response = ec2.startInstances(request);

            if (response.hasStartingInstances()) {

                List<InstanceStateChange> stateChanges = response.startingInstances();

                this.instanceStates = stateChanges.stream()
                        .map(InstanceStateChange::currentState)
                        .collect(Collectors.toList());
            }
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    @ArooaAttribute
    public void setInstanceIds(String... instanceIds) {
        this.instanceIds = instanceIds;
    }

    public List<InstanceState> getInstanceStates() {
        return instanceStates;
    }
}
