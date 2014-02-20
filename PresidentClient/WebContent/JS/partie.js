
//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest";

var joueur_trouve = "";
var moi = "";
var id_joueur_trouve = "";
var id_partie = "";

function annuler(){
	$.ajax({
		type: 'POST',
		url : rootURL + '/Partie/Annuler',
		dataType : 'json',
		data: {
			"login1": moi
		},
		success : function(data){
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
			joueur_infos(infos.pseudo, infos.pseudo);
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
				"login1": moi,
				"id2": id_joueur_trouve
			},
			success : function(data){
				id_partie = data.id_partie;
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
				"login1": moi
			},
			success : function(data){
				//on boucle en questionnant le serveur 
				alert('boucle');
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert("Chemin non accessible :" + errorThrown);
			}
		});
	}
}

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