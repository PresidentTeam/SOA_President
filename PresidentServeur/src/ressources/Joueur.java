package ressources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Joueur {

	private int idJoueur = 0;
	public String nom;
	public String prenom;
	public String login;
	public String mail;
	public String mdp;
	public int score;
	
	
	public Joueur(){
		this.setIdJoueur(this.getIdJoueur() + 1);
		this.nom = "Inconnu";
		this.prenom = "Inconnu";
		this.login = "Inconnu";
		this.mail = "Inconnu";
		this.mdp = "Inconnu";
		this.score = 0;
	}
	
	public Joueur(String nom, String prenom, String login, String mail, String mdp){
		this.setIdJoueur(this.getIdJoueur() + 1);
		this.nom = nom;
		this.prenom = prenom;
		this.login = login;
		this.mail = mail;
		this.mdp = mdp;
		this.score = 0;
	}
	
	public int getIdJoueur() {return idJoueur;}
	public String getNom(){return this.nom;}
	public String getPrenom(){return this.prenom;}
	public String getLogin(){return this.login;}
	public String getMail(){return this.mail;}
	public String getMdp(){return this.mdp;}
	
	public void setIdJoueur(int idJoueur) {this.idJoueur = idJoueur;}
	public void setNom(String n){this.nom = n;}
	public void setPrenom(String p){this.prenom = p;}
	public void setLogin(String l){this.login = l;}
	public void setMail(String m){this.mail = m;}
	public void setMdp(String m){this.mdp = m;}
	
	public String toJSON(){
		return "{ \"nom\" : \""+nom+"\", \"prenom\" : \""+prenom+"\",\"login\" : \""+login+"\", \"mail\" : \""+mail+"\", \"mdp\" : \""+mdp+"\"}";
	}

	
	}
