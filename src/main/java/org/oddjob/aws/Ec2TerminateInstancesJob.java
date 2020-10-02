package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;

import java.util.Optional;

public class Ec2TerminateInstancesJob extends Ec2InstanceStateChangeBase {

    private static final Logger logger = LoggerFactory.getLogger(Ec2TerminateInstancesJob.class);

    private String[] instanceIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String[] instanceIds = Optional.ofNullable(this.instanceIds)
                .orElseThrow(() -> new IllegalArgumentException("No Instance Id"));

        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();

        TerminateInstancesResponse response = ec2.terminateInstances(request);

        if (response.hasTerminatingInstances()) {

            populateStateChanges(response.terminatingInstances());

            logger.info("Received Terminating response with {} State Changes",
                    getSize());
        }
        else {

            logger.info("Received Terminating response with no State Changes");
        }
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    @ArooaAttribute
    public void setInstanceIds(String... instanceIds) {
        this.instanceIds = instanceIds;
    }

}
