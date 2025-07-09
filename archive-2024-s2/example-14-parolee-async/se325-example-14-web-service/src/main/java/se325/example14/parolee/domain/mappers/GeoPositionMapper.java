package se325.example14.parolee.domain.mappers;

import se325.example14.parolee.domain.GeoPosition;
import se325.example14.parolee.dto.GeoPositionDTO;

public class GeoPositionMapper {

    public static GeoPositionDTO toDTO(GeoPosition domain) {
        if (domain == null) return null;
        return new GeoPositionDTO(
                domain.getLatitude(),
                domain.getLongitude()
        );
    }

    public static GeoPosition toDomain(GeoPositionDTO dto) {
        if (dto == null) return null;
        return new GeoPosition(
                dto.getLatitude(),
                dto.getLongitude()
        );
    }
}
