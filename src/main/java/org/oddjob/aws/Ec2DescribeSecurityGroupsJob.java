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

    private String[] groupIds;

    private String[] groupNames;

    private Filter[] filters;

    private List<SecurityGroup> securityGroups;

    private Map<String, SecurityGroupBean> detailById;

    private int size;

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
