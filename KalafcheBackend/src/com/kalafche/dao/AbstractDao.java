package com.kalafche.dao;
 
import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
 
public abstract class AbstractDao {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
 
    public void persist(Object entity) {
        getSession().persist(entity);
    }
 
    public void delete(Object entity) {
        getSession().delete(entity);
    }
    
    public Serializable save(Object entity) {
    	return getSession().save(entity);
    }
    
    public void update(Object entity) {
    	getSession().update(entity);
    }
    
    public void saveOrUpdate(Object entity) {
    	getSession().saveOrUpdate(entity);
    }
}