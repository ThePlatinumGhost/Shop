package com.azienda.shop.dao;

import com.azienda.shop.model.Author;
import com.azienda.shop.model.Product;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class AuthorDAO extends AbstractDAO<Author> {
    public AuthorDAO(EntityManager entityManager) {
        super(entityManager);
    }

//    public Author findById(int id) {
//        return entityManager.find(Author.class, id);
//    }
//
//    public List<Author> findAll() {
//        return entityManager.createQuery("from Author").getResultList();
//    }

    public Author findByName(String name) {
        try{
            return entityManager.createQuery("SELECT u FROM Author u WHERE u.name = :name", Author.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    protected List<Author> executeQuery(String query) {
        return List.of();
    }

    @Override
    public Class<Author> getEntityClass() {
        return Author.class;
    }

}
