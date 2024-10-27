package dao;

import java.util.List;

public interface Dao<T> {
	
	void insert(T obj);
	void delete(T obj);
	List<T> findAll();
	T findById(Integer id);
	T findByEntity(T obj);
	
	void update(T obj);
	

}
