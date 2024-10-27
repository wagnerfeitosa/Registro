package dao;

import java.util.List;

import javax.persistence.EntityManager;

import db.DBhibernate;
import entities.Funcionario;

public class FuncionarioDao implements Dao<Funcionario> {

	private static FuncionarioDao instance;
	protected EntityManager entityManager;
	private Funcionario func;
	
	public static FuncionarioDao getInstance() {
		if(instance == null) {
			instance = new FuncionarioDao();
		}
		return instance;
	}
	
	private FuncionarioDao() {
		entityManager = DBhibernate.getEntityManager();
	}

	@Override
	public void insert(Funcionario obj) {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(obj);
			entityManager.getTransaction().commit();
			
		}catch(Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
		
	}

	@Override
	public void delete(Funcionario obj) {
		try {
			func = entityManager.find(Funcionario.class, obj.getId());
			if(func != null) {
				entityManager.getTransaction().begin();
				entityManager.remove(func);
				entityManager.getTransaction().commit();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
		
	}

	@Override
	public List<Funcionario> findAll() {
		return entityManager.createQuery("SELECT q FROM Funcionario q").getResultList();
	}

	@Override
	public Funcionario findById(Integer id) {
		
		return entityManager.find(Funcionario.class, id);
	}

	@Override
	public Funcionario findByEntity(Funcionario obj) {

		return entityManager.find(Funcionario.class, obj.getId());
	}

	@Override
	public void update(Funcionario obj) {
		try {
			func = entityManager.find(Funcionario.class, obj.getId());
			if(func != null) {
				func.setDepartamento(obj.getDepartamento());
				func.setNome(obj.getNome());
				func.setEmail(obj.getEmail());
				func.setNascimento(obj.getNascimento());
				func.setSalarioBase(obj.getSalarioBase());
				insert(func);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
	}

	
}
