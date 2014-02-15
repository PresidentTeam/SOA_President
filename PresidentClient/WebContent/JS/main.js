/**
 * 
 * Fichier javascript contenant toutes les interactions entre les interfaces.
 */

//The root URL for the RESTful services
var rootURL = "http://localhost:8080/President/rest/";

var CurrentPlayer;

$('#play').click(function(){
	Play();
	return false;
});

$('#rules').click(function(){
	Rules();
	return false;
});

$('#homepage').click(function(){
	HomePage();
	return false;
});

$('#inscrire').click(function(){
	Inscription();
	return false;
});

$('#menu').click(function(){
	Menu();
	return false;
});

$('#newPlay').click(function(){
	NewGame();
	return false;
});

$('#PartieLigne').click(function(){
	if(!CurrentPlayer){
		Connexion();
	}
	else{
		self.location.href = "http://localhost:8080/PresidentClient/Menu.html";
	}
	return false;
});

$('#PartieLocale').click(function(){
	Connexion();
	return false;
});

function Play(){
	self.location.href = "http://localhost:8080/PresidentClient/SelectionPartie.html";
}

function Rules(){
	self.location.href = "http://localhost:8080/PresidentClient/Regles.html";
}

function HomePage(){
	self.location.href = "http://localhost:8080/PresidentClient/";
}

function Connexion(){
	self.location.href = "http://localhost:8080/PresidentClient/Connexion.html";
}

function Inscription(){
	self.location.href = "http://localhost:8080/PresidentClient/Inscription.html";
}

function Menu(){
	self.location.href = "http://localhost:8080/PresidentClient/Menu.html";
}

function NewGame(){
	self.location.href = "http://localhost:8080/PresidentClient/CreationPartie.html";
}