package ressources;

import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("Partie")
public class CollectionPartie {
	private static ArrayList<Partie> parties;
	private static int countPartie = 0;

	public CollectionPartie(){
		countPartie++;
		if(countPartie ==1){
			parties = new ArrayList<Partie>();
			parties.add(new Partie(2,1,"2014-11-02"));
			parties.add(new Partie(3,2,"2014-12-02"));
		}
	}

	@GET
	@Path("{uniqueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDonnees( @PathParam("uniqueId") int id){
		System.out.println(id);
		System.out.println(parties.size());
		if(id>=0 && id<parties.size())
			return parties.get(id).toJSON();
		else 
			return "{\"idPartie\" : 0, \"NbJoueurs\" : 0, \"doitJouer\" : 0, \"etat\" : \"Inconnu\", \"vainqueur\" : 0, \"dateDebut\" : \"Inconnu\"}";
	}

	@GET
	@Path("query")
	@Produces(MediaType.APPLICATION_JSON) // sends JSON
	public String getTypePartie(@QueryParam("etat") String e){
		ArrayList<String> Ap = new ArrayList<String>();
		for(Partie p : parties)
			if(p.getEtat().contains(e))
				Ap.add(p.toJSON());
		return Ap.toString();
	//	return "{\"idPartie\" : 0, \"NbJoueurs\" : 0, \"doitJouer\" : 0, \"etat\" : \""+e+"\", \"vainqueur\" : 0, \"dateDebut\" : \"Inconnu\"}";
	}

	@GET	 // this method process GET request from client
	@Produces(MediaType.APPLICATION_JSON)
	public String getParties(){
		int i = 1;
		String s = "[";
		for(Partie p : parties){
			s += p.toJSON();
			if( i < parties.size())
				s+=",\n";
			i++;
		}
		s+="]";
		return s;
	}


	@DELETE
	@Path("supprimePartie/{uniqueId}")
	public void DeletePartie( @PathParam("uniqueId") int id ){
		System.out.println(id);
		parties.remove(id);
	}
}
