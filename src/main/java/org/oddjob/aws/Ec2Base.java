package org.oddjob.aws;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.util.Optional;

abstract public class Ec2Base implements Runnable {

    private String name;

    @Override
    public final void run() {

        try (Ec2Client ec2 = Ec2Client.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
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

    @Override
    public String toString() {
        return Optional.ofNullable(this.name)
                .orElse(getClass().getSimpleName());
    }
}
