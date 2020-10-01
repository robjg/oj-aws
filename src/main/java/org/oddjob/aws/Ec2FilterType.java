package org.oddjob.aws;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.types.ValueFactory;
import software.amazon.awssdk.services.ec2.model.Filter;

import java.util.Optional;

/**
 * @oddjob.description Provide a filter for tags.
 */
public class Ec2FilterType implements ValueFactory<Filter> {

    private String name;

    private String[] values;

    @Override
    public Filter toValue() throws ArooaConversionException {
        String name = Optional.ofNullable(this.name)
                .orElseThrow(() -> new ArooaConversionException("No name"));

        String[] values = Optional.ofNullable(this.values)
                .orElseThrow(() -> new ArooaConversionException("No values"));

        return Filter.builder()
                .name(name)
                .values(values)
                .build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getValues() {
        return values;
    }

    @ArooaAttribute
    public void setValues(String... values) {
        this.values = values;
    }
}
