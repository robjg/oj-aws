package org.oddjob.aws;

import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

abstract public class Ec2InstanceStateChangeBase extends Ec2Base {

    private List<InstanceStateChange> stateChanges;

    private Map<String, InstanceStateChangeBean> detailById;

    private String[] responseInstanceIds;

    private int size;


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

