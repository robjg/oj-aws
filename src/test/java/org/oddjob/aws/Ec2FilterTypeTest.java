package org.oddjob.aws;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import software.amazon.awssdk.services.ec2.model.Filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class Ec2FilterTypeTest {

    @Test
    public void testToFilter() throws ArooaConversionException {

        Ec2FilterType test = new Ec2FilterType();
        test.setName("tag-values");
        test.setValues("Test","Foo");

        Filter result = test.toValue();

        assertThat(result.name(), is("tag-values"));
        assertThat(result.values(), contains("Test","Foo"));
    }
}