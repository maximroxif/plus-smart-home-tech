package ru.yandex.practicum.service;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.DeliveryClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.DeliveryState;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.OrderState;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.payment.PaymentClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.WarehouseClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Qualifier("conversionService")
    private final ConversionService converter;

    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedException("Username must not be empty");
        }

        List<Order> orders = orderRepository.findByUsername(username);
        return orders.stream()
                .map(order -> converter.convert(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Creating new order");

        BookedProductDto bookedProducts = warehouseClient.checkShoppingCart(request.getShoppingCart());
        Order order = converter.convert(request, Order.class);
        Objects.requireNonNull(order).setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());
        order = orderRepository.save(order);

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        DeliveryDto newDelivery = DeliveryDto.builder()
                .fromAddress(warehouseAddress)
                .toAddress(request.getAddress())
                .orderId(order.getOrderId())
                .state(DeliveryState.CREATED)
                .build();
        newDelivery = deliveryClient.planDelivery(newDelivery);
        order.setDeliveryId(newDelivery.getDeliveryId());

        order = orderRepository.save(order);
        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Processing payment for order: {}", orderId);
        Order order = getOrderById(orderId);

        PaymentDto paymentDto = paymentClient.payment(converter.convert(order, OrderDto.class));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Processing failed payment for order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Initiating delivery for order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Processing failed delivery for order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto completed(UUID orderId) {
        log.info("Completing order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Calculating total cost for order: {}", orderId);
        Order order = getOrderById(orderId);

        Double totalPrice = paymentClient.getTotalCost(converter.convert(order, OrderDto.class));
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculating delivery cost for order: {}", orderId);
        Order order = getOrderById(orderId);

        Double deliveryPrice = deliveryClient.deliveryCost(converter.convert(order, OrderDto.class));
        order.setDeliveryPrice(deliveryPrice);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Processing assembly for order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Processing failed assembly for order: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Processing product return for order: {}", productReturnRequest.getOrderId());
        Order order = getOrderById(productReturnRequest.getOrderId());

        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
    }
}