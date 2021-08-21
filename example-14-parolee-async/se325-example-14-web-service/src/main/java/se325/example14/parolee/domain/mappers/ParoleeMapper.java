package se325.example14.parolee.domain.mappers;

import se325.example14.parolee.domain.Parolee;
import se325.example14.parolee.dto.ParoleeDTO;

public class ParoleeMapper {

    public static ParoleeDTO toDTO(Parolee domain) {
        ParoleeDTO dtoParolee = new ParoleeDTO();
        dtoParolee.setId(domain.getId());
        dtoParolee.setDateOfBirth(domain.getDateOfBirth());
        dtoParolee.setGender(domain.getGender());
        dtoParolee.setFirstName(domain.getFirstName());
        dtoParolee.setLastName(domain.getLastName());
        dtoParolee.setHomeAddress(AddressMapper.toDTO(domain.getHomeAddress()));
        dtoParolee.setLastKnownPosition(MovementMapper.toDTO(domain.getLastKnownPosition()));

        return dtoParolee;
    }

    public static Parolee toDomain(ParoleeDTO dto) {
        Parolee domainParolee = new Parolee();
        updateDomain(domainParolee, dto);
        return domainParolee;
    }

    public static void updateDomain(Parolee domain, ParoleeDTO dto) {
        domain.setId(dto.getId());
        domain.setDateOfBirth(dto.getDateOfBirth());
        domain.setGender(dto.getGender());
        domain.setFirstName(dto.getFirstName());
        domain.setLastName(dto.getLastName());
        domain.setHomeAddress(AddressMapper.toDomain(dto.getHomeAddress()));
    }
}
