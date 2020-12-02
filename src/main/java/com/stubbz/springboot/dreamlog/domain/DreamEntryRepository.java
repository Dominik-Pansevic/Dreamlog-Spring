package com.stubbz.springboot.dreamlog.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DreamEntryRepository extends CrudRepository<DreamEntry, Integer> {


    DreamEntry findDreamById (long id);
    List<DreamEntry> findDreamByAuthor (String author);
    List<DreamEntry> findDreamByAuthorAndTitle (String author, String title);
    List<DreamEntry> findDreamByAuthorAndType (String author, String type);
    List<DreamEntry> findDreamByVisibility (String visibility);
    List<DreamEntry> findDreamByVisibilityAndType (String visibility, String type);
    List<DreamEntry> findDreamByVisibilityAndTitle (String visibility, String title);
    List<DreamEntry> findDreamByVisibilityAndAuthor (String visibility, String author);
    void deleteById (long id);

}
