package com.ziye.ticket.service;

import com.ziye.ticket.entity.Comment;
import com.ziye.ticket.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper mapper;

    @Transactional
    public Comment add(Comment c){
        mapper.insert(c);
        return c;
    }

    public List<Comment> topByEvent(Long eid){return mapper.findTopByEvent(eid);}  //先查顶级评论，用户展开评论时再按需请求回复（懒加载）
    public List<Comment> topByOrganizer(Long oid){return mapper.findTopByOrganizer(oid);}    
    public List<Comment> replies(Long cid){return mapper.findReplies(cid);}    
    public List<Comment> received(Long uid){return mapper.findReceived(uid);}    
    public List<Comment> given(Long uid){return mapper.findGiven(uid);}    
    
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        // check if comment exists and belongs to the user
        Comment comment = mapper.findById(commentId);
        if (comment == null) {
            return false; // comment does not exist
        }
        
        if (!comment.getFromUserId().equals(userId)) {
            return false; // comment does not belong to the user
        }
        
        // delete all replies of the comment
        mapper.deleteRepliesByParentId(commentId);
        
        // delete the comment itself
        int result = mapper.deleteByIdAndUserId(commentId, userId);
        return result > 0;
    }
} 