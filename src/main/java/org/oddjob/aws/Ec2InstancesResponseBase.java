package org.oddjob.aws;

import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Common functionality for jobs that return Instances as their response.
 */
abstract public class Ec2InstancesResponseBase extends Ec2Base {

    /**
     * @oddjob.property
     * @oddjob.description The Instance objects returned in the response.
     */
    private List<Instance> instances;

    /**
     * @oddjob.property
     * @oddjob.description Provide some details as a bean so they can be easily accessed in
     * expressions. The bean properties exposed from the response are currently {@code state},
     * {@code publicIpAddress} and {@code publicDnsName}.
     */
    private Map<String, Ec2DescribeInstancesJob.InstanceBean> detailById;

    /**
     * @oddjob.property
     * @oddjob.description The number of instances in the response.
     */
    private int size;

    /**
     * @oddjob.property
     * @oddjob.description The Instance Ids in the response.
     */
    private String[] responseInstanceIds;

    protected void populateInstances(List<Instance> instances) {
        Objects.requireNonNull(instances);

        this.instances = instances;

        this.size = instances.size();

        this.detailById = instances
                .stream()
                .collect(Collectors.toMap(Instance::instanceId,
                        InstanceBean::new));

        this.responseInstanceIds = instances
                .stream()
                .map(Instance::instanceId)
                .toArray(String[]::new);
    }

    protected void moreRest() {}

    @SoftReset
    @HardReset
    final public void reset() {
        this.instances = null;
        this.size = 0;
        this.responseInstanceIds = null;
        moreRest();
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public Ec2DescribeInstancesJob.InstanceBean getDetailById(String groupId) {
        return Optional.ofNullable(this.detailById)
                .map(d -> d.get(groupId))
                .orElse(null);
    }

    public int getSize() {
        return size;
    }

    public String[] getResponseInstanceIds() {
        return responseInstanceIds;
    }

    public static class InstanceBean {

        private final String state;

        private final String publicIpAddress;

        private final String publicDnsName;

        InstanceBean(Instance instance) {

            this.state = Optional.ofNullable(instance.state())
                    .map(InstanceState::nameAsString)
                    .orElse(null);
            this.publicIpAddress = instance.publicIpAddress();
            this.publicDnsName = instance.publicDnsName();

        }

        public String getState() {
            return state;
        }

        public String getPublicIpAddress() {
            return publicIpAddress;
        }

        public String getPublicDnsName() {
            return publicDnsName;
        }
    }
}
