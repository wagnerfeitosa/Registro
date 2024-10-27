package db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBhibernate {
	
	private static EntityManagerFactory emf;
	public static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<EntityManager>();
	
	public static EntityManagerFactory getEntityManagerFactory() {
		if(emf == null) {
			emf = Persistence.createEntityManagerFactory("ex");
		}
		return emf;
	}
	
	public static EntityManager getEntityManager() {
		EntityManager entityManager = threadLocal.get();
		if(entityManager ==  null) {
			entityManager = getEntityManagerFactory().createEntityManager();
			threadLocal.set(entityManager);
		}
		return entityManager;
	}

}
