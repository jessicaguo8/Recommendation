package mars.dao;
import java.util.HashSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import mars.entity.Item;
import mars.hibernate.CategoryId;
import mars.hibernate.PersistentCategories;
import mars.hibernate.PersistentItem;

@Repository
public class MysqlDao extends AbstractDaoImplementation {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public void saveItem(Item item) {
		Session session = null;
		PersistentItem persistentItem = convert(item);

		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(persistentItem);
			session.getTransaction().commit();
		} catch (HibernateException ex) {
			if (session != null && session.getTransaction() != null) {
				session.getTransaction().rollback();
			}

			if (ex instanceof ConstraintViolationException) {
				System.out.println("item id " + item.getItemId() + " already exists");
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		System.out.println("insert into db successfully");
	}
	private PersistentItem convert(Item item) {
		PersistentItem persistentItem = new PersistentItem();
		persistentItem.setItemId(item.getItemId());
		persistentItem.setName(item.getName());
		persistentItem.setRating(item.getRating());
		persistentItem.setAddress(item.getAddress());
		persistentItem.setImageUrl(item.getImageUrl());
		persistentItem.setUrl(item.getUrl());
		persistentItem.setDistance(item.getDistance());
		persistentItem.setCategories(new HashSet<>());

		for (String category : item.getCategories()) {
			PersistentCategories persistentCategories = new PersistentCategories();
			persistentCategories.setCategoryId(new CategoryId(item.getItemId(), category));
			persistentCategories.setCategory(category);
			persistentCategories.setPersistentItem(persistentItem);
			persistentItem.getCategories().add(persistentCategories);
		}

		return persistentItem;
	}
}


