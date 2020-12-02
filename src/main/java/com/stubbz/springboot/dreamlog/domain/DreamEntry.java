package com.stubbz.springboot.dreamlog.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "dreams")
public class DreamEntry {

    @Id
    @GeneratedValue(generator = "dream-generator")
    @GenericGenerator(
            name = "dream-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "dream_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            } )
    @Column(name = "dream_id")
    private long id;

    @NotBlank
    private String title;
    @NotBlank
    private String type;
    @Lob
    @NotBlank
    private String description;

    @Column(name = "dream_author")
    private String author;

    private String visibility;

    public DreamEntry()
    {

    }

//    public DreamEntry(Integer id, String title, String type, String description)
//    {
//        this.id = id;
//        this.title = title;
//        this.type = type;
//        this.description = description;
//
//    }


    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
