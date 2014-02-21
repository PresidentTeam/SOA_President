
//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest";

var joueur_trouve = "";
var moi = "";
var id_joueur_trouve = "";
var id_partie = "";
var id_moi = "";
var timer = "";

//variables pour le jeu
var passe_manche = false;
var passe = false;
var nb_cartes_moi = 26;
var nb_cartes_lui = 26;
var nb_tour = 0;
var score = 0;
var tapis = new Array();
var cartes = new Array();
var perdant = "";
var debut = "";


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
	
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/Annuler',
		dataType : 'json',
		data: {
			"id1": id_moi
		}
	}); 
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
				if(data.debut == moi){
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
		document.getElementById('cartesHoriJ1').innerHTML += "<span id='carte_"+j+"' onclick='javascript:jouer_carte("+j+")'>"+cartes[carte]+"</span> ";
		j++;
	}
	
	demmarrer_partie_ligne();
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

function demmarrer_partie_ligne(){
	document.getElementById('recherche').style.display = 'none';
	document.getElementById('plateau').style.display = 'block';
	
	document.getElementById('nomJoueur1').innerHTML = moi;
	document.getElementById('nomJoueur2').innerHTML = joueur_trouve;
	document.getElementById('nb_J1').innerHTML = " ("+nb_cartes_moi+" cartes)";
	document.getElementById('nb_J2').innerHTML = " ("+nb_cartes_lui+" cartes)";
}

//le jeu

function passer_ligne(){
	if(passe){
		passe = false;
	}else{
		passe_manche = true;
	}
}

	
//selection partie
	
/*$('#showPartieEnPause').click(function(){
	var span = document.getElementById('showPartieEnPause');
	var div = document.getElementById('PlayOnPause');
	if( div.style.display == 'none'){
		span.innerHTML = '-';
		div.style.display ='block';
		SearchTypePartie("EnPause", div);
	}
	else{
		div.style.display = 'none';
		span.innerHTML = '+';
		div.innerHTML ="";
	}
	return false;
});

$('#showPartieEnAttente').click(function(){
	var span = document.getElementById('showPartieEnAttente');
	var div = document.getElementById('PlayOnWait');
	if( div.style.display == 'none'){
		span.innerHTML = '-';
		div.style.display ='block';
		SearchTypePartie("EnAttente", div);
	}
	else {
		div.style.display = 'none';
		span.innerHTML = '+';	
		div.innerHTML ="";
	}
	return false;
});

function SearchTypePartie(etat, div){
	
	$.ajax({
		type: 'GET',
		url : rootURL + 'query?etat=' + etat,
		dataType : 'json',
		success : function(data){
			console.log(data);
			console.log("parties d'etat " + etat);
			renderList(data, div);
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Mauvais etat :" + errorThrown);
		}
	});
}

function renderList(data, div){
	if(data!=null){
		div.innertHTML ='<ul>';
		for(key in data){
			var unePartie = data[key];
			console.log(div);
			div.innerHTML += '<li><a>'+unePartie.idPartie+' - '+ unePartie.NbJoueurs+'</a></li>';
		}
		div.innertHTML +='</ul>';
	}
}

function findAllParties() {
	console.log('findAll');
	$.ajax({
		type: 'GET',
		url: rootURL,
		dataType: "json", 
	});
}

function CreerPartie(){
	console.log('Creation partie :');
	$.ajax({
		type: 'PUT',
		contentType : 'application/json',
		url: rootURL + 'creerPartie/',
		dataType: 'json',
		data : PartieformToJSON(),
		success : function(data, textStatus, jqXHR){
			alert('Joueur inscrit');
		},
		error : function(jqHXR, testStatus, errorThrown){
			alert('Probleme d\'inscription');
		}
	});
}

function PartieformToJSON(){
	var date = new Date(year, month, day);
	return JSON.stringify({
		"NbJoueurs": $('#nbJoueur').val(),
		"dateDebut": date
	});
}*/