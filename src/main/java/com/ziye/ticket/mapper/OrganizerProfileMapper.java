package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.OrganizerProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrganizerProfileMapper {
    
    @Select("SELECT * FROM organizer_profile WHERE organizer_id = #{organizerId}")
    OrganizerProfile findByOrganizerId(Long organizerId);
    
    @Insert("INSERT INTO organizer_profile (organizer_id, homepage_description, banner_image_url) VALUES (#{organizerId}, #{homepageDescription}, #{bannerImageUrl})")
    int insertProfile(OrganizerProfile profile);
    
    @Update("UPDATE organizer_profile SET homepage_description = #{homepageDescription}, banner_image_url = #{bannerImageUrl} WHERE organizer_id = #{organizerId}")
    int updateProfile(OrganizerProfile profile);
    
    @Delete("DELETE FROM organizer_profile WHERE organizer_id = #{organizerId}")
    int deleteProfile(Long organizerId);
} 