package smtp;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import model.entidade.Usuario;
import seguranca.Criptografia;

public class Email {

	public static void envio(Usuario obj, int x) {
		try {
			Criptografia c = new Criptografia();
			String e = CredencialEmail.email();
			String s = CredencialEmail.senha();
			String emailApp = c.descriptografar(e);
			String senhaApp = c.descriptografar(s);

			SimpleEmail email = new SimpleEmail();
			email.setHostName("smtp.gmail.com"); 
			email.setSmtpPort(465); 
			email.setAuthenticator(new DefaultAuthenticator(emailApp, senhaApp));
			email.setSSLOnConnect(true);
			email.setFrom(emailApp);
			email.setSubject("Minhas Despesas APP"); 
			if (x == 1) {
				email.setMsg( 
						"USUÁRIO CADASTRADO COM SUCESSO" + "\n\nNome: " + c.descriptografar(obj.getNome()) + "\nEmail: "
								+ c.descriptografar(obj.getEmail()) + "\nSenha: " + c.descriptografar(obj.getSenha()));
			} else {
				email.setMsg(
						"USUÁRIO ATUALIZADO COM SUCESSO" + "\n\nNome: " + c.descriptografar(obj.getNome()) + "\nEmail: "
								+ c.descriptografar(obj.getEmail()) + "\nSenha: " + c.descriptografar(obj.getSenha()));
			}
			email.addTo(c.descriptografar(obj.getEmail()));
			email.send(); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

//Referência: https://www.youtube.com/watch?v=xBZqr4Gdnck