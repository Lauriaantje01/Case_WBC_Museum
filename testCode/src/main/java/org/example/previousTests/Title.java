package org.example.previousTests;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Title {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "title_id")
    @SequenceGenerator(sequenceName = "title_id", name = "title_id")
    private Long id;
    private String name;

    public Title() {
        setName("Unknown");
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @ManyToMany
//    @JoinTable(
//            name = "tile_author_mm_uni",
//            joinColumns = {@JoinColumn(name = "title_id")},
//            inverseJoinColumns = {@JoinColumn(name = "author_id")}
//    )
    private Set<Author> authors = new HashSet<>();

    public Title(String name) {
        setName(name);
    }

    //    public void addAuthor(Author author) {
//        authors.add(author);
//        if (author.getTitles() !=null && !author.getTitles().contains(this)){
//            author.getTitles().add(this);
//        };
//    }
    public void addAuthor(Author author) {
        authors.add(author);
        author.setTitle((this));
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
