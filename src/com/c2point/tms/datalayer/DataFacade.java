package com.c2point.tms.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.application.TmsContextListener;
import com.c2point.tms.entity.AbstractPojo;

/**
 * Facade for generic database management. Contains general purpose methods for
 * ORM access. More specific methods should be in more detailed facade.
 * 
 */
public class DataFacade {

	private static Logger logger = LogManager.getLogger( DataFacade.class.getName());

	public DataFacade() {
	}

	/**
	 * Used for persisting new entity instances.
	 * 
	 * @param pojo
	 *            An entity instance that doesn't have an Id-value yet.
	 */
	public <T extends AbstractPojo> T insert(T pojo) {
		logger.debug( "Inserting new pojo:" + pojo.getClass().getName());
		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(pojo);
			em.getTransaction().commit();
			return pojo;
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * Used for updating existing entity instances.
	 * 
	 * @param pojo
	 *            An entity instance that has an Id-value (considered existent).
	 * @return
	 */
	public <T> T merge(T pojo) {
		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			T p = em.merge(pojo);
			em.flush();
			p = em.merge(p); // Related entities marked cascade-merge will
			// become merged too.
			em.getTransaction().commit();
			return p;
		} catch (RollbackException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * Finds stored entity-instances (from the database) based on the provided
	 * Id.
	 * 
	 * @param c
	 *            Defines the class of the instance to find
	 * @param id
	 *            The Id of the instance.
	 * @return If the instance was found, it's returned. If not, null returned.
	 */
	public <T> T find(Class<T> c, Object id) {
		EntityManager em = createEntityManager();
		try {
			return em.find(c, id);
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * USE WITH CAUSION. You should use AbstractPojo.find() unless your certain
	 * what your doing.
	 * 
	 * @param <T>
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractPojo> T refresh(T c) {
		if (c != null && c.getId() > 0 ) {
			EntityManager em = createEntityManager();
			try {
				em.getTransaction().begin();
				T y = (T) em.find(c.getClass(), c.getId());
				if (y != null) {
					em.refresh(y);
				}
				em.getTransaction().commit();
				return y;
			} catch (PersistenceException e) {
				throw e;
			} finally {
				em.close();
			}
		} else {
			return null;
		}
	}

	/**
	 * Removes a matching instance record from the database.
	 * 
	 * @param pojo
	 *            An entity instance whose Id should be defined.
	 */
	public void remove(AbstractPojo pojo) {
		if (pojo == null || pojo.getId() <= 0 ) {
			return;
		}

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(em.find(pojo.getClass(), pojo.getId()));
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * Provides a new EntityManager. Entity manager must be closed with .close()
	 * after use!
	 * 
	 * @return New Entitymanager
	 */
	public EntityManager createEntityManager() {
		EntityManagerFactory emf = TmsContextListener.getEntityManagerFactory();

		return emf.createEntityManager();
	}

	/**
	 * Get every stored entity in db. Same as SQL "select * from <T>";
	 * 
	 * @param <T>
	 *            What class to look for
	 * @param classOfPojo
	 * @return Collection of entities. Sorting may vary.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> list(Class<T> classOfPojo) {
		EntityManager em = createEntityManager();
		try {
			Query query = em.createQuery("SELECT A FROM " + classOfPojo.getSimpleName() + " A");
			return query.getResultList();
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	public static DataFacade getInstance() {
		// TeagleApplication a = TeagleApplication.getCurrent();
		// if (a == null) {
		return new DataFacade();
		// }
		// return TeagleApplication.getCurrent().getDataFacade();
	}

	/** Removes all entities in single transaction */
	public void removeAll(ArrayList<AbstractPojo> entities) {
		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			for (AbstractPojo p : entities) {
				p = em.find(p.getClass(), p.getId());
				em.remove(p);
			}
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	public void sortById(List<AbstractPojo> questions) {
		Collections.sort(questions, new Comparator<AbstractPojo>() {
			@Override
			public int compare(AbstractPojo arg0, AbstractPojo arg1) {
				String s = Long.toString( arg0.getId());
				String s2 = Long.toString( arg1.getId());

				if (s.indexOf("?") > -1) {
					s = s.substring(s.indexOf("?") + 1, s.length());
				}
				if (s2.indexOf("?") > -1) {
					s2 = s2.substring(s2.indexOf("?") + 1, s2.length());
				}
				try {
					Long l = Long.parseLong(s);
					Long l2 = Long.parseLong(s2);
					return l.compareTo(l2);
				} catch (NumberFormatException e) {
					return 0;
				}
			}

		});
	}

}
