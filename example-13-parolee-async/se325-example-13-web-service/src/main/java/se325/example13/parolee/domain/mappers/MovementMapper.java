package se325.example13.parolee.domain.mappers;

import se325.example13.parolee.domain.Movement;
import se325.example13.parolee.dto.MovementDTO;

public class MovementMapper {

    public static MovementDTO toDTO(Movement domain) {
        if (domain == null) return null;
        return new MovementDTO(
                domain.getTimestamp(),
                GeoPositionMapper.toDTO(domain.getGeoPosition())
        );
    }

    public static Movement toDomain(MovementDTO dto) {
        if (dto == null) return null;
        return new Movement(
                dto.getTimestamp(),
                GeoPositionMapper.toDomain(dto.getPosition())
        );
    }
}
