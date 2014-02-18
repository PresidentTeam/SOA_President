/**
 * 
 * Ficher Javascript pour toutes les actions sur les joueurs
 */

//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest";

//retrouve la liste de tous les joueurs au demarrage 

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

function afficher_nom_joueur_2(){
	var span = document.getElementById('nom_joueur_2');
	var joueur_co_2 = getCookie("CookieLogin2");
	
	if(joueur_co_2){	
		span.innerHTML = ' et '+joueur_co_2;
		span.style.display ='block';
	}
			
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

