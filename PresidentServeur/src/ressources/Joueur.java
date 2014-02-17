package ressources;

//import java.awt.PageAttributes.*;

//import javax.xml.bind.annotation.XmlRootElement;
import java.sql.*;

import static ressources.BDD.stmt;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//@XmlRootElement

@Path("Player")
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
	
	public Joueur(int i, String n, String p, String l, String m, String pass, int s){
		this.idJoueur = i;
		this.nom = n;
		this.prenom = p;
		this.login = l;
		this.mail = m;
		this.mdp = pass;
		this.score = s;
	}
	
	@GET
	@Path("test")
	public void test(@QueryParam("login") String login, @QueryParam("mdp") String mdp){
		System.out.println("connexion login "+login+" mdp "+mdp);
	}
	
	@GET
	@Path("Connexion")
	@Produces(MediaType.APPLICATION_JSON)
	public String connexion(@DefaultValue("Inconnue") @QueryParam("login") String login, @DefaultValue("Inconnue") @QueryParam("mdp") String mdp) throws SQLException, ClassNotFoundException{
		System.out.println("connexion login "+login+" mdp "+mdp);
		
		BDD bdd = new BDD();
		
		ResultSet resultat = stmt.executeQuery("SELECT *  FROM joueur WHERE login='"+login+"' and mdp='"+mdp+"';"); 
        resultat.last();
        //on récupère le numéro de la ligne
        int nb_lignes = resultat.getRow();
        //on replace le curseur avant la première ligne
        resultat.beforeFirst();
        
        Joueur j = new Joueur();
        
        // Récupération des données du résultat de la requête de lecture 
        if(nb_lignes == 1){
            resultat.next();    
            
            String login_bdd = resultat.getString("login");  
            int id = resultat.getInt("id");
            String nom_bdd = resultat.getString("nom");  
            String prenom_bdd = resultat.getString("prenom");  
            String mail_bdd = resultat.getString("mail");  
            String mdp_bdd = resultat.getString("mdp");  
            int score_bdd = resultat.getInt("score");  
            
            j = new Joueur(id, nom_bdd, prenom_bdd, login_bdd, mail_bdd, mdp_bdd, score_bdd);
            
            System.out.println("connexion réussie");
        }
        return j.toJSON();
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
