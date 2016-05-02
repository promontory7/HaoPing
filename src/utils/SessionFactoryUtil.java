package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//µ¥ÀýÄ£Ê½
public class SessionFactoryUtil {
	public static SessionFactory instance = null;
	static Object object =new Object();

	public static SessionFactory getInstance() {
		if (instance == null) {
			synchronized (object) {
				if (instance==null) {
					instance = new Configuration().configure().buildSessionFactory();
				}
			}
		}
		return instance;
	}
}
