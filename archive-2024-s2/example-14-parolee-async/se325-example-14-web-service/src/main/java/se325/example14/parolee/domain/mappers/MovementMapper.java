package se325.example14.parolee.domain.mappers;

import se325.example14.parolee.domain.Movement;
import se325.example14.parolee.dto.MovementDTO;

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
