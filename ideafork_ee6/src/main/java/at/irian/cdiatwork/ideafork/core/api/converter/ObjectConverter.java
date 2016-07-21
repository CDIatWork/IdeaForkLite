package at.irian.cdiatwork.ideafork.core.api.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectConverter {

    public String toString(Object entity) {
        return toString(entity, null);
    }

    public String toString(Object entity, Class typeSafeDataView) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (typeSafeDataView != null) {
                objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
                return objectMapper.writerWithView(typeSafeDataView).writeValueAsString(entity);
            }
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
