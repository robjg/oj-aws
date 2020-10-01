package org.oddjob.aws;

import org.oddjob.util.OddjobUnexpectedException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @oddjob.description
 */
public class Ec2RunInstancesJob extends Ec2Base {

    private String imageId;

    private String instanceType;

    private int minCount;

    private int maxCount;

    private Map<String, String> tags;

    private String instanceId;


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

        tagsFrom(this.tags, ResourceType.KEY_PAIR)
                .ifPresent(requestBuilder::tagSpecifications);

        RunInstancesRequest request =  requestBuilder.build();

        RunInstancesResponse response = ec2.runInstances(request);

        List<Instance> instances = response.instances();
        if (instances == null || instances.size() != 1) {
            throw new OddjobUnexpectedException("Instances not as expected: " + instances);
        }

        this.instanceId = instances.get(0).instanceId();
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

    public String getInstanceId() {
        return instanceId;
    }

}
