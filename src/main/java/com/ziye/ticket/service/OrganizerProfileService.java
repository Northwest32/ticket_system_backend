package com.ziye.ticket.service;

import com.ziye.ticket.entity.OrganizerProfile;
import com.ziye.ticket.mapper.OrganizerProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizerProfileService {
    
    @Autowired
    private OrganizerProfileMapper organizerProfileMapper;
    
    public OrganizerProfile getProfileByOrganizerId(Long organizerId) {
        return organizerProfileMapper.findByOrganizerId(organizerId);
    }
    
    public boolean saveOrUpdateProfile(OrganizerProfile profile) {
        OrganizerProfile existingProfile = organizerProfileMapper.findByOrganizerId(profile.getOrganizerId());
        
        if (existingProfile != null) {
            // update existing record
            return organizerProfileMapper.updateProfile(profile) > 0;
        } else {
            // insert new record
            return organizerProfileMapper.insertProfile(profile) > 0;
        }
    }
    
    public boolean deleteProfile(Long organizerId) {
        return organizerProfileMapper.deleteProfile(organizerId) > 0;
    }
} 