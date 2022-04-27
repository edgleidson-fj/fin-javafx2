package seguranca;
//Email
public class Criptografia4 implements GatilhoCriptografia {

	@Override
	public String criptografia(String textoParaCriptografar) {
		StringBuffer sb = new StringBuffer(textoParaCriptografar);
		String a = sb.reverse().toString();
		StringBuilder sc = new StringBuilder(a);

	  	String p = sc.insert(1, "yahoo@fgjrbol@uoews_gh@").toString();
	  	p = sc.insert(0, "news@123_sdgbol@mkfldyahoo@2a").toString();
	   	p = sc.insert(p.length() - 1, "uol@_@345hotmail@56_@bol").toString();
   	  	p = sc.insert(p.length(), "@uol3h5hxmail").toString();
		return p;
	}

	@Override
	public String descriptografar(String textoParaDescriptografar) {
		StringBuilder sl = new StringBuilder(textoParaDescriptografar);
		sl.delete(0, 29);
		sl.delete(1, 24);
		sl.delete(sl.length() - 13, sl.length());
		sl.delete(sl.length() - 25, sl.length() - 1);

		StringBuffer sf = new StringBuffer(sl.toString());
		String x = sf.reverse().toString();
		return x;
	}

}

//Referência:
//https:www.guj.com.br/t/algoritmo-simples-de-criptografia-de-chave-simetrica-que-eu-criei/339025
