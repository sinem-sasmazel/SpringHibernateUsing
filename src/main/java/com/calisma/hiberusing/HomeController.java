package com.calisma.hiberusing;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import model.Users;


@Controller
public class HomeController {
	
	SessionFactory sf = HibernateUtil.getSessionFactory();
	int editID = 0;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		Session sesi = sf.openSession();
		List<Users> ls = sesi.createQuery("from Users order by uid desc") //userlarý sondan baþa sýralar
				.setFirstResult(0)
				.setMaxResults(10)
				.list(); //en fazla 10 eleman döndürür
		model.addAttribute("data", ls);
		/*
		Session sesi = sf.openSession();
		Transaction tr = sesi.beginTransaction();
		
		Users us = new Users();
		us.setUname("Emre");
		us.setUsurname("Bilmem");
		us.setUmail("emre@emre.com");
		us.setUpassword("12345");
		
		sesi.save(us);
		tr.commit();
		//tr.rollback();// iþlemleri geri al
		 */
		
		return "home";
	}
	
	
	@RequestMapping(value = "/userInsert", method = RequestMethod.POST)
	public String userInsert( Users us ) {
		Session sesi = sf.openSession();
		Transaction tr = sesi.beginTransaction();
		int id = (Integer) sesi.save(us);
		if (id > 0) {
			// ekleme baþarýlý
		}
		tr.commit();
		return "redirect:/";
	}
	
	
	@RequestMapping(value = "/delete/{userId}", method = RequestMethod.GET)
	public String userDelete(@PathVariable int userId) {
		
		Session sesi = sf.openSession();
		Transaction tr = sesi.beginTransaction();
		
		Users us = sesi.load(Users.class, userId);
		sesi.delete(us);
		tr.commit();
		
		return "redirect:/";
	}
	
	
	@RequestMapping(value = "/edit/{userId}", method = RequestMethod.GET)
	public String edit(@PathVariable int userId, Model model) {
		editID = userId;
		Session sesi = sf.openSession();
		Users us = sesi.load(Users.class, userId);
		model.addAttribute("us", us);
		return "home";
	}
  
	
	@RequestMapping(value = "/userEdit", method = RequestMethod.POST)
	public String userEdit( Users us ) {
		
		Session sesi = sf.openSession();
		Transaction tr = sesi.beginTransaction();
		us.setUid(editID);
		sesi.update(us);
		tr.commit();
		
		return "redirect:/";
	}
	
	
	
}
