package dao;

import java.util.List;

import javax.persistence.EntityManager;

import db.DBhibernate;
import entities.Departamento;

public class DepartamentoDao implements Dao<Departamento> {

	private static DepartamentoDao instance;
	protected EntityManager entityManager;
	private Departamento dep;

	public static DepartamentoDao getInstace() {
		if (instance == null) {
			instance = new DepartamentoDao();
		}
		return instance;
	}

	private DepartamentoDao() {
		entityManager = DBhibernate.getEntityManager();
	}

	@Override
	public void insert(Departamento obj) {

		try {
			entityManager.getTransaction().begin();
			entityManager.persist(obj);
			entityManager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();

		}
	}

	@Override
	public void delete(Departamento obj) {
		try {
			dep = entityManager.find(Departamento.class, obj.getId());
			if (dep != null) {
				entityManager.getTransaction().begin();
				entityManager.remove(dep);
				entityManager.getTransaction().commit();
			}
			System.out.println("Departamento não existe no banco de dados");

		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}

	}

	@Override
	public List<Departamento> findAll() {
		// return em.createQuery("FROM " +
		// Departamento.class.getName()).getResultList();
		List<Departamento> lista = entityManager.createQuery("select q from Departamento q").getResultList();
		return lista;
	}

	@Override
	public Departamento findById(Integer id) {
		return entityManager.find(Departamento.class, id);

	}

	@Override
	public Departamento findByEntity(Departamento obj) {
		return entityManager.find(Departamento.class, obj.getId());
	}

	@Override
	public void update(Departamento obj) {
		try {
			System.out.println(obj.getId() + " "+ obj.getNome());
			dep = entityManager.find(Departamento.class, obj.getId());
    
			dep.setNome(obj.getNome());
			insert(dep);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
