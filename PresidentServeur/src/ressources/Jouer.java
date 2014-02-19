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
		
		if(!login2.equals("Joueur 2")){
			//creer la partie dans la bdd
	        String requete = "INSERT INTO  partie "
	                +"(nbjoueur, etat, nbtour) "
	                +"VALUES ('2', 'en cours', '1');";
	        java.sql.PreparedStatement prep = conn.prepareStatement(requete); 
	        int nb = prep.executeUpdate();
	        System.out.println("nb : "+nb);
		}
		return Construction_response.Construct(201, "");
	}
	
	
	public String tirage_cartes_2_joueurs() throws ClassNotFoundException, SQLException{
		int nb_cartes_joueur = 26;
		String cartes[] = recup_cartes();
		String cartesJ1[] = new String[nb_cartes_joueur];
		String cartesJ2[] = new String[nb_cartes_joueur];
		
		int J1_taille = 0;
		int J2_taille = 0;
		int t=0;
		
		//tirage aléatoire des cartes
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
        //on récupère le numéro de la ligne
        int nb_lignes = resultat.getRow();
        //on replace le curseur avant la première ligne
        resultat.beforeFirst();
        
        // Récupération des données du résultat de la requête de lecture 
        for(int i=0; i < nb_lignes;i++){
            resultat.next();   
            cartes[i] = resultat.getString("libelle");
        }
		
		return cartes;
	}
}
