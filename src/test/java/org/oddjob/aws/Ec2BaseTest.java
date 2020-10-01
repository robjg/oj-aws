package org.oddjob.aws;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class Ec2BaseTest {

    @Test
    public void testTagSpecification() {

        Map<String, String> tags = new HashMap<>();

        tags.put("fruit", "Apple");
        tags.put("name", "Alice");

        TagSpecification spec =  Ec2Base.tagsFrom(tags, ResourceType.INSTANCE)
                .orElseThrow(() -> new RuntimeException("Error"));

        assertThat(spec.hasTags(), CoreMatchers.is(true));
    }
}
