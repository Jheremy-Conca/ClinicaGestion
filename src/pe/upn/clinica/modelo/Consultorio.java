package pe.upn.clinica.modelo;

public class Consultorio {

    private int id;
    private String numero;
    private int piso;
    private boolean disponible;

    public Consultorio() {
    }

    public Consultorio(int id, String numero, int piso, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.piso = piso;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Consultorio " + numero + " (Piso " + piso + ")";
    }
}