package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @oddjob.description Create new EC2 Instances.
 *
 *
 */
public class Ec2RunInstancesJob extends Ec2InstancesResponseBase {

    private static final Logger logger = LoggerFactory.getLogger(Ec2RunInstancesJob.class);

    private String imageId;

    private String instanceType;

    private int minCount;

    private int maxCount;

    private String keyName;

    private String[] securityGroupIds;

    private String[] securityGroups;

    private Map<String, String> tags;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String imageId = Objects.requireNonNull(this.imageId, "No Image Id");

        String instanceType = Objects.requireNonNull(this.instanceType, "No Instance Type");

        int minCount = this.minCount;
        if (minCount == 0) {
            minCount = 1;
        }

        int maxCount = this.maxCount;
        if (maxCount == 0) {
            maxCount = 1;
        }

        RunInstancesRequest.Builder requestBuilder = RunInstancesRequest.builder()
                .imageId(imageId)
                .instanceType(instanceType)
                .minCount(minCount)
                .maxCount(maxCount);

        Optional.ofNullable(this.keyName)
                .ifPresent(requestBuilder::keyName);

        Optional.ofNullable(this.securityGroupIds)
                .ifPresent(requestBuilder::securityGroupIds);

        Optional.ofNullable(this.securityGroups)
                .ifPresent(requestBuilder::securityGroups);

        tagsFrom(this.tags, ResourceType.INSTANCE)
                .ifPresent(requestBuilder::tagSpecifications);

        RunInstancesRequest request =  requestBuilder.build();

        RunInstancesResponse response = ec2.runInstances(request);

        if (response.hasInstances()) {

            populateInstances(response.instances());

            logger.info("Received Run Instance Response for {} Instances", getSize());
        }
        else {
            logger.info("No Instances returned for request {}",
                    response.responseMetadata().requestId());
        }
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String[] getSecurityGroupIds() {
        return securityGroupIds;
    }

    @ArooaAttribute
    public void setSecurityGroupIds(String[] securityGroupIds) {
        this.securityGroupIds = securityGroupIds;
    }

    public String[] getSecurityGroups() {
        return securityGroups;
    }

    @ArooaAttribute
    public void setSecurityGroups(String[] securityGroups) {
        this.securityGroups = securityGroups;
    }

}
