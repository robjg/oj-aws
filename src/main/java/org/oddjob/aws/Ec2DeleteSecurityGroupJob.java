package org.oddjob.aws;

import org.oddjob.util.OddjobConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupResponse;

import java.util.Optional;

/**
 * @oddjob.description Delete a Security Group.
 *
 */
public class Ec2DeleteSecurityGroupJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DeleteSecurityGroupJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The Id of the Security Group to be deleted.
     * @oddjob.required Yes, or the name.
     */
    private String groupId;

    /**
     * @oddjob.property
     * @oddjob.description The name of the Security Group to be deleted.
     * @oddjob.required Yes, or the Id.
     */
    private String groupName;

    @Override
    protected void withEc2(Ec2Client ec2) {

        DeleteSecurityGroupRequest.Builder requestBuilder = DeleteSecurityGroupRequest.builder();

        Optional.ofNullable(this.groupName).ifPresent(requestBuilder::groupName);

        Optional.ofNullable(this.groupId).ifPresent(requestBuilder::groupId);

        DeleteSecurityGroupRequest request = requestBuilder.build();

        if (request.groupName() == null && request.groupId() == null) {
            throw new OddjobConfigException("Either Security Group Name or Security Group Id must be provided.");
        }

        DeleteSecurityGroupResponse response = ec2.deleteSecurityGroup(request);

        logger.info("Successfully requested Security Group deletion, request Id {}",
                response.responseMetadata().requestId());
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
