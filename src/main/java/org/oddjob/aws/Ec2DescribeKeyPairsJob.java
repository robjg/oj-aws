package org.oddjob.aws;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.KeyPairInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @oddjob.description Describe EC2 Key Pairs.
 */
public class Ec2DescribeKeyPairsJob extends Ec2Base {

    private static final Logger logger = LoggerFactory.getLogger(Ec2DescribeKeyPairsJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The Ids of the Key Pairs to describe.
     * @oddjob.required Yes, or names or filters.
     */
    private String[] keyPairIds;

    /**
     * @oddjob.property
     * @oddjob.description The Key Names of the Key Pairs to describe.
     * @oddjob.required Yes, or the Ids, or filters.
     */
    private String[] keyNames;

    /**
     * @oddjob.property
     * @oddjob.description Filters to find key pairs to describe.
     * @oddjob.required Yes, or the Ids or the names.
     */
    private Filter[] filters;

    /**
     * @oddjob.property
     * @oddjob.description The Key Pair Info objects from the response.
     * @oddjob.required Read Only.
     */
    private List<KeyPairInfo> keyPairs;

    /**
     * @oddjob.property
     * @oddjob.description Provide some details as a bean so they can be easily accessed in
     * expressions. The bean properties exposed from the response are currently {@code keyName},
     * and {@code keyFingerprint}.
     * @oddjob.required Read only.
     */
    private Map<String, KeyPairBean> detailById;

    /**
     * @oddjob.property
     * @oddjob.description The number of key pairs in the response.
     * @oddjob.required Read only.
     */
    private int size;

    /**
     * @oddjob.property
     * @oddjob.description The Key Pair Ids in the response.
     * @oddjob.required Read only.
     */
    private String[] responseKeyPairIds;

    @Override
    protected void withEc2(Ec2Client ec2) {

        logger.info("Making Describe Instances request");

        DescribeKeyPairsRequest.Builder builder = DescribeKeyPairsRequest.builder();

        Optional.ofNullable(this.keyPairIds)
                .ifPresent(builder::keyPairIds);

        Optional.ofNullable(this.keyNames)
                .ifPresent(builder::keyNames);

        Optional.ofNullable(this.filters)
                .ifPresent(builder::filters);

        DescribeKeyPairsRequest request = builder.build() ;

        logger.info("Requesting Instances for: {}", request);

        DescribeKeyPairsResponse response = ec2.describeKeyPairs(request);

        if (response.hasKeyPairs()) {

            this.keyPairs = response.keyPairs();

            this.size = keyPairs.size();

            this.detailById = response.keyPairs()
                    .stream()
                    .collect(Collectors.toMap(KeyPairInfo::keyPairId,
                            KeyPairBean::new));

            this.responseKeyPairIds = response.keyPairs()
                    .stream()
                    .map(KeyPairInfo::keyPairId)
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
        this.keyPairs = null;
        this.size = 0;
        this.responseKeyPairIds = null;
    }

    public String[] getKeyPairIds() {
        return keyPairIds;
    }

    @ArooaAttribute
    public void setKeyPairIds(String[] keyPairIds) {
        this.keyPairIds = keyPairIds;
    }

    public String[] getKeyNames() {
        return keyNames;
    }

    @ArooaAttribute
    public void setKeyNames(String[] keyNames) {
        this.keyNames = keyNames;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter... filters) {
        this.filters = filters;
    }

    public List<KeyPairInfo> getKeyPairs() {
        return keyPairs;
    }

    public KeyPairBean getDetailById(String groupId) {
        return Optional.ofNullable(this.detailById)
            .map(d -> d.get(groupId))
            .orElse(null);
    }

    public int getSize() {
        return size;
    }

    public String[] getResponseKeyPairIds() {
        return responseKeyPairIds;
    }

    public static class KeyPairBean {

        private final String keyName;

        private final String keyFingerprint;

        KeyPairBean(KeyPairInfo securityGroup) {
            this.keyName = securityGroup.keyName();
            this.keyFingerprint = securityGroup.keyFingerprint();
        }

        public String getKeyName() {
            return keyName;
        }

        public String getKeyFingerprint() {
            return keyFingerprint;
        }

        @Override
        public String toString() {
            return "KeyPairBean{" +
                    "keyName='" + keyName + '\'' +
                    '}';
        }
    }
}
