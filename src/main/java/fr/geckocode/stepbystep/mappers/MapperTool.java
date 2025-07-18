package fr.geckocode.stepbystep.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MapperTool {
    private static ModelMapper mapper = new ModelMapper();

    // DTO --> Entité (générique)
    public <D, E> E convertirDtoEnEntite(D dto, Class<E> entityClass) {
        return mapper.map(dto, entityClass);
    }

    // Entité --> DTO (générique)
    public <E, D> D convertirEntiteEnDto(E entity, Class<D> dtoClass) {
        return mapper.map(entity, dtoClass);
    }


}
