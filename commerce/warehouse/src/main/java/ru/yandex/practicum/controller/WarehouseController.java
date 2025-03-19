package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PutMapping
    public ResponseEntity<Void> addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        log.info("Adding new product to warehouse: {}", request);
        warehouseService.addNewProduct(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> acceptReturn(@RequestBody Map<UUID, Integer> products) {
        log.info("Accepting return of products: {}", products);
        warehouseService.acceptReturn(products);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/booking")
    public ResponseEntity<BookedProductDto> bookProductForShoppingCart(@RequestBody @Valid ShoppingCartDto cartDto) {
        log.info("Booking products for shopping cart: {}", cartDto);
        BookedProductDto bookedProduct = warehouseService.bookProductForShoppingCart(cartDto);
        return ResponseEntity.ok(bookedProduct);
    }

    @PostMapping("/assembly")
    public ResponseEntity<BookedProductDto> assemblyProductForOrderFromShoppingCart(
            @RequestBody @Valid AssemblyProductForOrderFromShoppingCartRequest request) {
        log.info("Assembling products for order from shopping cart: {}", request);
        BookedProductDto assembledProduct = warehouseService.assemblyProductForOrderFromShoppingCart(request);
        return ResponseEntity.ok(assembledProduct);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request) {
        log.info("Adding product quantity to warehouse: {}", request);
        warehouseService.addProductQuantity(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("Fetching warehouse address");
        AddressDto address = warehouseService.getWarehouseAddress();
        return ResponseEntity.ok(address);
    }
}