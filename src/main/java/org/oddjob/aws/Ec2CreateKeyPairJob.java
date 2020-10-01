package org.oddjob.aws;

import org.oddjob.arooa.utils.IoUtils;
import org.oddjob.util.OddjobWrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.ResourceType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Ec2CreateKeyPairJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2CreateKeyPairJob.class);

    private String keyName;

    private OutputStream output;

    private Map<String, String> tags;

    private String keyPairId;

    private String fingerprint;

    @Override
    protected void withEc2(Ec2Client ec2) {

        String keyName = Objects.requireNonNull(this.keyName);

        CreateKeyPairRequest.Builder requestBuilder = CreateKeyPairRequest.builder()
                .keyName(keyName);

        tagsFrom(this.tags, ResourceType.KEY_PAIR)
                .ifPresent(requestBuilder::tagSpecifications);

        CreateKeyPairRequest request = requestBuilder.build();

        try {
            CreateKeyPairResponse response = ec2.createKeyPair(request);

            this.keyPairId = response.keyPairId();
            this.fingerprint = response.keyFingerprint();

            Optional.ofNullable(this.output)
                    .ifPresent(out -> {
                        try {
                            IoUtils.write(response.keyMaterial(), out);
                        } catch (IOException e) {
                            throw new OddjobWrapperException(e);
                        }
                    });
        }
        finally {
            Optional.ofNullable(this.output)
                    .ifPresent(out -> {
                        try {
                            out.close();
                        } catch (IOException e) {
                            logger.warn("Failed closing " + out, e);
                        }
                    });
        }

        logger.info(
                "Successfully created Key Pair {}, Id {}, Fingerprint: {}",
                keyName, this.keyPairId, this.fingerprint);
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public String getFingerprint() {
        return fingerprint;
    }
}
