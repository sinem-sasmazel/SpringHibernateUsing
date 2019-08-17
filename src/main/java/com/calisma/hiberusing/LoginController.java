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
	
	SessionFactory sf = HibernateUtil.getSessionFactory(); //DB deki connectiona karþýlýk geliyor

	@RequestMapping(value = "/login", method = RequestMethod.GET) //login sayfasýný getirmeye yarar
	public String login(HttpServletRequest req) {  //kullanýcý bu sayfada mail ve þifresini girer. 
		//Login butonuna týkladýðý an aþaðýdaki request mapping çalýþýr
		
		if (req.getCookies() != null) { // eðer hiç çerez yoksa çalýþmaz
		Cookie[] cookies = req.getCookies(); //bu domain altýndaki tüm cookieleri getirdi
		for(Cookie item : cookies) {
			if(item.getName().equals("user_remember")) {
				String uid = Password.sifreCoz(item.getValue(), 4);
				System.out.println("Cookie id :"+ uid);
				//session yaratýlacak
				//session içerisine bu id yi atýp kiþiyi yönlendir //setAttribute
				req.getSession().setAttribute("user_id", uid);
				return "redirect:/"; //gideceði sayfaya redirect edecek þekilde burayý deðiþtir
			}
		}
		
		}							
		return "login";  
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost( Users us, @RequestParam(defaultValue = "off") String remember_me,
							HttpServletResponse res) {  //HttpServletResponse oluþturduðumuz cookie yi chrome a ekler
		System.out.println("remember_me :" + remember_me);
		Session sesi = sf.openSession();
		try {      //formdan gelen umail ve upassword ile db sorgusu yapýldý
		Users lus = (Users) sesi.createQuery("from Users where umail = ? and upassword = ?")
				.setParameter(0, us.getUmail()) //1.soru iþatini kullanýcýdan aldýðýn maile eþitle
				.setParameter(1, us.getUpassword()) //2.soru iþatini kullanýcýdan aldýðýn passworde eþitle
				.list().get(0);  //bu koþullara uyan ilk elemaný (tek eleman) getir.
		System.out.println("Name : "+ lus.getUname());
		//kullanýcý var ve giriþ baþarýlý
		if(remember_me.equals("on")) {
			//her cookie bir isim bir de deðer tutar
			Cookie cookie = new Cookie("user_remember", Password.sifrele(""+lus.getUid(),4)); // sifrele algoritmasý 4 defa base64 ü çalýþtýrdý
														//bu deðer id yaptýk ki session yaratýrken kullanabilelim.
		    cookie.setMaxAge(60*60*24); // her cookienin bir ömrü vardýr
		    res.addCookie(cookie);
		}
		
		
		} catch (Exception e) {
			System.out.println("Böyle bir kullanýcý yok");
		}
		
		return "redirect:/";
	}
	
	//exit
	// cookie delete
	@RequestMapping(value = "/exit", method = RequestMethod.GET)
	public String exit(HttpServletResponse res) {
		// yaratýlan yeni cookie eskisiyle replace edilerek cookie silinir
		Cookie cookie = new Cookie("user_remember", ""); //user_remember valuesunu boþ yaptýk
		cookie.setMaxAge(0); // ömrünü sýfýrladýk
		res.addCookie(cookie);  //yeni oluþturduðumuz bu cookieyi tarayýcýya ekledik
		return "redirect:/login";
	}
}
