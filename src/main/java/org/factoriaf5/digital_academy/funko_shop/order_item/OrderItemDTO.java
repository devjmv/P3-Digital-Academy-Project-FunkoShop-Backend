package org.factoriaf5.digital_academy.funko_shop.order_item;

import org.factoriaf5.digital_academy.funko_shop.order.OrderDTO;
import org.factoriaf5.digital_academy.funko_shop.product.ProductDTO;
import org.factoriaf5.digital_academy.funko_shop.review.ReviewDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private int quantity;
    private OrderDTO order;
    private ProductDTO product;
    private ReviewDTO review;

}
