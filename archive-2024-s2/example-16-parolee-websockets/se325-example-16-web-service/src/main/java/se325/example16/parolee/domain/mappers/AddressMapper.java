package se325.example16.parolee.domain.mappers;

import se325.example16.parolee.domain.Address;
import se325.example16.parolee.dto.AddressDTO;

public class AddressMapper {

    public static AddressDTO toDTO(Address domain) {
        return new AddressDTO(
                domain.getStreetNumber(),
                domain.getStreetName(),
                domain.getSuburb(),
                domain.getCity(),
                domain.getZipCode(),
                GeoPositionMapper.toDTO(domain.getLocation())
        );
    }

    public static Address toDomain(AddressDTO dto) {
        return new Address(
                dto.getStreetNumber(),
                dto.getStreetName(),
                dto.getSuburb(),
                dto.getCity(),
                dto.getZipCode(),
                GeoPositionMapper.toDomain(dto.getLocation())
        );
    }
}
