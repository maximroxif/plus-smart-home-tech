package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.PaymentState;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.OrderClient;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.shoppingStore.ShoppingStoreClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    @Qualifier("conversionService")
    private final ConversionService converter;

    private static final double VAT_RATE = 0.20;

    public Double productCost(OrderDto order) {
        log.info("Calculating product cost for order: {}", order.getOrderId());
        List<Double> pricesList = new ArrayList<>();
        Map<UUID, Integer> orderProducts = order.getProducts();

        orderProducts.forEach((id, quantity) -> {
            ProductDto product = shoppingStoreClient.getProduct(id);
            double totalProductPrice = product.getPrice() * quantity;
            pricesList.add(totalProductPrice);
        });

        double totalProductCost = pricesList.stream().mapToDouble(Double::doubleValue).sum();

        log.info("Product cost for order {}: {}", order.getOrderId(), totalProductCost);
        return totalProductCost;
    }

    public Double getTotalCost(OrderDto order) {
        log.info("Calculating total cost for order: {}", order.getOrderId());

        double productsPrice = order.getProductPrice();
        double deliveryPrice = order.getDeliveryPrice();
        double totalCost = deliveryPrice + productsPrice + (productsPrice * VAT_RATE);

        log.info("Total cost for order {}: {}", order.getOrderId(), totalCost);
        return totalCost;
    }

    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Creating payment for order: {}", orderDto.getOrderId());

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(productCost(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .totalPayment(getTotalCost(orderDto))
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        log.info("Payment with ID {} created for order {}", payment.getPaymentId(), orderDto.getOrderId());
        return converter.convert(payment, PaymentDto.class);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("Processing successful payment for payment ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.completed(payment.getOrderId());
        log.info("Payment with ID {} marked as successful", paymentId);
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("Processing payment failure for payment ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
        log.info("Payment with ID {} marked as failed", paymentId);
    }
}