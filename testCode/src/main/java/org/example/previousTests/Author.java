package org.example.previousTests;

import org.example.previousTests.Title;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;


    @ManyToMany (mappedBy = "authors")
    private Set<Title> titles = new HashSet<>();
    public void setTitle(Title title) {
        titles.add(title);
    }
    public Set<Title> getTitles() {
        return titles;
    }

    public Author(String name) {
        setName(name);
    }

    public Author() {
        setName("Unknown");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
