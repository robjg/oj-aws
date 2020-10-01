package org.oddjob.aws;

import org.oddjob.util.OddjobConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;

import java.util.Optional;

public class Ec2DeleteKeyPairJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DeleteKeyPairJob.class);

    private String keyName;

    private String keyPairId;

    @Override
    protected void withEc2(Ec2Client ec2) {

        DeleteKeyPairRequest.Builder requestBuilder = DeleteKeyPairRequest.builder();

        Optional.ofNullable(this.keyName).ifPresent(requestBuilder::keyName);
        Optional.ofNullable(this.keyPairId).ifPresent(requestBuilder::keyPairId);

        DeleteKeyPairRequest request = requestBuilder.build();

        if (request.keyName() == null && request.keyPairId() == null) {
            throw new OddjobConfigException("Either Key Name or Key Pair Id must be provided.");
        }

        DeleteKeyPairResponse response = ec2.deleteKeyPair(request);

        logger.info(
                "Successfully requested Key Pair deletion, request Id {}",
                response.responseMetadata().requestId());
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }
}
