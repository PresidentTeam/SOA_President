package ressources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mysql.jdbc.Statement;

@Path("Joueur")
public class Population {
	private static ArrayList<Joueur> joueurs = new ArrayList<Joueur>();
	//private static int countJoueur = 0;

	public Population(){
/*		countJoueur++;
		if(countJoueur == 1){
			joueurs = new ArrayList<Joueur>();
			joueurs.add(new Joueur("Romain", "Morice", "Romain", "romain@president.com", "test"));
			joueurs.add(new Joueur("Amelie", "Lefevre", "Amelie", "amelie@president.com","test"));
			joueurs.add(new Joueur("Charlotte", "Saintpierre", "Charlotte", "charlotte@president.com","test"));
		}*/
	}

	@GET
	@Path("{uniqueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDonnees( @PathParam("uniqueId") int id){
		System.out.println(id);
		System.out.println(joueurs.size());
		if(id>=0 && id<joueurs.size())
			return joueurs.get(id).toJSON();
		else 
			return "{\"nom\" : \"Inconnu\", \"prenom\" : \"Inconnu\", \"login\" : \"Inconnu\", \"mail\" : \"Inconnu\", \"mdp\" : \"Inconnu\", \"score\" : 0}";
	}

	@GET
	@Path("query")
	@Produces(MediaType.APPLICATION_JSON) 
	public String getJoueur(@QueryParam("login") String l, @QueryParam("mdp") String m){
		Joueur joueurCo = null;
		System.out.println(l);
		
		for(Joueur j : joueurs)
			if(j.getLogin().equals(l) && j.getMdp().equals(m)){
				joueurCo = j;
				return joueurCo.toJSON();
			} 
		return "{\"nom\" : \"Inconnu\", \"prenom\" : \"Inconnu\", \"login\" : \"Inconnu\", \"mail\" : \"Inconnu\", \"mdp\" : \"Inconnu\", \"score\" : 0}";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getJoueurs(){
		int i = 1;
		String s = "[";
		for(Joueur j : joueurs){
			s += j.toJSON();
			if( i < joueurs.size())
				s+=",\n";
			i++;
		}
		s+="]";
		return s;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/ajoutJoueur")
	public Response AddJoueur(Joueur j) throws SQLException, ClassNotFoundException{
		// Vérification que le login n'est pas utilisé
		BDD database = new BDD();
		String select = "SELECT login FROM joueur WHERE login='"+j.getLogin()+"';";
		ResultSet res = database.stmt.executeQuery(select);
		res.last();
		int nb_lignes = res.getRow();
		String s = null;

		System.out.println("ajoutjoueur : ");
		
		if(nb_lignes < 1){
			// Ajout du joueur dans la base de données

			String insert = "INSERT INTO  joueur "
					+"(prenom, mail, login, mdp, score, nom) "
					+"VALUES ('"+j.getPrenom()+"', '"+j.getMail()+"', '"+j.getLogin()+"', '"+j.getMdp()+"', '"+j.getScore()+"', '"+j.getNom()+"');";
			int nb = database.stmt.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			database.closeConnection();
			System.out.println("nb : "+nb);
			Joueur joueur = new Joueur(nb, j.getNom(), j.getPrenom(), j.getLogin(), j.getMail(), j.getMdp(), j.getScore());
			System.out.println(joueur.getLogin());
			joueurs.add(joueur);
			for ( Joueur jo : joueurs){
				System.out.println(jo.getNom());
			}
			s= "{" +
			"id : "+ nb +
			"nom :" + j.getNom() +
			"prenom :" + j.getPrenom() +
			"login :" + j.getLogin() +
			"mail :" + j.getMail() +
			"mdp :" + j.getMdp() + 
			"score : " + j.getScore() +
			"}";
			return Construction_response.Construct(201, s);
		}
		else {
			return Construction_response.Construct(405, s);
		}
	}

	@DELETE
	@Path("supprimeJoueur/{uniqueId}")
	public void DeleteJoueur( @PathParam("uniqueId") int id ){
		System.out.println(id);
		joueurs.remove(id);
	}
}
