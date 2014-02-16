package ressources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Joueur {
	
	private int idJoueur = 0;
	private String nom;
	private String prenom;
	private String login;
	private String mail;
	private String mdp;
	private int score;

	public Joueur(){
		this.nom = "Inconnu";
		this.prenom = "Inconnu";
		this.login = "Inconnu";
		this.mail = "Inconnu";
		this.mdp = "Inconnu";
		this.score = 0;
	}

	public Joueur(String n, String p, String l, String m, String pass){
		this.nom = n;
		this.prenom = p;
		this.login = l;
		this.mail = m;
		this.mdp = pass;
		this.score = 0;
		/*
		BDD database = new BDD();
		database.createConnection();
		database.insertJoueur(m, p, n, l, pass);
		database.closeConnection();
		*/
	}

	public int getIdJoueur() {return this.idJoueur;}
	public String getNom(){return this.nom;}
	public String getPrenom(){return this.prenom;}
	public String getLogin(){return this.login;}
	public String getMail(){return this.mail;}
	public String getMdp(){return this.mdp;}

	public void setIdJoueur(int idJ) {this.idJoueur = idJ;}
	public void setNom(String n){this.nom = n;}
	public void setPrenom(String p){this.prenom = p;}
	public void setLogin(String l){this.login = l;}
	public void setMail(String m){this.mail = m;}
	public void setMdp(String m){this.mdp = m;}

	public String toJSON(){
		return "{ \"nom\" : \""+nom+"\", \"prenom\" : \""+prenom+"\",\"login\" : \""+login+"\", \"mail\" : \""+mail+"\", \"mdp\" : \""+mdp+"\"}";
	}
	
}
