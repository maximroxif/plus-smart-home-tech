package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.DeliveryState;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.OrderClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.WarehouseClient;

import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    @Qualifier("conversionService")
    private final ConversionService converter;

    private static final double BASE_RATE = 5.0;
    private static final double WAREHOUSE_1_ADDRESS_MULTIPLIER = 1.0;
    private static final double WAREHOUSE_2_ADDRESS_MULTIPLIER = 2.0;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double STREET_MULTIPLIER = 0.2;


    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Planning delivery: {}", deliveryDto);

        deliveryDto.setState(DeliveryState.CREATED);
        Delivery delivery = converter.convert(deliveryDto, Delivery.class);
        deliveryRepository.save(Objects.requireNonNull(delivery));

        log.info("Delivery planned successfully with ID: {}", delivery.getDeliveryId());
        return converter.convert(delivery, DeliveryDto.class);
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        log.info("Processing successful delivery for order ID: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(delivery.getOrderId());
        log.info("Delivery marked as successful for order ID: {}", orderId);
    }

    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        log.info("Processing delivery pickup for delivery ID: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery not found: " + deliveryId));

        delivery.setState(DeliveryState.IN_DELIVERY);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(deliveryId)
                .build();

        warehouseClient.shippedToDelivery(request);
        log.info("Delivery marked as picked up for delivery ID: {}", deliveryId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        log.info("Processing failed delivery attempt for order ID: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(orderId);
        log.info("Delivery attempt marked as failed for order ID: {}", orderId);
    }

    public Double deliveryCost(OrderDto orderDto) {
        log.info("Calculating delivery cost for order ID: {}", orderDto.getOrderId());

        Delivery delivery = getDelivery(orderDto.getDeliveryId());
        Address warehouseAddress = delivery.getFromAddress();
        Address destinationAddress = delivery.getToAddress();

        double totalCost = BASE_RATE;

        totalCost *= warehouseAddress.getCity().equals("ADDRESS_1")
                ? WAREHOUSE_1_ADDRESS_MULTIPLIER
                : WAREHOUSE_2_ADDRESS_MULTIPLIER;

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            totalCost += totalCost * FRAGILE_MULTIPLIER;
        }

        totalCost += orderDto.getDeliveryWeight() * WEIGHT_MULTIPLIER;
        totalCost += orderDto.getDeliveryVolume() * VOLUME_MULTIPLIER;

        if (!warehouseAddress.getStreet().equals(destinationAddress.getStreet())) {
            totalCost += totalCost * STREET_MULTIPLIER;
        }

        log.info("Delivery cost calculated for order ID {}: {}", orderDto.getOrderId(), totalCost);
        return totalCost;
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> {
                    log.warn("Delivery not found for cost calculation with ID: {}", deliveryId);
                    return new NotFoundException("Delivery not found: " + deliveryId);
                });
    }
}