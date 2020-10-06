package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @oddjob.description Describe EC2 Security Groups.
 */
public class Ec2DescribeSecurityGroupsJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DescribeSecurityGroupsJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The Ids of the Security Groups to describe.
     * @oddjob.required Yes, or names or filters.
     */
    private String[] groupIds;

    /**
     * @oddjob.property
     * @oddjob.description The Group Names of the Security Groups to describe.
     * @oddjob.required Yes, or the Ids, or filters.
     */
    private String[] groupNames;

    /**
     * @oddjob.property
     * @oddjob.description Filters to find Security Groups to describe.
     * @oddjob.required Yes, or the Ids or the names.
     */
    private Filter[] filters;

    /**
     * @oddjob.property
     * @oddjob.description The Security Group objects from the response.
     * @oddjob.required Read Only.
     */
    private List<SecurityGroup> securityGroups;

    /**
     * @oddjob.property
     * @oddjob.description Provide some details as a bean so they can be easily accessed in
     * expressions. The bean properties exposed from the response are currently {@code groupName},
     * and {@code description}.
     * @oddjob.required Read only.
     */
    private Map<String, SecurityGroupBean> detailById;

    /**
     * @oddjob.property
     * @oddjob.description The number of security groups in the response.
     * @oddjob.required Read only.
     */
    private int size;

    /**
     * @oddjob.property
     * @oddjob.description The Group Ids in the response.
     * @oddjob.required Read only.
     */
    private String[] responseGroupIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        logger.info("Making Describe Instances request");

        DescribeSecurityGroupsRequest.Builder builder = DescribeSecurityGroupsRequest.builder();

        Optional.ofNullable(this.groupIds)
                .ifPresent(builder::groupIds);

        Optional.ofNullable(this.groupNames)
                .ifPresent(builder::groupNames);

        Optional.ofNullable(this.filters)
                .ifPresent(builder::filters);

        DescribeSecurityGroupsRequest request = builder.build() ;

        logger.info("Requesting Instances for: {}", request);

        DescribeSecurityGroupsResponse response = ec2.describeSecurityGroups(request);

        if (response.hasSecurityGroups()) {

            this.securityGroups = response.securityGroups();

            this.size = securityGroups.size();

            this.detailById = response.securityGroups()
                    .stream()
                    .collect(Collectors.toMap(SecurityGroup::groupId,
                            SecurityGroupBean::new));

            this.responseGroupIds = response.securityGroups()
                    .stream()
                    .map(SecurityGroup::groupId)
                    .toArray(String[]::new);


            logger.info("Received response for {} Security Groups", this.size);
        }
        else {

            logger.info("No Security Groups for search criteria");
        }
    }

    @SoftReset
    @HardReset
    public void reset() {
        this.detailById = null;
        this.securityGroups = null;
        this.size = 0;
        this.responseGroupIds = null;
    }

    public String[] getGroupIds() {
        return groupIds;
    }

    @ArooaAttribute
    public void setGroupIds(String[] groupIds) {
        this.groupIds = groupIds;
    }

    public String[] getGroupNames() {
        return groupNames;
    }

    @ArooaAttribute
    public void setGroupNames(String[] groupNames) {
        this.groupNames = groupNames;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter... filters) {
        this.filters = filters;
    }

    public List<SecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    public SecurityGroupBean getDetailById(String groupId) {
        return Optional.ofNullable(this.detailById)
            .map(d -> d.get(groupId))
            .orElse(null);
    }

    public int getSize() {
        return size;
    }

    public String[] getResponseGroupIds() {
        return responseGroupIds;
    }

    public static class SecurityGroupBean {

        private final String groupName;

        private final String description;

        private final String ownerId;

        private final String vpcId;

        SecurityGroupBean(SecurityGroup securityGroup) {
            this.groupName = securityGroup.groupName();
            this.description = securityGroup.description();
            this.ownerId = securityGroup.ownerId();
            this.vpcId = securityGroup.vpcId();
        }

        public String getGroupName() {
            return groupName;
        }

        public String getDescription() {
            return description;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getVpcId() {
            return vpcId;
        }

        @Override
        public String toString() {
            return "SecurityGroupBean{" +
                    "groupName='" + groupName + '\'' +
                    '}';
        }
    }
}
