package se325.example14.parolee.domain.mappers;

import se325.example14.parolee.domain.Conviction;
import se325.example14.parolee.dto.ConvictionDTO;

public class ConvictionMapper {

    public static ConvictionDTO toDTO(Conviction domain) {
        return new ConvictionDTO(
                domain.getDate(),
                domain.getDescription(),
                domain.getOffence()
        );
    }

    public static Conviction toDomain(ConvictionDTO dto) {
        return new Conviction(
                dto.getDate(),
                dto.getDescription(),
                dto.getOffence()
        );
    }
}
