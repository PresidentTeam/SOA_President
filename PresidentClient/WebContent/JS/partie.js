
//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest";

var joueur_trouve = "";
var moi = "";
var id_joueur_trouve = "";
var id_partie = "";
var id_moi = "";
var timer = "";

//variables pour le jeu
var passe_manche_lui = false;
var passe_manche_moi = false;
var passe = false;
var nb_cartes_moi = 26;
var nb_cartes_lui = 26;
var nb_tour = 0;
var score = 0;
var tapis = new Array();
var cartes = new Array();
var debut = "";
var mon_tour = "";
var etat = "";

function vide_variable(){
	passe_manche_lui = false;
	passe_manche_moi = false;
	passe = false;
	nb_cartes_moi = 26;
	nb_cartes_lui = 26;
	nb_tour = 0;
	score = 0;
	tapis = new Array();
	cartes = new Array();
	debut = "";
	mon_tour = "";
}

function annuler_boucle(){
	clearInterval(timer);
}

function annuler(){
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/Annuler',
		dataType : 'json',
		data: {
			"id1": id_moi
		},
		success : function(data){
			annuler_boucle();
			self.location.href = "./CreationPartie.html";
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

function joueur_infos(joueur, id){
	joueur_trouve = joueur;
	id_joueur_trouve = id;
}

function ferme(){
	moi = getCookie("CookieLogin");
	
	//si le joueur quitte la partie en cours de route, il est considere comme perdant
	if(mon_tour != "" && cartes.length > 0){
		etat = "quitter";
		score = -2;
		envoyer_infos("");
		save_partie();
		
		$.ajax({
			type: 'POST',
			url : rootURL + '/Partie/Quitter',
			dataType : 'json',
			data: {
				"nb_tour": nb_tour, 
				"id_partie": id_partie
			}
		});
		
	}else{
		$.ajax({
			type: 'POST',
			url : rootURL + '/Partie/Annuler',
			dataType : 'json',
			data: {
				"id1": id_moi
			}
		}); 
	}

	vide_variable();
}

//rechercher une partie en ligne
function jouer_ligne(){	
	moi = getCookie("CookieLogin");
	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Partie/RechercheJoueur',
		dataType : 'json',
		data : {
			"login": moi
		},
		success : function(data){
			var infos = data;
	
			id_moi = infos.id_moi;
			
			joueur_infos(infos.pseudo, infos.id_joueur);
			jouer_ligne2();
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible jouer ligne :" + errorThrown);
		}
	});
}

function jouer_ligne2(){	
	if(joueur_trouve != "Inconnu"){
		//on peut lancer une partie avec ce joueur
		$.ajax({
			type: 'POST',
			url : rootURL + '/Partie/Lancer',
			dataType : 'json',
			data: {
				"id1": id_moi,
				"id2": id_joueur_trouve
			},
			success : function(data){
				id_partie = data.id_partie;
				if(data.debut == id_moi){
					debut = 1;
				}else{
					debut = 0;
				}
				i = 0;
				
				for(key1 in data.cartes){
					cartes[i] = data.cartes[key1];
					i++;
				}
				commencer();
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert("Chemin non accessible :" + errorThrown);
			}
		});
	}else{
		//on attend un joueur
		document.getElementById("attenteJoueur").style.display = "block";
		document.getElementById("deconnexion").style.display = "none";
		document.getElementById("action").innerHTML = "<button id='jouer_ligne' onClick='javascript:annuler();'>Annuler</button>";
		
		$.ajax({
			type: 'POST',
			url : rootURL + '/Partie/Creer',
			dataType : 'json',
			data: {
				"id1": id_moi
			},
			success : function(data){
				//on boucle en questionnant le serveur
				timer = setInterval(question_serveur, 10000);
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert("Chemin non accessible :" + errorThrown);
			}
		});
	}
}

function commencer(){
	cartes.sort(tri);
	
	j = 0;
	for(carte in cartes){
		document.getElementById('cartesHoriJ1').innerHTML += "<img src='./Cartes/"+cartes[carte]+".png' class='cartes_img' height='80px' width='50px' id='carte_"+j+"' onClick='javascript:jouer_carte("+j+")' alt='"+cartes[carte]+"'>";
		j++;
	}
	
	demarrer_partie_ligne();
}

//fonction pour savoir si un joueur veut jouer
function question_serveur(){
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/Trouver',
		dataType : 'json',
		data: {
			"id1": id_moi
		},
		success : function(data){
			id_partie = data.id;
			id_joueur_trouve = data.id2;
			joueur_trouve = data.login2;
			
			if(data.debut == id_moi){
				debut = 1;
			}else{
				debut = 0;
			}
			
			if(id_partie != 0){
				annuler_boucle();
				getCartes();
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

function getCartes(){
	$.ajax({
		type: 'GET',
		url : rootURL + '/Partie/Cartes',
		dataType : 'json',
		data: {
			"id1": id_moi,
			"id_partie": id_partie
		},
		success : function(data){
			i = 0;
		
			for(key1 in data.cartes){
				cartes[i] = data.cartes[key1];
				i++;
			}
			commencer();
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

//affiche quel joueur doit jouer
function changer_infos_tour(tour){
	var text = "";
	
	if(tour){
		text = "<center><h3>A votre tour de jouer</h3></center>";
		document.getElementById('bouton_1').style.display = 'block';
	}else{
		text = "<center><h3>A "+joueur_trouve+" de jouer</h3></center>";
		document.getElementById('bouton_1').style.display = 'none';
	}
	document.getElementById('infos_tour').innerHTML = text;
}

function demarrer_partie_ligne(){
	document.getElementById('recherche').style.display = 'none';
	document.getElementById('plateau').style.display = 'block';

	mon_tour = debut;
	changer_infos_tour(debut);
	
	if(!mon_tour)
		timer = setInterval(attendre_infos, 5000);
	
	document.getElementById('nomJoueur1').innerHTML = moi;
	document.getElementById('nomJoueur2').innerHTML = joueur_trouve;
	document.getElementById('nb_J1').innerHTML = " ("+nb_cartes_moi+" cartes)";
	document.getElementById('nb_J2').innerHTML = " ("+nb_cartes_lui+" cartes)";
}

//le jeu

function passer_ligne(){
	if(!mon_tour)
		return false;
	
	if(passe){
		passe = false;
		etat = "passe";
		envoyer_infos("");
		timer = setInterval(attendre_infos, 5000);
		mon_tour = 0;
		changer_infos_tour(mon_tour);
	}else{
		passe_manche_moi = true;
		
		//si les deux joueurs ont passé, on vide le tapis et le dernier a avoir passe joue
		if(passe_manche_lui){
			vider_tapis();
			etat = "arret";
			envoyer_infos("");
		}else{		
			document.getElementById('infos_moi').style.display = 'block';
			etat = "passe_manche";
			envoyer_infos("");
			mon_tour = 0;
			changer_infos_tour(mon_tour);
			timer = setInterval(attendre_infos, 5000);
		}
	}
}

function jouer_carte(num_carte){
	//si ce n'est pas a mon tour de jouer ou que j'ai deja passe, il ne se passe rien
	if(!mon_tour || passe_manche_moi)
		return false;
	
	var val = cartes[num_carte].substring(0, cartes[num_carte].indexOf("C"));

	val_1 = val_2 = val_3 = 0;
	
	//on recupere la valeur d'au max les 3 dernieres cartes du tapis
	if(tapis.length>0){
		val_1 = tapis[tapis.length-1].substring(0, tapis[tapis.length-1].indexOf("C"));
		if(tapis.length>1){
			val_2 = tapis[tapis.length-2].substring(0, tapis[tapis.length-2].indexOf("C"));
			if(tapis.length>2){
				val_3 = tapis[tapis.length-3].substring(0, tapis[tapis.length-3].indexOf("C"));
			}	
		}
	}
	
	if((tapis.length == 0 || tri(cartes[num_carte], tapis[tapis.length-1]) >= 0) && (!passe || val_1 == val)){
		nb_cartes_moi -=1;
		change_nb_cartes("nb_J1", nb_cartes_moi);
		passe = false;
		
		document.getElementById('carte_'+num_carte).style.display = 'none';
		
		carte_tapis(cartes[num_carte]);
				
		if(nb_cartes_moi ==0){ //si j'ai gagné
			score = 2;

			save_partie();
			cartes = new Array();
			
			document.getElementById('joueur2').style.display = 'none';
			document.getElementById('joueur1').style.display = 'none';
			document.getElementById('plateau2').style.display = 'none';
			document.getElementById('infos_tour').innerHTML = "<center><h3>Bravo "+moi+" ! Vous avez gagne ce tour.</h3><br/> <button id='retour' onClick='javascript:self.location.href(\"./CreationPartie.html\");'>Retour</button></center>";

		}else{	
			
			// si on joue un 2 ou que les 4 cartes de meme valeur sont posees, le jeu se ferme (on vide le tapis)
			if(val == "2" || (val_1 == val_2 && val_2 == val_3 && val_3 == val)){
				vider_tapis();
				etat = "vider";
			}else{
				if(val == val_1 && !passe_manche_lui){
					passe = true;
					etat = "va_passer";
				}
				
				//c'est a lui de jouer
				if(!passe_manche_lui){
					mon_tour = 0;
					changer_infos_tour(mon_tour);
					timer = setInterval(attendre_infos, 5000);
				}
			}
		}
		//on previent le serveur qu'on joue une carte
		envoyer_infos(cartes[num_carte]);
	}else{
		alert("Vous ne pouvez jouer cette carte");
	}
}

function envoyer_infos(libel_carte){
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/JouerCarte',
		dataType : 'json',
		data: {
			"id1": id_moi,
			"id_partie": id_partie,
			"carte": libel_carte,
			"etat": etat //passe, va_passer, passe_manche, quitter, vider
		},
		success : function(data){
		}
	});
	etat = "";
}

function attendre_infos(){
	$.ajax({
		type: 'GET',
		url : rootURL + '/Partie/AttendreCarte',
		dataType : 'json',
		data: {
			"id1": id_moi,
			"id_partie": id_partie
		},
		success : function(data){
			//si l'adversaire a joue
			if(data.nouveau == 1){
				passe = false;
				
				//l'adversaire a vider le tapis, il rejoue
				if(data.etat == "vider"){
					carte_tapis(data.carte);
					nb_cartes_lui --;
					change_nb_cartes("nb_J2", nb_cartes_lui);
					vider_tapis();
				}else if(data.etat == "arret")
						vider_tapis();
				else{
					//si l'adversaire n'a pas passe, il a joue une carte
					if(data.etat == "va_passer" || data.etat == ""){
						carte_tapis(data.carte);
						nb_cartes_lui --;
						change_nb_cartes("nb_J2", nb_cartes_lui);
					}
					if(!passe_manche_moi || nb_cartes_lui == 0)//si le joueur n'a pas passe son tour pour la manche
						annuler_boucle();
					
					if(data.etat == "va_passer" && !passe_manche_moi){
						passe = true;
					}else if(data.etat == "passe_manche"){
						passe_manche_lui = true;
						document.getElementById('infos_lui').style.display = 'block';
					}else if(data.etat == "quitter"){ //si l'adversaire a quitte la partie, le joueur gagne
		
						score = 2;					
						save_partie();
						cartes = new Array();
						
						document.getElementById('joueur2').style.display = 'none';
						document.getElementById('joueur1').style.display = 'none';
						document.getElementById('plateau2').style.display = 'none';
						document.getElementById('infos_tour').innerHTML = "<center><h5>Vous avez gagne ! Le joueur "+joueur_trouve+" est parti.</h5><br/><button id='retour' onClick='javascript:location.href(\"./CreationPartie.html\");'>Retour</button></center>";
					}
					
					//est ce que l'adversaire a gagne
					if(nb_cartes_lui == 0){
						score = -2;
						cartes = new Array();
						save_partie();
						
						document.getElementById('joueur2').style.display = 'none';
						document.getElementById('joueur1').style.display = 'none';
						document.getElementById('plateau2').style.display = 'none';
						document.getElementById('infos_tour').innerHTML = "<center><h3>Dommage ... "+joueur_trouve+" a gagne ce tour.</h3><br/><button id='retour' onClick='javascript:location.href(\"./CreationPartie.html\");'>Retour</button></center>";
	
					}else if(!passe_manche_moi){
						mon_tour = 1;
						changer_infos_tour(mon_tour);
					}
				}
				
				etat = "";
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

function vider_tapis(){
	tapis = new Array();
	document.getElementById('plateau2').innerHTML = "";

	passe_manche_moi = false;
	passe_manche_lui = false;
	passe = false;
	
	document.getElementById('infos_moi').style.display = 'none';
	document.getElementById('infos_lui').style.display = 'none';
}

function change_nb_cartes(id_div, nb){
	//affiche le nb de cartes des joueurs
	document.getElementById(id_div).innerHTML = " ("+nb+" cartes)";
}

function carte_tapis(val_carte){
	tapis[tapis.length] = val_carte;
	document.getElementById('plateau2').innerHTML += " <img src='./Cartes/"+val_carte+".png' class='cartes_img' height='80px' width='50px' alt='"+val_carte+"' />";
}

//a la fin de chaque tour, on enregistre les infos dans la bdd
function save_partie(){
//on cree la partie dans la bdd ou on l'update	
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/Sauver',
		dataType : 'json',
		data: {
			"id1" : id_moi,
			"id_partie": id_partie,
			"score": score
		}
	});
}
	
//selection partie
	
$('#showPartieEnPause').click(function(){
	var span = document.getElementById('showPartieEnPause');
	var div = document.getElementById('PlayOnPause');
	if(div.style.display == 'none'){
		span.innerHTML = '-';
		SearchTypePartie("pause", div);
		div.style.display ='block';
	}
	else{
		div.style.display = 'none';
		span.innerHTML = '+';
		div.innerHTML ="";
	}
	return false;
});

function reprendre_partie(id_part, log_adv){
	moi = getCookie("CookieLogin");
	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Partie/AttendreJoueur',
		dataType : 'json',
		data: {
			"id_partie": id_part,
			"login1": moi,
			"login2": log_adv
		},
		success : function(data){
			if(data.trouve == 0){
				timer = setInterval(reprendre_partie(id_part, log_adv), 10000);
			}else{
				annuler_boucle();
				
				id_moi = data.id_J1;
				id_joueur_trouve = data.id_J2;
				id_partie = id_part;
				joueur_trouve = log_adv;
				nb_cartes_moi = data.cartes.length;
				
				if(data.debut == id_moi){
					debut = true;
				}else{
					debut = false;
				}
				
				i = 0;
				
				for(key1 in data.cartes){
					cartes[i] = data.cartes[key1];
					i++;
				}
				
				for(key2 in data.tapis){
					carte_tapis(data.tapis[key2]);
				}
				commencer();
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Mauvais etat :" + errorThrown);
		}
	});
}

function SearchTypePartie(etat, div){

	moi = getCookie("CookieLogin");
	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Partie/EnPause',
		dataType : 'json',
		data: {
			"etat": etat,
			"login": moi
		},
		success : function(data){
			for(key in data.parties){
				div.innerHTML += "<li onClick='javascript:reprendre_partie("+data.parties[key]+", \""+data.logins[key]+"\")'>Continuer la partie avec "+data.logins[key]+"</li>";
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Mauvais etat :" + errorThrown);
		}
	});
}