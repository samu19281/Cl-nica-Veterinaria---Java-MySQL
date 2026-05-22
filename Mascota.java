public class Mascota {
    private String nombre;
    private String especie;
    private int edad;
    private String propietario;
    private String dni;

    public Mascota(String nombre, String especie, int edad, String propietario, String dni) {
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.propietario = propietario;
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "Mascota [nombre=" + nombre + ", especie=" + especie + ", edad=" + edad + ", propietario=" + propietario
                + ", dni=" + dni + "]";
    }

}
