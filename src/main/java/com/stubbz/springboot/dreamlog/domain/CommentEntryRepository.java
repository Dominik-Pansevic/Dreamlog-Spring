package com.stubbz.springboot.dreamlog.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentEntryRepository extends CrudRepository<CommentEntry, Integer> {

    CommentEntry findCommentById (long id);
    List<CommentEntry> findCommentByAuthor (String author);
    List<CommentEntry> findCommentByConnectedDreamId (long connectedDreamId);
    Long deleteByConnectedDreamId(long connectedDreamId);
    Long countByConnectedDreamId(long connectedDreamId);
    void deleteById(long id);
}
