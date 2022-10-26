package com.zhihao.zhiyin.dto;


import com.zhihao.zhiyin.entity.OrderDetail;
import com.zhihao.zhiyin.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
