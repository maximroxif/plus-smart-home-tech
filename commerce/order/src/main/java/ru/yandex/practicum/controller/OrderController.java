package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping
    public ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String username) {
        log.info("Fetching orders for client: {}", username);
        List<OrderDto> orders = orderService.getClientOrders(username);
        return ResponseEntity.ok(orders);
    }


    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(@Valid @RequestBody CreateNewOrderRequest createNewOrderRequest) {
        log.info("Creating new order: {}", createNewOrderRequest);
        OrderDto order = orderService.createNewOrder(createNewOrderRequest);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/return")
    public ResponseEntity<OrderDto> processProductReturn(@RequestBody ProductReturnRequest productReturnRequest) {
        log.info("Processing product return: {}", productReturnRequest);
        OrderDto order = orderService.productReturn(productReturnRequest);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/payment")
    public ResponseEntity<OrderDto> processPayment(@RequestParam UUID orderId) {
        log.info("Processing payment for order ID: {}", orderId);
        OrderDto order = orderService.payment(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> processPaymentFailed(@RequestParam UUID orderId) {
        log.info("Processing failed payment for order ID: {}", orderId);
        OrderDto order = orderService.paymentFailed(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> initiateDelivery(@RequestParam UUID orderId) {
        log.info("Initiating delivery for order ID: {}", orderId);
        OrderDto order = orderService.delivery(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> processDeliveryFailed(@RequestParam UUID orderId) {
        log.info("Processing failed delivery for order ID: {}", orderId);
        OrderDto order = orderService.deliveryFailed(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/completed")
    public ResponseEntity<OrderDto> markOrderCompleted(@RequestParam UUID orderId) {
        log.info("Marking order as completed for order ID: {}", orderId);
        OrderDto order = orderService.completed(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(@RequestParam UUID orderId) {
        log.info("Calculating total cost for order ID: {}", orderId);
        OrderDto order = orderService.calculateTotalCost(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(@RequestParam UUID orderId) {
        log.info("Calculating delivery cost for order ID: {}", orderId);
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> initiateAssembly(@RequestParam UUID orderId) {
        log.info("Initiating assembly for order ID: {}", orderId);
        OrderDto order = orderService.assembly(orderId);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> processAssemblyFailed(@RequestParam UUID orderId) {
        log.info("Processing failed assembly for order ID: {}", orderId);
        OrderDto order = orderService.assemblyFailed(orderId);
        return ResponseEntity.ok(order);
    }
}