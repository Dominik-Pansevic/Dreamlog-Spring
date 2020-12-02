package com.stubbz.springboot.dreamlog.service;

import com.stubbz.springboot.dreamlog.domain.DreamEntry;
import com.stubbz.springboot.dreamlog.domain.DreamEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DreamService {

    @Autowired
    private DreamEntryRepository dreamRepository;


    public List<DreamEntry> getOwnDreams(String user)
    {
        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByAuthor(user)
        .forEach(dreams::add);
        return dreams;

    }

    public List<DreamEntry> getAllDreams(String user)
    {
        List<DreamEntry> dreams = new ArrayList<>();
         dreamRepository.findAll()
                .forEach(dreams::add);
        return dreams;

    }
    public List<DreamEntry>  getOwnDreamsByTitle(String user, String title)
    {
        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByAuthorAndTitle(user, title)
                .forEach(dreams::add);
        return dreams;
    }

    public List<DreamEntry>  getOwnDreamsByType(String user, String type)
    {
        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByAuthorAndType(user, type)
                .forEach(dreams::add);
        return dreams;
    }

    public List<DreamEntry>  getPublicDreams()
    {

        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByVisibility("public")
                .forEach(dreams::add);

        return dreams;

    }

    public List<DreamEntry>  getPublicDreamsByType(String type)
    {

        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByVisibilityAndType("public", type)
                .forEach(dreams::add);

        return dreams;

    }

    public List<DreamEntry>  getPublicDreamsByTitle(String title)
    {

        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByVisibilityAndTitle("public", title)
                .forEach(dreams::add);

        return dreams;

    }

    public List<DreamEntry>  getPublicDreamsByAuthor(String author)
    {

        List<DreamEntry> dreams = new ArrayList<>();
        dreamRepository.findDreamByVisibilityAndAuthor("public", author)
                .forEach(dreams::add);

        return dreams;

    }

    public DreamEntry getDreamConverted(long id)
    {
        DreamEntry convertedDream = new DreamEntry();
        convertedDream.setDescription(dreamRepository.findDreamById(id).getDescription().replaceAll("(\r\n|\n)", "<br>"));
        convertedDream.setTitle(dreamRepository.findDreamById(id).getTitle());
        convertedDream.setType(dreamRepository.findDreamById(id).getType());
        convertedDream.setAuthor(dreamRepository.findDreamById(id).getAuthor());
        convertedDream.setId(id);
        convertedDream.setVisibility(dreamRepository.findDreamById(id).getVisibility());
       return convertedDream;
    }

    public DreamEntry getDream(long id)
    {
        return dreamRepository.findDreamById(id);
    }

    public void saveDream(DreamEntry dream)
    {
       dreamRepository.save(dream);
    }


    @Transactional
    public void deleteDream(long id) {

        dreamRepository.deleteById(id);
    }

    public DreamEntry trimInput(DreamEntry dream)
    {
        dream.setTitle(dream.getTitle().trim());
        dream.setType(dream.getType().trim());
        dream.setDescription(dream.getDescription().trim());

        return dream;
    }
}
