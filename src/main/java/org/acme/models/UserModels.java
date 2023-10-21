package org.acme.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "user")
@Table(name = "users", indexes = {
        @Index(name = "idx_user", columnList = "id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_id", columnNames = {"id"})
})
public class UserModels extends PanacheEntityBase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "city")
    public String city;

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    //        public UserModels(long id, String name, String city) {
//        this.id = id;
//        this.name = name;
//        this.city = city;
//    }
}
