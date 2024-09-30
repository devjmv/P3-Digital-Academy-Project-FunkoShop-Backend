package org.factoriaf5.digital_academy.funko_shop.product;

import java.util.List;

import org.factoriaf5.digital_academy.funko_shop.category.Category;
import org.factoriaf5.digital_academy.funko_shop.order_item.OrderItem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @NonNull
    private String name;
    @Column(name = "image")
    private String imageHash;
    @NonNull
    private String description;
    private float price;
    private int stock;
    @Column(name = "is_available", columnDefinition = "boolean default true")
    private boolean isAvailable;

    @Column(name = "is_new", columnDefinition = "boolean default true")
    private boolean isNew;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @Column(nullable = true)
    private int discount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public float getDiscountedPrice() {
        if (discount <= 0 || discount > 100) {
            return price;
        }

        float discountMultiplier = 1 - (discount / 100.0f);
        float discountedPrice = price * discountMultiplier;

        System.out.println("Applying discount for product: " + name +
                ". Original price: " + price +
                ", Discount percentage: " + discount +
                "%, Discounted price: " + discountedPrice);

        return discountedPrice;
    }

}
