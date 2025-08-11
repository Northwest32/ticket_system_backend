package com.ziye.ticket.service;

import com.ziye.ticket.dto.RefundRequestDto;
import com.ziye.ticket.dto.RefundApprovalDto;
import com.ziye.ticket.entity.RefundRequest;
import com.ziye.ticket.entity.Order;
import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.RefundRequestMapper;
import com.ziye.ticket.mapper.OrderMapper;
import com.ziye.ticket.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefundRequestService {
    
    @Autowired
    private RefundRequestMapper refundRequestMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private EventMapper eventMapper;
    
    @Transactional
    public RefundRequest createRefundRequest(RefundRequestDto dto, Long userId) {
        // check if order exists and belongs to the user
        Order order = orderMapper.findById(dto.getOrderId());
        if (order == null) {
            throw new IllegalStateException("Order not found");
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("Order does not belong to user");
        }
        
        // check if order status allows refund
        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalStateException("Order status does not allow refund");
        }
        
        // check if already applied for refund
        RefundRequest existing = refundRequestMapper.findByOrderId(dto.getOrderId());
        if (existing != null) {
            throw new IllegalStateException("Refund request already exists for this order");
        }
        
        // get event info to get organizer ID
        Event event = eventMapper.findById(order.getEventId());
        if (event == null) {
            throw new IllegalStateException("Event not found");
        }
        
        // create refund request
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(dto.getOrderId());
        refundRequest.setUserId(userId);
        refundRequest.setOrganizerId(event.getCreatedBy());
        refundRequest.setStatus("REQUESTED");
        refundRequest.setReason(dto.getReason());
        
        refundRequestMapper.insert(refundRequest);
        return refundRequest;
    }
    
    public RefundRequest getRefundRequestById(Long id) {
        return refundRequestMapper.findById(id);
    }
    
    public List<RefundRequest> getRefundRequestsByUserId(Long userId) {
        return refundRequestMapper.findByUserId(userId);
    }
    
    public List<RefundRequest> getRefundRequestsByOrganizerId(Long organizerId) {
        return refundRequestMapper.findByOrganizerId(organizerId);
    }
    
    @Transactional
    public RefundRequest approveRefundRequest(Long id, RefundApprovalDto dto, Long organizerId) {
        RefundRequest refundRequest = refundRequestMapper.findById(id);
        if (refundRequest == null) {
            throw new IllegalStateException("Refund request not found");
        }
        
        if (!refundRequest.getOrganizerId().equals(organizerId)) {
            throw new IllegalStateException("Not authorized to approve this refund request");
        }
        
        if (!"REQUESTED".equals(refundRequest.getStatus())) {
            throw new IllegalStateException("Refund request is not in REQUESTED status");
        }
        
        refundRequestMapper.updateStatus(id, "APPROVED", dto.getReply());
        
        // update order status to refunded
        orderMapper.updateStatus(refundRequest.getOrderId(), "REFUNDED");
        
        return refundRequestMapper.findById(id);
    }
    
    @Transactional
    public RefundRequest rejectRefundRequest(Long id, RefundApprovalDto dto, Long organizerId) {
        RefundRequest refundRequest = refundRequestMapper.findById(id);
        if (refundRequest == null) {
            throw new IllegalStateException("Refund request not found");
        }
        
        if (!refundRequest.getOrganizerId().equals(organizerId)) {
            throw new IllegalStateException("Not authorized to reject this refund request");
        }
        
        if (!"REQUESTED".equals(refundRequest.getStatus())) {
            throw new IllegalStateException("Refund request is not in REQUESTED status");
        }
        
        refundRequestMapper.updateStatus(id, "REJECTED", dto.getReply());
        
        return refundRequestMapper.findById(id);
    }
} 