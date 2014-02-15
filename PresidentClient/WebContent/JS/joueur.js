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
				setCookie("CookieLogin", CurrentPlayer.login);
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

function setCookie(sName, sValue) {
    var today = new Date(), expires = new Date();
    expires.setTime(today.getTime() + (365*24*60*60*1000));
    document.cookie = sName + "=" + encodeURIComponent(sValue) + ";expires=" + expires.toGMTString();
}

function getCookie(sName) {
    var cookContent = document.cookie, cookEnd, i, j;
    var sName = sName + "=";
    for (i=0, c=cookContent.length; i<c; i++) {
            j = i + sName.length;
            if (cookContent.substring(i, j) == sName) {
                    cookEnd = cookContent.indexOf(";", j);
                    if (cookEnd == -1) {
                            cookEnd = cookContent.length;
                    }
                    return decodeURIComponent(cookContent.substring(j, cookEnd));
            }
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
		url: rootURL + 'ajoutJoueur/',
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
	console.log($('#nom').val());
	return JSON.stringify({
		"nom": $('#nom').val(),
		"prenom": $('#prenom').val(),
		"login": $('#login').val(),
		"mail": $('#mail').val(),
		"mdp" : $('#password').val(),
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