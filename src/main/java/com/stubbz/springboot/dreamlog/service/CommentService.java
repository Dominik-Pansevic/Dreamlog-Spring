package com.stubbz.springboot.dreamlog.service;


import com.stubbz.springboot.dreamlog.domain.CommentEntryRepository;
import com.stubbz.springboot.dreamlog.domain.CommentEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentEntryRepository commentRepository;


    public List<CommentEntry> getMyComments(String user)
    {
        List<CommentEntry> comments = new ArrayList<>();
        // commentRepository.findAll()
        commentRepository.findCommentByAuthor(user)
                .forEach(comments::add);
        return comments;
    }


    public CommentEntry getCommentById(long id)
    {
        // go through List and find an element with the given id
        return commentRepository.findCommentById(id);
    }

    public void saveComment(CommentEntry comment)
    {
        commentRepository.save(comment);
    }


    public void deleteComment(long id) {

        commentRepository.deleteById(id);
    }

    public List<CommentEntry> getDreamComments(long dreamId)
    {
        List<CommentEntry> comments = new ArrayList<>();
        // commentRepository.findAll()
        commentRepository.findCommentByConnectedDreamId(dreamId)
                .forEach(comments::add);

        return comments;
    }

    public List<CommentEntry> getDreamCommentsConverted(long dreamId)
    {
        List<CommentEntry> comments = new ArrayList<>();
        // commentRepository.findAll()
        commentRepository.findCommentByConnectedDreamId(dreamId)
                .forEach(comments::add);

        for (int i = 0; i < comments.size(); i++) {
            CommentEntry newEntry = comments.get(i);
            newEntry.setContent(newEntry.getContent().replaceAll("(\r\n|\n)", "<br>"));
            comments.set(i, newEntry);
        }
        return comments;
    }

    @Transactional
    public Long deleteCommentByDream(long connectedDreamId)
    {
        Long commentsFound = commentRepository.countByConnectedDreamId(connectedDreamId);
        commentRepository.deleteByConnectedDreamId(connectedDreamId);

        return commentsFound;
    }

    public CommentEntry trimInput(CommentEntry comment)
    {
        comment.setContent(comment.getContent().trim());

        return comment;
    }

}
