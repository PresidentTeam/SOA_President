package ressources;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BDD 
{
	private static BDD instance = new BDD();
	
	public static Connection conn = null;
	public static Statement stmt = null;
	public static ResultSet res = null;

	public static String userName = "root";
	public static String password = "root";
	
	public static String driver = "com.mysql.jdbc.Driver";
	public static String dbName = "/soa_president";
	public static String url = "jdbc:mysql://localhost:";
	public static String port = "8889";

	public BDD(){
		
		System.out.println("test connexion");
		try{
			Class.forName(driver);
			}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection createConnection(){
		try {
			conn = DriverManager.getConnection(url+port+dbName,userName,password);
			System.out.println("Connected to the database");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection getConnection() {
        return instance.createConnection();
    }

	public int insertJoueur(String nom, String prenom, String login, String mail, String mdp){
		ResultSet resultats = null;
		int idGenere = -1;
		try {
			stmt.executeUpdate("INSERT INTO Joueur (nom, prenom, login, mail, mdp) values ('"+nom+"', '"+prenom+"','"+login+"','"+mail+"','"+mail+"','"+mdp+"')", Statement.RETURN_GENERATED_KEYS);
			resultats = stmt.getGeneratedKeys();
			if (resultats.next()) {
				idGenere = resultats.getInt(1);
			}
			resultats.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idGenere;
	}
}