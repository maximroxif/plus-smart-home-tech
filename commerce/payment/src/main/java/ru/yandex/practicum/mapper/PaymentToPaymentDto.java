package ru.yandex.practicum.mapper;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.model.Payment;

public class PaymentToPaymentDto implements Converter<Payment, PaymentDto> {

    @Override
    public PaymentDto convert(Payment source) {
        return PaymentDto.builder()
                .paymentId(source.getPaymentId())
                .feePayment(source.getProductTotal())
                .deliveryPayment(source.getDeliveryTotal())
                .totalPayment(source.getTotalPayment())
                .build();
    }
}
