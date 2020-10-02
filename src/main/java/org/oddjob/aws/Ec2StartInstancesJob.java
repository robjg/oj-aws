package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;

import java.util.Optional;

public class Ec2StartInstancesJob extends Ec2InstanceStateChangeBase {

    private static final Logger logger = LoggerFactory.getLogger(Ec2StartInstancesJob.class);

    private String[] instanceIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String[] instanceIds = Optional.ofNullable(this.instanceIds)
                .orElseThrow(() -> new IllegalArgumentException("No Instance Ids"));

            StartInstancesRequest request = StartInstancesRequest.builder()
                    .instanceIds(instanceIds)
                    .build();

            StartInstancesResponse response = ec2.startInstances(request);

            if (response.hasStartingInstances()) {

                populateStateChanges(response.startingInstances());

                logger.info("Received Starting response with {} State Changes",
                        getSize());
            }
            else {

                logger.info("Received Starting response with no State Changes");
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
