import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnexioBD {
    private static final String URL = "jdbc:mysql://localhost:3306/clinica_vet";
    private static final String USER = "root";
    private static final String PASS = "1234samuel";

    private static Connection conectar() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASS);
        if (con == null) {
            throw new SQLException("No se ha podido establecer la conexion.");
        }
        return con;
    }

    // Guardar Registro de mascota
    public void guardarMascota(Mascota m) throws SQLException {
        String sql = "INSERT INTO mascotes (nom_mascota, especie, edat, propietari_nom, propietari_dni) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, m.getNombre());
            pst.setString(2, m.getEspecie());
            pst.setInt(3, m.getEdad());
            pst.setString(4, m.getPropietario());
            pst.setString(5, m.getDni());
            pst.executeUpdate();
        }
    }

    // Buscar mascota por DNI
    public String BuscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM mascotes WHERE propietari_dni = ?";
        try (Connection con = conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, dni);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return "Mascota: " + rs.getString("nom_mascota") + " (" + rs.getString("especie") + ")\n" +
                            "Edad: " + rs.getInt("edat") + "\n" +
                            "Propietario: " + rs.getString("propietari_nom");
                } else {
                    return "No se encontró ninguna mascota con ese DNI.";
                }
            }
        }
    }

    // Dar de baja a una Mascota
    public void DarDeBajaMascota(Mascota m) throws SQLException {
        String sql = "DELETE FROM mascotes WHERE propietari_dni = ?";
        try (Connection con = conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, m.getDni());
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se ha encontrado ninguna  mascota con ese   DNI para  darle  de baja.");
            }
        }
    }

    // Modificar los datos de una mascota basándose en su DNI
    public void ModificarDatosMascota(Mascota m) throws SQLException {
        String sql = "UPDATE mascotes SET nom_mascota = ?, especie = ?, edat = ?, propietari_nom = ? WHERE propietari_dni = ?";
        try (Connection con = conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, m.getNombre());
            pst.setString(2, m.getEspecie());
            pst.setInt(3, m.getEdad());
            pst.setString(4, m.getPropietario());
            pst.setString(5, m.getDni());
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se ha podido actualizar. El DNI no existe.");
            }
        }
    }

    // Buscar todas las mascotas que tengan una edad concreta
    public String BuscarMascotasPorEdad(int edat) throws SQLException {
        String sql = "SELECT * FROM mascotes WHERE edat = ?";
        StringBuilder sb = new StringBuilder();
        try (Connection con = conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, edat);
            try (ResultSet rs = pst.executeQuery()) {
                boolean hayResultados = false;
                while (rs.next()) {
                    hayResultados = true;
                    sb.append("Mascota: ").append(rs.getString("nom_mascota"))
                            .append(" (").append(rs.getString("especie")).append(") | ")
                            .append("Propietario: ").append(rs.getString("propietari_nom"))
                            .append(" (").append(rs.getString("propietari_dni")).append(")\n");
                }
                if (!hayResultados) {
                    return "No sehan encontrado mascotas con  la edad de: " + edat + " años.";
                }
            }
        }
        return sb.toString();
    }
}