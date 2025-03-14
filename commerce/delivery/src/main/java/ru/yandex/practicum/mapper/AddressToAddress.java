package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.model.Address;

@Component
@RequiredArgsConstructor
public class AddressToAddress implements Converter<Address, AddressDto> {

    @Override
    public AddressDto convert(Address source) {
        return AddressDto.builder()
                .city(source.getCity())
                .house(source.getHouse())
                .country(source.getCountry())
                .flat(source.getFlat())
                .street(source.getStreet())
                .build();
    }
}
