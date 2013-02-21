package com.ushahidi.swiftriver.core.api.dao;


public interface GenericDao<T> {

	T create(T t);

    void delete(T t);

    T findById(Object id);

    T update(T t);   
}
