package ru.yandex.practicum.mapper;


import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;

public class OrderDtoToOrder implements Converter<OrderDto, Order> {

    @Override
    public Order convert(OrderDto source) {
        return Order.builder()
                .orderId(source.getOrderId())
                .shoppingCartId(source.getShoppingCartId())
                .products(source.getProducts())
                .paymentId(source.getPaymentId())
                .state(source.getState())
                .fragile(source.getFragile())
                .deliveryWeight(source.getDeliveryWeight())
                .deliveryId(source.getDeliveryId())
                .deliveryPrice(source.getDeliveryPrice())
                .deliveryVolume(source.getDeliveryVolume())
                .productPrice(source.getProductPrice())
                .totalPrice(source.getTotalPrice())
                .build();
    }
}
