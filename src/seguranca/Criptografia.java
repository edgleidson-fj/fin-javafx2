package seguranca;
//Nome
public class Criptografia implements GatilhoCriptografia {

	@Override
	public String criptografia(String textoParaCriptografar) {
		StringBuffer sb = new StringBuffer(textoParaCriptografar);
		String a = sb.reverse().toString();
		StringBuilder sc = new StringBuilder(a);

		String p = sc.insert(1, "ErluiIlChJkLucaPoStaJAc").toString();
		p = sc.insert(0, "GbRPHA�wiyzoUSoKBFhlpeswWryZA").toString();
		p = sc.insert(p.length() - 1, "maqzhbijckdrebnmlDsbcNqu").toString();
		p = sc.insert(p.length(), "Brunoawhernan").toString();
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

//Refer�ncia:
//https:www.guj.com.br/t/algoritmo-simples-de-criptografia-de-chave-simetrica-que-eu-criei/339025
