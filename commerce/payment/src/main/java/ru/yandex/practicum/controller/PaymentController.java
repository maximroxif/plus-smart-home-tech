package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody OrderDto orderDto) {
        log.info("Creating payment for order: {}", orderDto);
        PaymentDto payment = paymentService.createPayment(orderDto);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/totalCost")
    public ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto) {
        log.info("Calculating total cost for order: {}", orderDto);
        Double totalCost = paymentService.getTotalCost(orderDto);
        return ResponseEntity.ok(totalCost);
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> processPaymentSuccess(@RequestParam UUID paymentId) {
        log.info("Processing successful payment with ID: {}", paymentId);
        paymentService.paymentSuccess(paymentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/productCost")
    public ResponseEntity<Double> calculateProductCost(@RequestBody OrderDto orderDto) {
        log.info("Calculating product cost for order: {}", orderDto);
        Double productCost = paymentService.productCost(orderDto);
        return ResponseEntity.ok(productCost);
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> processPaymentFailed(@RequestParam UUID paymentId) {
        log.info("Processing payment failure with ID: {}", paymentId);
        paymentService.paymentFailed(paymentId);
        return ResponseEntity.noContent().build();
    }
}