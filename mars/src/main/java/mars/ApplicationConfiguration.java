package mars;


import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import mars.common.HibernateUtil;
import mars.hibernate.PersistentCategories;
import mars.hibernate.PersistentHistory;
import mars.hibernate.PersistentItem;
import mars.hibernate.PersistentUser;


@Configuration
@EnableWebMvc
public class ApplicationConfiguration {
	@Bean(name="restTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
               return HibernateUtil.createSessionFactory(
                          PersistentItem.class,
                          PersistentCategories.class,
                          PersistentHistory.class,
                          PersistentUser.class);

               				
    }


}
