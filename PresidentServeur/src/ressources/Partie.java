package ressources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Partie {

	private int idPartie;
	private int NbJoueurs;
	private int doitJouer;
	private String etat;
	private int vainqueur;
	private String dateDebut;

	public Partie(){
		this.idPartie = 0;
		this.NbJoueurs = 2;
		this.doitJouer = 1;
		this.etat = "fini";
		this.vainqueur = 1;
		this.dateDebut = "2014-02-11";
	}

	public Partie(int nbJoueurs, int doitJouer, String date){
		this.idPartie++;
		this.NbJoueurs = nbJoueurs;
		this.doitJouer = doitJouer;
		this.etat = "EnAttente";
		this.dateDebut = date;
		this.vainqueur = 0;
	}

	public int getIdPartie(){return this.idPartie;}
	public int getNbJoueurs(){return this.NbJoueurs;}
	public int getDoitJouer(){return this.doitJouer;}
	public String getEtat(){return this.etat;}
	public int getVainqueur(){return this.vainqueur;}
	public String getDate(){return this.dateDebut;}

	public String toJSON(){
		return "{ \"idPartie\" : \""+idPartie+"\", \"NbJoueurs\" : \""+NbJoueurs+"\",\"doitJouer\" : \""+doitJouer+"\", \"vainqueur\" : \""+vainqueur+"\", \"dateDebut\" : \""+dateDebut+"\"}";
	}
}
