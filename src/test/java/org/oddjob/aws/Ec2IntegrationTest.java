package org.oddjob.aws;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.oddjob.Oddjob;
import org.oddjob.state.StateConditions;
import org.oddjob.tools.StateSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Tag("IntegrationTest")
public class Ec2IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(Ec2IntegrationTest.class);

    @Test
    @EnabledIf("awsAvailable")
    public void testInstanceStartStop() throws InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(getClass().getResource("StartCopyToStopTest.xml").getFile()));

        StateSteps steps = new StateSteps(oddjob);
        steps.setTimeout(300_1000L);
        steps.startCheck(StateConditions.READY, StateConditions.EXECUTING,
                StateConditions.ACTIVE, StateConditions.COMPLETE);

        oddjob.run();

        steps.checkWait();
    }


    boolean awsAvailable() {

        Path path = Paths.get(System.getProperty("user.home"), ".aws", "credentials");
        return Files.exists(path);
    }
}
