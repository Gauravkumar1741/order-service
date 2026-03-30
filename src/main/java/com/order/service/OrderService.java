package com.order.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.client.UserClient;
import com.order.dto.Product;
import com.order.entity.Order;
import com.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final UserClient userClient;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository repo,
                        UserClient userClient,
                        RestTemplate restTemplate) {
        this.repo = repo;
        this.userClient = userClient;
        this.restTemplate = restTemplate;
    }

    public Order placeOrder(Order order) {

        // Validate user
        userClient.getUser(order.getUserId());

        // Fetch product from Product Service
        Product product = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/api/products/" + order.getProductId(),
                Product.class);

        if (product == null)
            throw new RuntimeException("Product not found");

        if (product.getQuantity() < order.getQuantity())
            throw new RuntimeException("Insufficient stock");

        return repo.save(order);
    }
}