package com.ziye.ticket.service;

import com.ziye.ticket.entity.Follow;
import com.ziye.ticket.mapper.FollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FollowService {
    
    @Autowired
    private FollowMapper followMapper;
    
    public boolean addFollow(Long userId, Long organizerId) {
        // check if already followed
        Follow existingFollow = followMapper.findByUserIdAndOrganizerId(userId, organizerId);
        if (existingFollow != null) {
            return false; // already followed
        }
        
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setOrganizerId(organizerId);
        
        return followMapper.insertFollow(follow) > 0;
    }
    
    public boolean removeFollow(Long userId, Long organizerId) {
        return followMapper.deleteFollow(userId, organizerId) > 0;
    }
    
    public List<Follow> getUserFollows(Long userId) {
        return followMapper.findByUserIdWithDetails(userId);
    }
    
    public boolean isFollowing(Long userId, Long organizerId) {
        Follow follow = followMapper.findByUserIdAndOrganizerId(userId, organizerId);
        return follow != null;
    }
    
    public int getFollowerCount(Long organizerId) {
        return followMapper.countByOrganizerId(organizerId);
    }
} 