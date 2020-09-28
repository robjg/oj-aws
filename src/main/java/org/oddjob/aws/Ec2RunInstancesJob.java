package org.oddjob.aws;

import org.oddjob.util.OddjobUnexpectedException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.List;
import java.util.Objects;

public class Ec2RunInstancesJob extends Ec2Base {

    private String imageId;

    private String instanceType;

    private String instanceId;

    private int minCount;

    private int maxCount;


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

        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(imageId)
                .instanceType(instanceType)
                .minCount(minCount)
                .maxCount(maxCount)
                .build();

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

    public String getInstanceId() {
        return instanceId;
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
}
