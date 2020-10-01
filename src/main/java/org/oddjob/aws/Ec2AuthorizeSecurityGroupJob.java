package org.oddjob.aws;

import org.oddjob.util.OddjobConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.IpPermission;

import java.util.Optional;


public class Ec2AuthorizeSecurityGroupJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2AuthorizeSecurityGroupJob.class);

    private String groupId;

    private String groupName;

    private IpPermission[] inboundPermissions;

    @Override
    protected void withEc2(Ec2Client ec2) {

        AuthorizeSecurityGroupIngressRequest.Builder requestBuilder =
                AuthorizeSecurityGroupIngressRequest.builder();

        Optional.ofNullable(this.groupName).ifPresent(requestBuilder::groupName);

        Optional.ofNullable(this.groupId).ifPresent(requestBuilder::groupId);

        Optional.ofNullable(this.inboundPermissions).ifPresent(requestBuilder::ipPermissions);

        AuthorizeSecurityGroupIngressRequest request = requestBuilder.build();

        if (request.groupName() == null && request.groupId() == null) {
            throw new OddjobConfigException("Either Security Group Name or Security Group Id must be provided.");
        }

        AuthorizeSecurityGroupIngressResponse response =
                ec2.authorizeSecurityGroupIngress(request);

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

    public IpPermission[] getInboundPermissions() {
        return inboundPermissions;
    }

    public void setInboundPermissions(IpPermission[] inboundPermissions) {
        this.inboundPermissions = inboundPermissions;
    }
}
