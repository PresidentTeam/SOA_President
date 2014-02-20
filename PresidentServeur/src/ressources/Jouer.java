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

		return Construction_response.Construct(200, s);
	}

	@GET
	@Path("NouveauTourLocal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response Nouveau_Local() throws ClassNotFoundException, SQLException{
		String s = tirage_cartes_2_joueurs();

		return Construction_response.Construct(200, s);
	}

	@PUT
	@Path("SauverLocal")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response SauverLocal(Partie p) throws SQLException{ 
		System.out.println("J2 : "+p.getlogin2());
		String login2 = p.getlogin2();
		String login1 = p.getlogin1();
		
		int idJoueur1 = getIdjoueurFromLogin(login1);
		int idJoueur2 = getIdjoueurFromLogin(login2);

		if(!login2.equals("Joueur 2")){
			if(VerifPartieExistante(login1, login2) != null){
				//creer la partie dans la bdd
				String requete = "INSERT INTO  partie "
						+"(nbjoueur, etat, nbtour) "
						+"VALUES ('2', 'en cours', '1');";
				int nb = stmt.executeUpdate(requete, Statement.RETURN_GENERATED_KEYS);
				System.out.println("nb : "+nb);
				
				//récupérer l'id de la partie créer
				ResultSet rs = stmt.getGeneratedKeys();
				int id = 0;
				while(rs.next()){
					id = rs.getInt(1);
					System.out.println(id);
				}
				
				//Ajout de la partie dans les sauvegardes
				String insertJ1 = "INSERT INTO  jouer "
						+"(id_partie, id_joueur, scorepartie) "
						+"VALUES ("+id+","+idJoueur1+", 0);";
				int J1 = stmt.executeUpdate(insertJ1);
				System.out.println("Ajout partie avec J1 : "+J1);

				String insertJ2 = "INSERT INTO  jouer "
						+"(id_partie, id_joueur, scorepartie) "
						+"VALUES ("+id+","+idJoueur2+", 0);"; 
				int J2 = stmt.executeUpdate(insertJ2);
				System.out.println("Ajout partie avec J2 : "+J2);
			}
			else {
				String updateJ1 = "UPDATE jouer SET scorepartie = "+p.getScore1()+"WHERE id_joueur = "+idJoueur1+";";
				int updateValJ1 = stmt.executeUpdate(updateJ1);
				System.out.println("Update score J1 : "+updateValJ1);
				String updateJ2 = "UPDATE jouer SET scorepartie = "+p.getScore2()+"WHERE id_joueur = "+idJoueur2+";"; 
				int updateValJ2 = stmt.executeUpdate(updateJ2);
				System.out.println("Update score J2 : "+updateValJ2);
			}

		}
		return Construction_response.Construct(201, "");
	}

	public String VerifPartieExistante(String log1, String log2) throws SQLException{
		String res = null;
		String partie[] = new String[6];
		// verifie qu'une partie existe pour le joueur 1
		String requete1 = "Select id_partie FROM jouer WHERE id_joueur = "+ log1 +";";
		ResultSet resultat = stmt.executeQuery(requete1);

		//on r�cup�re le num�ro de la ligne
		int nb_lignes = resultat.getRow();
		//on replace le curseur avant la premi�re ligne
		resultat.beforeFirst();

		// R�cup�ration des donn�es du r�sultat de la requ�te de lecture 
		for(int i=0; i < nb_lignes;i++){
			resultat.next();   
			partie[i] = resultat.getString("id_partie");
			String requete2 = "Select id_partie FROM jouer WHERE id_joueur = "+ log2 +" and id_partie = "+partie[i]+";";
			ResultSet resultat2 = stmt.executeQuery(requete2);
			int partieExiste = resultat2.getRow();
			if(partieExiste != 0)
				res = resultat2.getString("id_partie");
		}
		return res;
	}

	public int getIdjoueurFromLogin(String log) throws SQLException{
		int idjoueur = -1;
		// récupère l'id_joueur si il existe
		String requete = "Select id_joueur FROM joueur WHERE login = "+ log +";";
		ResultSet rs = stmt.executeQuery(requete);
		int joueurExiste = rs.getRow();
		if(joueurExiste != 0)
			idjoueur = rs.getInt(1);
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
			s+="\"debut\":\"J1\"}";
		}else{
			s+="\"debut\":\"J2\"}";
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
