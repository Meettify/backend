package com.example.meettify.entity.order;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.PayStatus;
import com.example.meettify.entity.member.AddressEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString(exclude = "orderItems")
public class OrderEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(name = "order_address")
    private AddressEntity address;

    @Column(name = "total_price")
    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // 주문 랜덤 번호
    private String orderUUIDid;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;


    public static OrderEntity saveOrder(MemberEntity member,
                                        AddressEntity address,
                                        int totalPrice,
                                        String orderUUIDid) {
        return OrderEntity.builder()
                .member(member)
                .address(address)
                .totalPrice(totalPrice)
                .orderUUIDid(orderUUIDid)
                .payStatus(PayStatus.PAY_X)
                .build();
    }

    public void changePayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }

    public void addTotalPrice(int totalPrice) {
        this.totalPrice += totalPrice;
    }
}
