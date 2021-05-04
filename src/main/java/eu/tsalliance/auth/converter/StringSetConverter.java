package eu.tsalliance.auth.converter;

import javax.persistence.AttributeConverter;
import java.util.*;
import java.util.stream.Collectors;

public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        return String.join(";", Optional.ofNullable(strings).orElse(Collections.emptySet()));
    }

    @Override
    public Set<String> convertToEntityAttribute(String value) {
        return Arrays.stream(Optional.ofNullable(value).orElse("").split(";")).collect(Collectors.toSet());
    }
}
