package com.example.meettify.entity.order;


import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.item.ItemEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString(exclude = {"item", "order"})
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "order_count")
    private int orderCount;

    @Column(name = "order_price")
    private int orderPrice;

    // 장바구니 상품 등록
    public static OrderItemEntity saveOrder(ItemEntity item,
                                            OrderEntity order,
                                            int count,
                                            int price) {
        return OrderItemEntity.builder()
                .item(item)
                .order(order)
                .orderCount(count)
                .orderPrice(price)
                .build();
    }

}
