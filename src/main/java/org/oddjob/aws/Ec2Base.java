package org.oddjob.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for EC2 Jobs
 */
abstract public class Ec2Base implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Ec2Base.class);

    /**
     * @oddjob.property
     * @oddjob.description The name of the job. Can be any text.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.property
     * @oddjob.description A Credential Provider. Defaults to the Profile Credential Provider using
     * the default credential file as specified in the AWS SDK Guide
     * <a href=https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html>here</a>.
     * @oddjob.required No.
     */
    private AwsCredentialsProvider credentialsProvider;

    @Override
    public final void run() {

        AwsCredentialsProvider credentialsProvider = Optional.ofNullable(this.credentialsProvider)
                .orElseGet(() -> ProfileCredentialsProvider.create());

        logger.info("Creating EC2 Client with credentials : {}", credentialsProvider);

        try (Ec2Client ec2 = Ec2Client.builder()
                .credentialsProvider(credentialsProvider)
                .build()) {

            withEc2(ec2);
        }
    }

    abstract protected void withEc2(Ec2Client ec2);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AwsCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(AwsCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public static Optional<TagSpecification> tagsFrom(Map<String, String> tagMap,
                                                      ResourceType resourceType) {
        Objects.requireNonNull(resourceType);

        return Optional.ofNullable(tagMap)
                .map(tm -> {
                    Tag[] ta = tm.entrySet().stream()
                            .map(e -> Tag.builder()
                                    .key(e.getKey())
                                    .value(e.getValue())
                                    .build())
                            .toArray(Tag[]::new);

                    return TagSpecification.builder()
                            .resourceType(resourceType)
                            .tags(ta)
                            .build();
                });
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.name)
                .orElse(getClass().getSimpleName());
    }
}
