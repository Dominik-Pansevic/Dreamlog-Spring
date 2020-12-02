package com.stubbz.springboot.dreamlog.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "comments")
public class CommentEntry {

    @Id
    @GeneratedValue(generator = "comment-generator")
    @GenericGenerator(
            name = "comment-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "comment_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            } )
    @Column(name = "comment_id")
    private long id;

    @Lob
    @NotBlank
    private String content;


    @Column(name = "comment_author")
    private String author;

    private long connectedDreamId;

    private Date entryDate;

    public CommentEntry()
    {

        this.entryDate = generateEntryDate();

    }

    private Date generateEntryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        return new Date(cal.getTime().getTime());
    }
    //    @NotEmpty
//    private ArrayList<User> upvotes;




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getConnectedDreamId() {
        return connectedDreamId;
    }

    public void setConnectedDreamId(long connectedDreamId) {
        this.connectedDreamId = connectedDreamId;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
}
