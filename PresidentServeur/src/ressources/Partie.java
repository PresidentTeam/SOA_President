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
		
		String req = "Select id FROM joueur WHERE login = '"+ login +"';";
		ResultSet res = stmt.executeQuery(req);
		
		int id_moi = 0;
		
		if(res.next()){
			id_moi = res.getInt("id");
		}
		
		
		s += "\""+log+"\", \"id_moi\":\""+id_moi+"\", \"id_joueur\":\""+id+"\"}";
		
		//System.out.println("s : "+s);
		
		return Construction_response.Construct(200, s);
	}
	
	@GET
	@Path("Cartes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ObtenirCartes(@QueryParam("id1") int id1, @QueryParam("id_partie") int id_partie) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		
		String req1 = "SELECT carte FROM cartes_tmp WHERE id_partie = "+id_partie+" AND id_joueur = "+id1;
		
		ResultSet resultat = stmt.executeQuery(req1);
		
		resultat.last();
		//on recupere le numero de la ligne
		int nb_lignes = resultat.getRow();
		
		resultat.beforeFirst();
		
		String carte = "";
		
		int i=0;

		String s = "{\"cartes\" : [";
		
		while(resultat.next()){
			carte = resultat.getString("carte");	
			if(i!=0)
				s+=",";
			s+="\""+carte+"\""; 	
			i++;
		}

		s+="]}";
		
		//on supprime les cartes dans la bdd
		String requete = "DELETE FROM cartes_tmp WHERE id_partie = "+id_partie+" AND id_joueur = "+id1;
		stmt.executeUpdate(requete);
		
		//System.out.println(" Cartes s : "+s);
		
		return Construction_response.Construct(200, s);
	}
	
	
	@POST
	@Path("Lancer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response LancerPartieLigne(@FormParam("id1") int id1, @FormParam("id2") int id2) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		//creer partie dans bdd
		//retourner l'id
		int id_debut = 0;
		
		int debut = (int) (Math.random() * 2);
		
		if(debut == 0){
			id_debut = id1;
		}else{
			id_debut = id2;
		}
		
		String requete = "INSERT INTO  partie "
				+"(nbjoueur, etat, nbtour, debut) "
				+"VALUES ('2', 'commence', '0', '"+id_debut+"');";
		stmt.executeUpdate(requete);
		
		int nb = 0;
		
		ResultSet result = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM partie");
		
		if(result.next())
			nb = result.getInt(1);
			
		String s = "{ \"id_partie\" : \""+nb+"\", \"debut\" : \""+id_debut+"\", ";
		
		String req = "INSERT INTO  jouer "
				+"(id_partie, id_joueur, scorepartie) "
				+"VALUES ('"+nb+"', '"+id1+"', '0');";
		stmt.executeUpdate(req);

		String req2 = "INSERT INTO  jouer "
				+"(id_partie, id_joueur, scorepartie) "
				+"VALUES ('"+nb+"', '"+id2+"', '0');";
		stmt.executeUpdate(req2);
		
		String cartes[] = tirage_cartes_2_joueurs(nb, id2);
		
		s += "\"cartes\" : [";
				
		//on convertit en json
		for (int i=0; i<cartes.length; i++){
			if(i!=0)
				s+=",";
			s+="\""+cartes[i].toString()+"\""; 	
		}

		s+="]";
		s += "}";
		
		//System.out.println(" insert into s : "+s);
		
		return Construction_response.Construct(201, s);
	}
	
	@POST
	@Path("Creer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response AttendreJoueurLigne(@FormParam("id1") int id1) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String requete = "UPDATE  joueur SET en_attente_partie = 1 WHERE id = "+id1;
		stmt.executeUpdate(requete);
		
		String s = "{}";
		
		return Construction_response.Construct(201, s);
	}
	
	//le joueur questionne régulièrement le serveur pour savoir si un autre joueur veut jouer
	@POST
	@Path("Trouver")
	@Produces(MediaType.APPLICATION_JSON)
	public Response PartieLigneTrouver(@FormParam("id1") int id1) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String req1 = "SELECT p.id, p.debut FROM partie p INNER JOIN jouer j ON j.id_partie = p.id WHERE j.id_joueur = "+id1+" AND p.etat = 'commence'";
		
		ResultSet resultat = stmt.executeQuery(req1);
		
		int id = 0;
		int id2 = 0;
		String log2 = "";
		int debut = 0;
		
		if(resultat.next()){
			id = resultat.getInt("id");	
			debut = resultat.getInt("debut");
			
			String requete = "UPDATE  joueur SET en_attente_partie = 0 WHERE id = "+id1;
			stmt.executeUpdate(requete);
			
			String req = "UPDATE partie SET etat = 'en cours' WHERE id = "+id;
			stmt.executeUpdate(req);
			
			String req2 = "SELECT j.id, j.login FROM joueur j INNER JOIN jouer jo ON jo.id_joueur = j.id WHERE jo.id_partie = "+id;
			
			ResultSet result = stmt.executeQuery(req2);
			
			if(result.next()){
				id2 = result.getInt("id");
				log2 = result.getString("login");
			}
		}
		
		String s = "{ \"id\" : \""+id+"\", \"id2\" : \""+id2+"\", \"login2\" : \""+log2+"\", \"debut\" : \""+debut+"\"}";

		//System.out.println(" s :"+s);
		
		return Construction_response.Construct(201, s);
	}
	
	@POST
	@Path("Annuler")
	@Produces(MediaType.APPLICATION_JSON)
	public Response AnnulerAttente(@FormParam("id1") int id1) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String requete = "UPDATE joueur SET en_attente_partie = 0 WHERE id = "+id1;
		stmt.executeUpdate(requete);
		
		//System.out.println("req :"+requete);
		
		return Construction_response.Construct(201, "{}");
	}
	
	public String[] tirage_cartes_2_joueurs(int id_partie, int id_joueur) throws ClassNotFoundException, SQLException{
		int nb_cartes_joueur = 26;
		String cartes[] = recup_cartes();
		String cartesJ1[] = new String[nb_cartes_joueur];
		String cartesJ2[] = new String[nb_cartes_joueur];

		int J1_taille = 0;
		int J2_taille = 0;
		int t=0;

		//tirage aleatoire des cartes
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

		//on parcourt les cartes du J2 pour les enregistrer dans la bdd
		for(int h=0; h<cartesJ2.length; h++){
			String requete = "INSERT INTO  cartes_tmp "
					+"(id_partie, id_joueur, carte) "
					+"VALUES ('"+id_partie+"', '"+id_joueur+"', '"+cartesJ2[h]+"');";
			stmt.executeUpdate(requete);
		}

		return cartesJ1;
	}

	public String[] recup_cartes() throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		String cartes[] = new String[52];


		ResultSet resultat = stmt.executeQuery("SELECT libelle FROM carte;"); 
		resultat.last();
		//on rï¿½cupï¿½re le numï¿½ro de la ligne
		int nb_lignes = resultat.getRow();
		//on replace le curseur avant la premiï¿½re ligne
		resultat.beforeFirst();

		// Rï¿½cupï¿½ration des donnï¿½es du rï¿½sultat de la requï¿½te de lecture 
		for(int i=0; i < nb_lignes;i++){
			resultat.next();   
			cartes[i] = resultat.getString("libelle");
		}

		return cartes;
	}
	
	//pendant la partie en ligne
	@POST
	@Path("JouerCarte")
	@Produces(MediaType.APPLICATION_JSON)
	public Response JouerCarte(@FormParam("id1") int id1, @FormParam("id_partie") int id_partie, @FormParam("carte") String carte, @FormParam("etat") String etat) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		String requete = "INSERT INTO action (id_joueur, id_partie, libelle_carte, etat) VALUES ('"+id1+"', '"+id_partie+"', '"+carte+"', '"+etat+"');";
		stmt.executeUpdate(requete);
		
		//System.out.println("req :"+requete);
		
		return Construction_response.Construct(201, "{}");
	}
	
	@GET
	@Path("AttendreCarte")
	@Produces(MediaType.APPLICATION_JSON)
	public Response AttendreCarte(@QueryParam("id1") int id1, @QueryParam("id_partie") int id_partie) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		int nouveau = 0;
		String libel = "";
		String etat = "";
		
		String req1 = "SELECT id, libelle_carte, etat FROM action WHERE id_partie = "+id_partie+" AND id_joueur != "+id1+" ORDER BY id";
		
		ResultSet resultat = stmt.executeQuery(req1);
		
		if(resultat.next()){
			nouveau = 1;
			int id = resultat.getInt("id");	
			libel = resultat.getString("libelle_carte");
			etat = resultat.getString("etat");
			
			stmt.executeUpdate("DELETE FROM action WHERE id="+id);
		}
		
		String s = "{\"nouveau\" : \""+nouveau+"\", \"carte\" : \""+libel+"\", \"etat\" : \""+etat+"\"}";
		
		//on supprime l'action dans la bdd
		String requete = "DELETE FROM cartes_tmp WHERE id_partie = "+id_partie+" AND id_joueur = "+id1;
		stmt.executeUpdate(requete);
		
		//System.out.println(" Attente Cartes s : "+s);
		
		return Construction_response.Construct(200, s);
	}
	
	@POST
	@Path("Sauver")
	@Produces(MediaType.APPLICATION_JSON)
	public Response SauverScore(@FormParam("id1") int id1, @FormParam("id_partie") int id_partie, @FormParam("score") int score) throws ClassNotFoundException, SQLException{
		if(stmt == null){
			new BDD();
		}
		
		//on met a jour le score lie a la partie
		stmt.executeUpdate("UPDATE jouer SET scorepartie = scorepartie+"+score+" WHERE id_partie = "+id_partie+" AND id_joueur = "+id1);
		
		//on met a jour le score total du joueur
		stmt.executeUpdate("UPDATE joueur SET score = score+"+score+" WHERE id = "+id1);
		
		//System.out.println("req :"+requete);
		
		return Construction_response.Construct(201, "{}");
	}
	
	@POST
	@Path("Quitter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response QuitterLocal(@FormParam("nb_tour") int nb_tour, @FormParam("id_partie") int id_partie) throws SQLException, ClassNotFoundException{ 
		if(stmt == null){
			new BDD();
		}
		
		stmt.executeUpdate("UPDATE partie SET nbtour = "+nb_tour+", etat = 'termine' WHERE id = "+id_partie);
		
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
