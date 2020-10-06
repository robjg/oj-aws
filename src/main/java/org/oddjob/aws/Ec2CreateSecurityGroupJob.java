package org.oddjob.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.ResourceType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @oddjob.description Create an EC2 Security Group.
 *
 */
public class Ec2CreateSecurityGroupJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2CreateSecurityGroupJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The name to be given to the Security Group to be created.
     * @oddjob.required Yes.
     */
    private String groupName;

    /**
     * @oddjob.property
     * @oddjob.description A description for the Security Group.
     * @oddjob.required No.
     */
    private String description;

    /**
     * @oddjob.property
     * @oddjob.description The Id of a VPC (Virtual Private Cloud) to attach the group to.
     * @oddjob.required No.
     */
    private String vpcId;

    /**
     * @oddjob.property
     * @oddjob.description Tags to attach to the newly created group.
     * @oddjob.required No.
     */
    private Map<String, String> tags;

    /**
     * @oddjob.property
     * @oddjob.description The id of the group created.
     * @oddjob.required Read only.
     */
    private String groupId;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String groupName = Objects.requireNonNull(this.groupName);

        CreateSecurityGroupRequest.Builder requestBuilder = CreateSecurityGroupRequest.builder()
                .groupName(groupName);

        requestBuilder.description(
                Optional.ofNullable(this.description)
                        .orElse(groupName));

        Optional.ofNullable(this.vpcId)
                .ifPresent(requestBuilder::vpcId);

        tagsFrom(this.tags, ResourceType.SECURITY_GROUP)
                .ifPresent(requestBuilder::tagSpecifications);

        CreateSecurityGroupRequest request = requestBuilder.build();

        CreateSecurityGroupResponse response = ec2.createSecurityGroup(request);

        this.groupId = response.groupId();

        logger.info(
                "Successfully created Security Group {}, Id {}",
                groupName, this.groupId);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getGroupId() {
        return groupId;
    }

}
