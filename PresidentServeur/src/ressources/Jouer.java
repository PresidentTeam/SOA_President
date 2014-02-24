package ressources;

import static ressources.BDD.stmt;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import static ressources.BDD.conn;

@Path("Jouer")
public class Jouer {

	@GET
	@Path("Local")
	@Produces(MediaType.APPLICATION_JSON)
	public Response JouerLocal(@QueryParam("login1") String login1, @QueryParam("login2") String login2) throws ClassNotFoundException, SQLException{
		String s = tirage_cartes_2_joueurs();
		int id_j1 = 0;
		int id_j2 = 0;
		
		
		if(login2 != "Joueur 2"){
			int id = 0;
			id_j1 = getIdjoueurFromLogin(login1);
			id_j2 = getIdjoueurFromLogin(login2);
			
			String requete = "INSERT INTO  partie "
					+"(nbjoueur, etat, nbtour, debut) "
					+"VALUES ('2', 'en cours', '0', '0');";
			stmt.executeUpdate(requete);
			
			ResultSet result = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM partie");
			
			if(result.next()){
				id = result.getInt(1);
				String req = "INSERT INTO jouer (id_partie, id_joueur, scorepartie) VALUES ('"+id+"', '"+id_j1+"', '0');";
				stmt.executeUpdate(req);
				
				stmt.executeUpdate("INSERT INTO jouer (id_partie, id_joueur, scorepartie) VALUES ('"+id+"', '"+id_j2+"', '0')");
				
				s += ", \"id_partie\": \""+id+"\"}";
			}
			
		}else{
			s += "}";
		}
		return Construction_response.Construct(200, s);
	}

	@GET
	@Path("NouveauTourLocal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response Nouveau_Local() throws ClassNotFoundException, SQLException{
		String s = tirage_cartes_2_joueurs();
		s += "}";
		return Construction_response.Construct(200, s);
	}
	
	@POST
	@Path("QuitterLocal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response QuitterLocal(@FormParam("nb_tour") int nb_tour, @FormParam("id_partie") int id_partie) throws SQLException, ClassNotFoundException{ 
		if(stmt == null){
			new BDD();
		}
		
		stmt.executeUpdate("UPDATE partie SET nbtour = "+nb_tour+", etat = 'termine' WHERE id = "+id_partie);
		
		return Construction_response.Construct(201, "{}");
	}
	
	@POST
	@Path("PauseLocal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response PauseLocal(
		@FormParam("nb_tour") int nb_tour, 
		@FormParam("id_partie") int id_partie, 
		@FormParam("tour") String tour, 
		@FormParam("login1") String login1, 
		@FormParam("login2") String login2, 
		@FormParam("cartesJ1") String cartes1, 
		@FormParam("cartesJ2") String cartes2, 
		@FormParam("tapis") String tapis) throws SQLException, ClassNotFoundException{ 
		
		if(stmt == null){
			new BDD();
		}
		
		String cart1[] = cartes1.split(",");
		String cart2[] = cartes2.split(",");
		String tap[] = tapis.split(",");
		
		
		int id_j1 = getIdjoueurFromLogin(login1);
		int id_j2 = getIdjoueurFromLogin(login2);
		
		int doit_jouer;
		
		if(tour.equals("J1")){
			doit_jouer = id_j1;
		}else{
			doit_jouer = id_j2;			
		}
		
		for (int i = 0; i < cart1.length; i++) {
		    String element = cart1[i];
			stmt.executeUpdate("INSERT INTO sauvegarde (id_partie, id_joueur, libelle_carte) VALUES ('"+id_partie+"', '"+id_j1+"', '"+element+"')");
		    
		}
		
		for (int j = 0; j < cart2.length; j++) {
		    String element = cart2[j];
			stmt.executeUpdate("INSERT INTO sauvegarde (id_partie, id_joueur, libelle_carte) VALUES ('"+id_partie+"', '"+id_j2+"', '"+element+"')");
		    
		}
		
		for (int k = 0; k < tap.length; k++) {
		    String element = tap[k];
			stmt.executeUpdate("INSERT INTO sauvegarde (id_partie, id_joueur, libelle_carte) VALUES ('"+id_partie+"', '0', '"+element+"')");
		    
		}

		stmt.executeUpdate("UPDATE partie SET nbtour = "+nb_tour+", etat = 'pause', doit_jouer = "+doit_jouer+" WHERE id = "+id_partie);
		
		return Construction_response.Construct(201, "{}");
	}
	
	@POST
	@Path("SauverLocal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response SauverLocal(@FormParam("login1") String login1, @FormParam("login2") String login2, @FormParam("id_partie") int id_partie, @FormParam("score1") int score1, @FormParam("score2") int score2) throws SQLException, ClassNotFoundException{ 
		if(stmt == null){
			new BDD();
		}
		
		int id_j1 = getIdjoueurFromLogin(login1);
		int id_j2 = getIdjoueurFromLogin(login2);
		
		//on met a jour le score lie a la partie
		stmt.executeUpdate("UPDATE jouer SET scorepartie = scorepartie+"+score1+" WHERE id_partie = "+id_partie+" AND id_joueur = "+id_j1);
		stmt.executeUpdate("UPDATE jouer SET scorepartie = scorepartie+"+score2+" WHERE id_partie = "+id_partie+" AND id_joueur = "+id_j2);
		
		//on met a jour le score total du joueur
		stmt.executeUpdate("UPDATE joueur SET score = score+"+score1+" WHERE id = "+id_j1);
		stmt.executeUpdate("UPDATE joueur SET score = score+"+score2+" WHERE id = "+id_j2);
		
		//System.out.println("req :"+requete);
		return Construction_response.Construct(201, "{}");
	}


	public int getIdjoueurFromLogin(String log) throws SQLException{
		int idjoueur = -1;
		// récupère l'id_joueur si il existe
		String requete = "Select id FROM joueur WHERE login = '"+ log +"';";
		ResultSet rs = stmt.executeQuery(requete);

		if(rs.next()){
			idjoueur = rs.getInt("id");
		}
		return idjoueur;
	}

	public String tirage_cartes_2_joueurs() throws ClassNotFoundException, SQLException{
		int nb_cartes_joueur = 26;
		String cartes[] = recup_cartes();
		String cartesJ1[] = new String[nb_cartes_joueur];
		String cartesJ2[] = new String[nb_cartes_joueur];

		int J1_taille = 0;
		int J2_taille = 0;
		int t=0;

		//tirage al�atoire des cartes
		for(t = 0; t<cartes.length && J1_taille<nb_cartes_joueur && J2_taille<nb_cartes_joueur;t++){
			int joueur = (int) (Math.random() * 2); // 0 : carte pour J1, 1 : carte pour J2
			if(joueur == 0){
				cartesJ1[J1_taille] = cartes[t];
				J1_taille ++;
			}else{
				cartesJ2[J2_taille] = cartes[t];
				J2_taille ++;				
			}
		}
		//si le joueur 1 a le maximum de cartes possible, on donne le reste au joueur 2
		if(J1_taille == nb_cartes_joueur && J2_taille < nb_cartes_joueur){
			while(t<cartes.length){
				cartesJ2[J2_taille] = cartes[t];
				J2_taille ++;	
				t++;
			}
		}else if(J2_taille == nb_cartes_joueur && J1_taille < nb_cartes_joueur){
			while(t<cartes.length){
				cartesJ1[J1_taille] = cartes[t];
				J1_taille ++;	
				t++;
			}
		}

		//creation du JSON pour retourner les cartes au client
		String s = "{ \"cartesJ1\" : [";

		for (int i=0; i<nb_cartes_joueur; i++){
			if(i!=0)
				s+=",";
			s+="\""+cartesJ1[i].toString()+"\""; 	
		}

		s+="], \"cartesJ2\" : [";	

		for (int j=0; j<nb_cartes_joueur; j++){
			if(j!=0)
				s+=",";
			s+="\""+cartesJ2[j].toString()+"\""; 	
		}

		s+="],";

		int debut = (int) (Math.random() * 2);
		if(debut == 0){
			s+="\"debut\":\"J1\"";
		}else{
			s+="\"debut\":\"J2\"";
		}

		//System.out.println("s : "+s);

		return s;
	}

	public String[] recup_cartes() throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		String cartes[] = new String[52];


		ResultSet resultat = stmt.executeQuery("SELECT libelle FROM carte;"); 
		resultat.last();
		//on r�cup�re le num�ro de la ligne
		int nb_lignes = resultat.getRow();
		//on replace le curseur avant la premi�re ligne
		resultat.beforeFirst();

		// R�cup�ration des donn�es du r�sultat de la requ�te de lecture 
		for(int i=0; i < nb_lignes;i++){
			resultat.next();   
			cartes[i] = resultat.getString("libelle");
		}

		return cartes;
	}
}
