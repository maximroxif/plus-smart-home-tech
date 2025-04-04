package ru.yandex.practicum.warehouse;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

@Component
@FeignClient(name = "warehouse-service", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductDto checkShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/booking")
    BookedProductDto bookProducts(@RequestBody ShoppingCartDto shoppingCart) throws FeignException;

    @PostMapping("/assembly")
    BookedProductDto assemblyProductForOrderFromShoppingCart(
            @RequestBody AssemblyProductForOrderFromShoppingCartRequest request) throws FeignException;

    @PostMapping("shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getWarehouseAddress() throws FeignException;
}
