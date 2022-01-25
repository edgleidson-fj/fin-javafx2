package smtp;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import model.entidade.Usuario;
import seguranca.Criptografia;

public class Email {

	public static void envio(Usuario obj, int x) {
		Criptografia c = new Criptografia();
		
    	String meuEmail = "meu@gmail.com";
		String minhaSenha = "minhaSenha";
				 
		 SimpleEmail email = new SimpleEmail();
	        email.setHostName("smtp.gmail.com");
	        email.setSmtpPort(465);
	        email.setAuthenticator(new DefaultAuthenticator(meuEmail, minhaSenha));
	        email.setSSLOnConnect(true);
	        
	        try {
	            email.setFrom(meuEmail);
	            email.setSubject("Minhas Despesas APP");
	            if(x==1) {
	            email.setMsg("USUÁRIO CADASTRADO COM SUCESSO"
	            		+"\n\nNome: "+c.descriptografar(obj.getNome())
	    	            +"\nEmail: "+c.descriptografar(obj.getEmail())
	    	            +"\nSenha: "+c.descriptografar(obj.getSenha()));
	            }
	            else {
	            	email.setMsg("USUÁRIO ATUALIZADO COM SUCESSO"
		            		+"\n\nNome: "+c.descriptografar(obj.getNome())
		    	            +"\nEmail: "+c.descriptografar(obj.getEmail())
		    	            +"\nSenha: "+c.descriptografar(obj.getSenha()));
	            }	            
	            email.addTo(c.descriptografar(obj.getEmail()));//Destinatario.
	            email.send();
		} catch (Exception e) {
		e.printStackTrace();
	}
    }

}

//Referência: https://www.youtube.com/watch?v=xBZqr4Gdnck