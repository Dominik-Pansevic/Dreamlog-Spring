package com.stubbz.springboot.dreamlog.controller;

import com.stubbz.springboot.dreamlog.domain.DreamEntry;
import com.stubbz.springboot.dreamlog.service.DreamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api")
@RestController
public class DreamController {

    @Autowired
    private DreamService dreamService;


    // SHOW ALL RECORDS
    @RequestMapping("/dreams")
    public List<DreamEntry> getAllDreams(Principal principal)
    {
        return dreamService.getAllDreams(principal.getName());
    }

    //SHOW AN EXISTING RECORD
    @RequestMapping("/dreams/{id}")
    public DreamEntry getDream(@PathVariable Integer id)
    {
        return dreamService.getDream(id);
    }

    // POST request // CREATE A NEW RECORD
    @RequestMapping(method = RequestMethod.POST, value="/dreams")
    public void addDream(@RequestBody DreamEntry dream)
    {
        dreamService.saveDream(dream);
    }

    //EDIT AN EXISTING RECORD
    @RequestMapping(method = RequestMethod.PUT, value="/dreams/{id}")
    public void updateDream(@RequestBody DreamEntry dream, @PathVariable Integer id)
    {
        dreamService.saveDream(dream);
    }

    //DELETE EXISTING RECORD
    @RequestMapping(method = RequestMethod.DELETE, value="/dreams/{id}")
    public void deleteDream(@PathVariable Integer id)
    {
        dreamService.deleteDream(id);
    }


}
