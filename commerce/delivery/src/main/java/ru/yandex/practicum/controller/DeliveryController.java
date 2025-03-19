package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("Received request to plan a new delivery: {}", deliveryDto);
        DeliveryDto plannedDelivery = deliveryService.planDelivery(deliveryDto);
        return ResponseEntity.ok(plannedDelivery);
    }

    @PostMapping("/successful")
    public ResponseEntity<Void> markDeliverySuccessful(@RequestParam UUID orderId) {
        log.info("Marking delivery as successful for order ID: {}", orderId);
        deliveryService.deliverySuccessful(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/picked")
    public ResponseEntity<Void> markDeliveryPicked(@RequestParam UUID deliveryId) {
        log.info("Marking delivery as picked up for delivery ID: {}", deliveryId);
        deliveryService.deliveryPicked(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> markDeliveryFailed(@RequestParam UUID orderId) {
        log.info("Marking delivery as failed for order ID: {}", orderId);
        deliveryService.deliveryFailed(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cost")
    public ResponseEntity<Double> calculateDeliveryCost(@RequestBody OrderDto orderDto) {
        log.info("Calculating delivery cost for order ID: {}", orderDto.getOrderId());
        Double cost = deliveryService.deliveryCost(orderDto);
        return ResponseEntity.ok(cost);
    }
}