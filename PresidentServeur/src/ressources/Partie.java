package ressources;

import static ressources.BDD.stmt;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mysql.jdbc.Statement;

//import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement

@Path("Partie")
public class Partie {
	
	private int idPartie;
	private int nb_tour;
	private int score1;
	private int score2;
	private String login1;
	private String login2;

	public Partie(){
		this.idPartie = 0;
		this.nb_tour = 0;
		this.score1 = 0;
		this.score2 = 0;
		this.login1 = null;
		this.login2 = null;
	}

	public Partie(String login1, String login2, int score1, int score2, int nb_tour){
		this.nb_tour = nb_tour;
		this.score1 = score1;
		this.score2 = score2;
		this.login1 = login1;
		this.login2 = login2;
	}

	@GET
	@Path("RechercheJoueur")
	@Produces(MediaType.APPLICATION_JSON)
	public Response RecherchePartieLigne(@QueryParam("login") String login) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String req1 = "SELECT id, login FROM joueur WHERE en_attente_partie = 1 AND login != '"+login+"'";
		
		ResultSet resultat = stmt.executeQuery(req1);
		
		resultat.last();
		
		//on recupere le numero de la ligne
		int nb_lignes = resultat.getRow();

		String s = "{\"pseudo\":";
		String log = "Inconnu";
		int id = 0;
		
		if(nb_lignes > 0){
			int aleatoire = (int) (Math.random() * nb_lignes+1);

			resultat.absolute(aleatoire);
			
			log = resultat.getString("login");	
			id = resultat.getInt("id");
		}
		
		s += "\""+log+"\", \"id_joueur\":\""+id+"\"}";
		
		//System.out.println("s : "+s);
		
		return Construction_response.Construct(200, s);
	}
	
	@POST
	@Path("Lancer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response LancerPartieLigne(@QueryParam("login1") String login1, @QueryParam("id2") int id2) throws ClassNotFoundException, SQLException{
		
		//creer partie dans bdd
		//retourner l'id
		String s = "{ \"id_partie\" : ";
		
		s += "}";
		
		System.out.println("s : "+s);
		
		return Construction_response.Construct(201, s);
	}
	
	@POST
	@Path("Creer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response AttendreJoueurLigne(@FormParam("login1") String login1) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String requete = "UPDATE  joueur SET en_attente_partie = 1 WHERE login = '"+login1+"'";
		stmt.executeUpdate(requete);
		
		String s = "{ \" id \" : \" id \"";
		
		s += "}";
		
		return Construction_response.Construct(201, s);
	}
	
	@POST
	@Path("Annuler")
	@Produces(MediaType.APPLICATION_JSON)
	public Response AnnulerAttente(@FormParam("login1") String login1) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String requete = "UPDATE  joueur SET en_attente_partie = 0 WHERE login = '"+login1+"'";
		stmt.executeUpdate(requete);
		
		return Construction_response.Construct(201, "{}");
	}
	
	public int getIdPartie(){return this.idPartie;}
	public int getNbTour(){return this.nb_tour;}
	public int getScore1(){return this.score1;}
	public int getScore2(){return this.score2;}
	public String getlogin1(){return this.login1;}
	public String getlogin2(){return this.login2;}

	/*public String toJSON(){
		return "{ \"idPartie\" : \""+idPartie+"\", \"NbJoueurs\" : \""+NbJoueurs+"\",\"doitJouer\" : \""+doitJouer+"\", \"vainqueur\" : \""+vainqueur+"\", \"dateDebut\" : \""+dateDebut+"\"}";
	}*/
}
