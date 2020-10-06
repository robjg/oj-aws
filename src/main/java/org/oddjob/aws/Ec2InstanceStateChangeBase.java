package org.oddjob.aws;

import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Common functionality for jobs that change the state of an instance and must process an
 * Instance State Change response.
 */
abstract public class Ec2InstanceStateChangeBase extends Ec2Base {

    /**
     * @oddjob.property
     * @oddjob.description The Instance State Change objects returned in the response.
     * @oddjob.required Read only.
     */
    private List<InstanceStateChange> stateChanges;

    /**
     * @oddjob.property
     * @oddjob.description Provide some details as a bean so they can be easily accessed in
     * expressions. The bean properties exposed from the response are currently
     * {@code previousState} and {@code currentState}.
     * @oddjob.required Read only.
     */
    private Map<String, InstanceStateChangeBean> detailById;

    /**
     * @oddjob.property
     * @oddjob.description The number of Instance State Changes in the response.
     * @oddjob.required Read only.
     */
    private int size;

    /**
     * @oddjob.property
     * @oddjob.description The Instance Ids in the response.
     * @oddjob.required Read only.
     */
    private String[] responseInstanceIds;


    protected void populateStateChanges(List<InstanceStateChange> stateChanges) {

        Objects.requireNonNull(stateChanges);

        this.stateChanges = stateChanges;

        this.size = stateChanges.size();

        this.detailById = stateChanges
                .stream()
                .collect(Collectors.toMap(InstanceStateChange::instanceId,
                        InstanceStateChangeBean::new));

        this.responseInstanceIds = stateChanges
                .stream()
                .map(InstanceStateChange::instanceId)
                .toArray(String[]::new);

    }

    protected void moreRest() {}

    @SoftReset
    @HardReset
    final public void reset() {
        this.stateChanges = null;
        this.detailById = null;
        this.responseInstanceIds = null;
        this.size = 0;
        moreRest();
    }

    public List<InstanceStateChange> getStateChanges() {
        return stateChanges;
    }

    public Ec2InstanceStateChangeBase.InstanceStateChangeBean getDetailById(String groupId) {
        return Optional.ofNullable(this.detailById)
                .map(d -> d.get(groupId))
                .orElse(null);    }

    public String[] getResponseInstanceIds() {
        return responseInstanceIds;
    }

    public int getSize() {
        return size;
    }

    public static class InstanceStateChangeBean {

        private final String previousState;

        private final String currentState;

        InstanceStateChangeBean(InstanceStateChange stateChange) {

            this.previousState = stateChange.previousState().nameAsString();
            this.currentState = stateChange.currentState().nameAsString();
        }

        public String getPreviousState() {
            return previousState;
        }

        public String getCurrentState() {
            return currentState;
        }
    }
}

