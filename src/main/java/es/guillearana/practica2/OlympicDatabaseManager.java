package es.guillearana.practica2;

import java.sql.*;
import java.io.*;
import java.util.*;

public class OlympicDatabaseManager {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/olympics";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "password";
    private static final String SQLITE_URL = "jdbc:sqlite:Olympics.db";

    // Método para conectar a MySQL
    private static Connection connectToMySQL() throws SQLException {
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }

    // Método para conectar a SQLite
    private static Connection connectToSQLite() throws SQLException {
        return DriverManager.getConnection(SQLITE_URL);
    }

    // Método para crear tablas
    private static void createTables(Connection conn) {
        String createAthletes = "CREATE TABLE IF NOT EXISTS athletes (" +
                "athlete_id INT PRIMARY KEY, name VARCHAR(255), sex CHAR(1), height FLOAT, weight FLOAT)";
        String createParticipations = "CREATE TABLE IF NOT EXISTS participations (" +
                "participation_id INT PRIMARY KEY, athlete_id INT, sport VARCHAR(255), " +
                "event VARCHAR(255), team VARCHAR(255), year INT, medal VARCHAR(255), " +
                "FOREIGN KEY(athlete_id) REFERENCES athletes(athlete_id))";
        // Continúa con las demás tablas (olympic_editions, sports, events)

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createAthletes);
            stmt.executeUpdate(createParticipations);
            // Ejecutar la creación de las otras tablas
        } catch (SQLException e) {
            System.out.println("Error creando las tablas: " + e.getMessage());
        }
    }

    // Método para cargar datos desde el CSV
    private static void loadDataFromCSV(Connection conn, String filePath) {
        String insertAthlete = "INSERT INTO athletes (athlete_id, name, sex, height, weight) VALUES (?, ?, ?, ?, ?)";
        String insertParticipation = "INSERT INTO participations (participation_id, athlete_id, sport, event, team, year, medal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             PreparedStatement psAthlete = conn.prepareStatement(insertAthlete);
             PreparedStatement psParticipation = conn.prepareStatement(insertParticipation)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                // Asumiendo que el CSV tiene el formato: athlete_id, name, sex, height, weight, sport, event, team, year, medal
                // Insertar atleta
                psAthlete.setInt(1, Integer.parseInt(fields[0]));
                psAthlete.setString(2, fields[1]);
                psAthlete.setString(3, fields[2]);
                psAthlete.setFloat(4, Float.parseFloat(fields[3]));
                psAthlete.setFloat(5, Float.parseFloat(fields[4]));
                psAthlete.executeUpdate();

                // Insertar participación
                psParticipation.setInt(1, Integer.parseInt(fields[5])); // participation_id
                psParticipation.setInt(2, Integer.parseInt(fields[0])); // athlete_id
                psParticipation.setString(3, fields[5]);
                psParticipation.setString(4, fields[6]);
                psParticipation.setString(5, fields[7]);
                psParticipation.setInt(6, Integer.parseInt(fields[8]));
                psParticipation.setString(7, fields[9]);
                psParticipation.executeUpdate();
            }
            System.out.println("Datos cargados correctamente.");
        } catch (IOException | SQLException e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
        }
    }

    // Método principal que maneja el menú
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Crear BBDD MySQL");
            System.out.println("2. Crear BBDD SQLite");
            System.out.println("3. Listar Deportistas en Diferentes Deportes");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Ingrese la ruta del archivo CSV: ");
                    String mysqlCSV = sc.next();
                    try (Connection conn = connectToMySQL()) {
                        createTables(conn);
                        loadDataFromCSV(conn, mysqlCSV);
                    } catch (SQLException e) {
                        System.out.println("Error de conexión a MySQL: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Ingrese la ruta del archivo CSV: ");
                    String sqliteCSV = sc.next();
                    try (Connection conn = connectToSQLite()) {
                        createTables(conn);
                        loadDataFromCSV(conn, sqliteCSV);
                    } catch (SQLException e) {
                        System.out.println("Error de conexión a SQLite: " + e.getMessage());
                    }
                    break;
                case 3:
                    // Implementación para listar deportistas
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}
