package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.models.UserModels;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModels>{

}
