package com.example.meettify.entity.order;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.member.AddressDTO;
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

    public static OrderEntity saveOrder(MemberEntity member, AddressDTO address, int totalPrice) {
        AddressEntity addressEntity = AddressEntity.changeEntity(address);
        return OrderEntity.builder()
                .member(member)
                .address(addressEntity)
                .totalPrice(totalPrice)
                .build();
    }

    public void addTotalPrice(int totalPrice) {
        this.totalPrice += totalPrice;
    }
}
