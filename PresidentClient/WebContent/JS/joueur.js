/**
 * 
 * Ficher Javascript pour toutes les actions sur les joueurs
 */

//The root URL for the RESTful services
var rootURL = "http://localhost:8080/PresidentServeur/rest/Joueur/";

//retrouve la liste de tous les joueurs au demarrage 


$('#connection').click(function(){
	Connect( $('#login').val(), $('#mdp').val() );
	return false;
});

$('#creerLogin').click(function(){
	console.log("test");
	var test = VerificationPassword( $('#password').val(), $('#VerifPassword').val() );
	if( test != null)
		InscrireJoueur();
	return false;
});

function Connect(login,mdp){
	console.log('Recherche login-mdp ' + login + ' ' + mdp);
	$.ajax({
		type: 'GET',
		url : rootURL + 'query?login=' + login + '&mdp=' + mdp,
		dataType : 'json',
		success : function(data){
			console.log('Connexion faite par ' + login + mdp);
			CurrentPlayer = data;
			var d = login+mdp;
			console.log(d);
			if(CurrentPlayer.login == login && CurrentPlayer.mdp == mdp) {
				createCookie(CookieLogin, CurrentPlayer.login);
				self.location.href = "http://localhost:8080/PresidentClient/Menu.html";
			}
			else{
				alert("Mauvaise combinaison login");
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert("Chemin non accessible :" + errorThrown);
		}
	});
}

function createCookie(name,value){
	var date = new Date();
	date.setTime(date.getTime()+(days*24*60*60*1000));
	var expires = "; expires="+date.toGMTString();
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name,"",-1);
}

function VerificationPassword(mdp, verifMdp){
	if(mdp != verifMdp){
		var d = $("#erreurPwd");
		d.append('Mauvais mot de passe');
		return null;
	}
	else {
		return mdp;
	}
}

function InscrireJoueur(){
	console.log('Inscription joueur :');
	$.ajax({
		type: 'PUT',
		contentType : 'application/json',
		url: rootURL + 'ajoutJoueur',
		dataType: 'json',
		data : joueurFormToJSON(),
		success : function(data, textStatus, jqXHR){
			alert('Joueur inscrit');
		},
		error : function(jqHXR, testStatus, errorThrown){
			alert('Probleme d\'inscription');
		}
	});
}

function joueurFormToJSON(){
	console.log("adressage des infos");
	return JSON.stringify({
		"nom": $('#nom').val(),
		"prenom": $('#prenom').val(),
		"login": $('#login').val(),
		"mail": $('#mail').val(),
		"mdp" : $('#password').val()
	});
}

function findAllJoueurs() {
	console.log('findAll');
	$.ajax({
		type: 'GET',
		url: rootURL,
		dataType: "json", 
	});
}