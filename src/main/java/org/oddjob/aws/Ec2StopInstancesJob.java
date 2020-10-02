package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesResponse;

import java.util.Optional;

public class Ec2StopInstancesJob extends Ec2InstanceStateChangeBase {

    private static final Logger logger = LoggerFactory.getLogger(Ec2RunInstancesJob.class);

    private String[] instanceIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String[] instanceIds = Optional.ofNullable(this.instanceIds)
                .orElseThrow(() -> new IllegalArgumentException("No Instance Id"));

        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();

        StopInstancesResponse response = ec2.stopInstances(request);

        if (response.hasStoppingInstances()) {

            populateStateChanges(response.stoppingInstances());

            logger.info("Received Stop response with {} State Changes",
                    getSize());
        }
        else {

            logger.info("Received Stop response with no State Changes");
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
