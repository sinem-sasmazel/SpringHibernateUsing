package com.calisma.hiberusing;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import Util.Password;
import model.Users;

@Controller
public class LoginController {
	
	SessionFactory sf = HibernateUtil.getSessionFactory(); //DB deki connectiona kar��l�k geliyor

	@RequestMapping(value = "/login", method = RequestMethod.GET) //login sayfas�n� getirmeye yarar
	public String login(HttpServletRequest req) {  //kullan�c� bu sayfada mail ve �ifresini girer. 
		//Login butonuna t�klad��� an a�a��daki request mapping �al���r
		
		if (req.getCookies() != null) { // e�er hi� �erez yoksa �al��maz
		Cookie[] cookies = req.getCookies(); //bu domain alt�ndaki t�m cookieleri getirdi
		for(Cookie item : cookies) {
			if(item.getName().equals("user_remember")) {
				String uid = Password.sifreCoz(item.getValue(), 4);
				System.out.println("Cookie id :"+ uid);
				//session yarat�lacak
				//session i�erisine bu id yi at�p ki�iyi y�nlendir //setAttribute
				req.getSession().setAttribute("user_id", uid);
				return "redirect:/"; //gidece�i sayfaya redirect edecek �ekilde buray� de�i�tir
			}
		}
		
		}							
		return "login";  
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost( Users us, @RequestParam(defaultValue = "off") String remember_me,
							HttpServletResponse res) {  //HttpServletResponse olu�turdu�umuz cookie yi chrome a ekler
		System.out.println("remember_me :" + remember_me);
		Session sesi = sf.openSession();
		try {      //formdan gelen umail ve upassword ile db sorgusu yap�ld�
		Users lus = (Users) sesi.createQuery("from Users where umail = ? and upassword = ?")
				.setParameter(0, us.getUmail()) //1.soru i�atini kullan�c�dan ald���n maile e�itle
				.setParameter(1, us.getUpassword()) //2.soru i�atini kullan�c�dan ald���n passworde e�itle
				.list().get(0);  //bu ko�ullara uyan ilk eleman� (tek eleman) getir.
		System.out.println("Name : "+ lus.getUname());
		//kullan�c� var ve giri� ba�ar�l�
		if(remember_me.equals("on")) {
			//her cookie bir isim bir de de�er tutar
			Cookie cookie = new Cookie("user_remember", Password.sifrele(""+lus.getUid(),4)); // sifrele algoritmas� 4 defa base64 � �al��t�rd�
														//bu de�er id yapt�k ki session yarat�rken kullanabilelim.
		    cookie.setMaxAge(60*60*24); // her cookienin bir �mr� vard�r
		    res.addCookie(cookie);
		}
		
		
		} catch (Exception e) {
			System.out.println("B�yle bir kullan�c� yok");
		}
		
		return "redirect:/";
	}
	
	//exit
	// cookie delete
	@RequestMapping(value = "/exit", method = RequestMethod.GET)
	public String exit(HttpServletResponse res) {
		// yarat�lan yeni cookie eskisiyle replace edilerek cookie silinir
		Cookie cookie = new Cookie("user_remember", ""); //user_remember valuesunu bo� yapt�k
		cookie.setMaxAge(0); // �mr�n� s�f�rlad�k
		res.addCookie(cookie);  //yeni olu�turdu�umuz bu cookieyi taray�c�ya ekledik
		return "redirect:/login";
	}
}
