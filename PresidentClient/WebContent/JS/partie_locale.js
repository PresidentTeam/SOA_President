/**
 * 
 * Ficher Javascript pour toutes les actions sur les joueurs
 */

//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest";

var cartes1 = new Array();
var cartes2 = new Array();
var joueur_1 = null;
var joueur_2 = null;
var nb_cartes_J1 = null;
var nb_cartes_J2 = null;
var nb_tour = 0;
var score_J1 = 0;
var score_J2 =0;
var id_partie = null;

function afficher_connex(){
	var joueur_co_2 = getCookie("CookieLogin2");
	
	if(joueur_co_2){
		var div = document.getElementById('no_connex');
	
		if( div.style.display == 'none'){
			var span_2 = document.getElementById('nom_2');
			var span_deco_2 = document.getElementById('deco_nom_2');
			
			span_2.innerHTML = joueur_co_2;
			span_deco_2.innerHTML = joueur_co_2;
			div.style.display ='block';
		}
	}else{
		var div = document.getElementById('connex');
		
		if( div.style.display == 'none'){
			div.style.display ='block';
		}
	}
}

function deco_joueur2(){
	eraseCookie("CookieLogin2");
	self.location.href = "./ConnexionLocale.html";
}

function afficher_noms(){
	var span = document.getElementById('nom_joueur_2');
	var joueur_co_2 = getCookie("CookieLogin2");
	var joueur_co_1 = getCookie("CookieLogin");
	
	if(joueur_co_2){	
		span.innerHTML = ' et '+joueur_co_2;
		span.style.display ='block';
	}else{
		joueur_co_2 = "Joueur 2";
	}
	document.getElementById('nomJoueur2').innerHTML = joueur_co_2;
	document.getElementById('nomJoueur1').innerHTML = joueur_co_1;	
}

function change_joueur(joueur){
	if(joueur == "J1"){
		//on cache les cartes de J2
		document.getElementById('cartesHoriJ2').style.display = 'none';
		alert("A "+joueur_1+" de jouer");
		document.getElementById('cartesHoriJ1').style.display = 'block';
	}else{
		document.getElementById('cartesHoriJ1').style.display = 'none';
		alert("A "+joueur_2+" de jouer");
		document.getElementById('cartesHoriJ2').style.display = 'block';
	}
}

function tri(a, b){
	a = a.substring(0, a.indexOf("C"));
	b = b.substring(0, b.indexOf("C"));
	
	return parseInt(a) - parseInt(b);
}

function change_nb_cartes(id_div, nb){
	//affiche le nb de cartes des joueurs
	document.getElementById(id_div).innerHTML = " ("+nb+" cartes)";
}

function save_partie (){
	//on cree la partie dans la bdd ou on l'update
	
	/*$.ajax({
		type: 'PUT',
		contentType: 'application/json',
		url : rootURL + '/Jouer/SauverLocal',
		dataType : 'json',
		data: toJSON(),
		success : function(data){
			id_partie = data.id_partie;
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});*/
}

function toJSON() {
	return JSON.stringify({
		"login1": joueur_1, 
		"login2": joueur_2,
		"score1": score_J1,
		"score2": score_J2,
		"nb_tour": nb_tour
		});
}

function new_tour(){
	alert("nouveau tour !");
	nb_tour ++;
	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Jouer/NouveauTourLocal',
		dataType : 'json',
		success : function(data){
			var i =0;	
			var j=0;
			infos = data;
			var debut = infos.debut;
			
			for(key1 in infos.cartesJ1){
				cartes1[i] = infos.cartesJ1[key1];
				i++;
			}

			for(key2 in infos.cartesJ2){
				cartes2[j] = infos.cartesJ2[key2];
				j++;
			}
			
			i=0;
			j=0;
			
			//tri les cartes par ordre croissant
			cartes1.sort(tri);
			cartes2.sort(tri);
			
			nb_cartes_J1 = cartes1.length;
			nb_cartes_J2 = cartes2.length;
			
			change_nb_cartes("nb_J1", nb_cartes_J1);
			change_nb_cartes("nb_J2", nb_cartes_J2);
			
			change_joueur(debut);
			
			
			//les cartes de J1
			for(carte in cartes1){
				document.getElementById('cartesHoriJ1').innerHTML += "<span id='carte1_"+i+"' onclick='javascript:jouer_carte(\"J1\", "+i+")'>"+cartes1[carte]+"</span> ";
				i++;
			}
			//les cartes de J2
			for(carte2 in cartes2){
				document.getElementById('cartesHoriJ2').innerHTML += "<span id='carte2_"+j+"' onclick='javascript:jouer_carte(\"J2\", "+j+")'>"+cartes2[carte2]+"</span> ";
				j++;
			}
					
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

function jouer_carte(joueur, num_carte){
	//alert("jeu : "+num_carte+" de "+joueur);

	if(joueur == "J1"){
		nb_cartes_J1 -=1;
		change_nb_cartes("nb_J1", nb_cartes_J1);
		document.getElementById('carte1_'+num_carte).style.display = 'none';
		
		if(nb_cartes_J1 ==0){ //si le J1 a gagné
			score_J2 -= 2;
			score_J1 += 2;
			
			if(joueur_2 != "Joueur 2")
				save_partie();
			
			document.getElementById('joueur2').style.display = 'none';
			document.getElementById('joueur1').style.display = 'none';
			document.getElementById('plateau2').innerHTML = "<h5>Bravo "+joueur_1+" ! Vous avez gagne ce tour.</h5><br/> <button onClick='javascript:new_tour();'>Nouveau tour</button>";
		}else{	
			change_joueur("J2");
		}
	}else{
		nb_cartes_J2 -=1;
		change_nb_cartes("nb_J2", nb_cartes_J2);
		document.getElementById('carte2_'+num_carte).style.display = 'none';
		
		if(nb_cartes_J2 ==0){ //si le J2 a gagné
			score_J1 -= 2;
			score_J2 += 2;
			
			if(joueur_2 != "Joueur 2")
				save_partie();
			
			document.getElementById('joueur2').style.display = 'none';
			document.getElementById('joueur1').style.display = 'none';
			document.getElementById('plateau2').innerHTML = "<h5>Bravo "+joueur_2+" ! Vous avez gagne ce tour.</h5><br/> <button onClick='javascript:new_tour();'>Nouveau tour</button>";
		}else{	
			change_joueur("J1");
		}
	}
}

function demarrer_partie(){
	var joueur_co_2 = getCookie("CookieLogin2");
	var joueur_co_1 = getCookie("CookieLogin");
	
	if(!joueur_co_2){	
		joueur_co_2 = "Joueur 2";
	}
	
	joueur_1 = joueur_co_1;
	joueur_2 = joueur_co_2;
	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Jouer/Local',
		dataType : 'json',
		data: {
			"login1": joueur_co_1, 
			"login2": joueur_co_2 
		},
		success : function(data){
			var i =0;	
			var j=0;
			infos = data;
			var debut = infos.debut;
			
			for(key1 in infos.cartesJ1){
				cartes1[i] = infos.cartesJ1[key1];
				i++;
			}

			for(key2 in infos.cartesJ2){
				cartes2[j] = infos.cartesJ2[key2];
				j++;
			}
			
			i=0;
			j=0;
			
			//tri les cartes par ordre croissant
			cartes1.sort(tri);
			cartes2.sort(tri);
			
			nb_cartes_J1 = 1;//cartes1.length;
			nb_cartes_J2 = cartes2.length;
			
			change_nb_cartes("nb_J1", nb_cartes_J1);
			change_nb_cartes("nb_J2", nb_cartes_J2);
			
			change_joueur(debut);
			
			//les cartes de J1
			for(carte in cartes1){
				document.getElementById('cartesHoriJ1').innerHTML += "<span id='carte1_"+i+"' onclick='javascript:jouer_carte(\"J1\", "+i+")'>"+cartes1[carte]+"</span> ";
				i++;
			}
			//les cartes de J2
			for(carte2 in cartes2){
				document.getElementById('cartesHoriJ2').innerHTML += "<span id='carte2_"+j+"' onclick='javascript:jouer_carte(\"J2\", "+j+")'>"+cartes2[carte2]+"</span> ";
				j++;
			}				
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

$('#connect_2').click(function(){
	Connect_2();
	return false;
});

$('#local').click(function(){
	self.location.href = "./Partie_locale.html";
	return false;
});

$('#retour_selection').click(function(){
	self.location.href = "./SelectionPartie.html";
	return false;
});


function Connect_2(){	
	$.ajax({
		type: 'GET',
		url : rootURL + '/Player/Connexion',
		dataType : 'json',
		data: {
			"login": $('#login_2').val(), 
			"mdp": $('#mdp_2').val() 
		},
		success : function(data){
			CurrentPlayer2 = data;
			
			login_2 = $('#login_2').val();
			mdp_2 = $('#mdp_2').val();
			
			if(CurrentPlayer2.login == login_2 && CurrentPlayer2.mdp == login_2) {
				setCookie("CookieLogin2", CurrentPlayer2.login);
				self.location.href = "./Partie_locale.html";
			}
			else{
				alert("Mauvaise combinaison login/password");
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown+" "+jqXHR.status);
		}
	});
}

