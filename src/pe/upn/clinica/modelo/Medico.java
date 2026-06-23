package pe.upn.clinica.modelo;

public class Medico extends Persona {

    private String cmp;
    private String especialidad;

    public Medico() {
        super();
    }

    public Medico(int id, String nombre, String apellido, String dni, String telefono, String cmp, String especialidad) {
        super(id, nombre, apellido, dni, telefono);
        this.cmp = cmp;
        this.especialidad = especialidad;
    }

    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    // POLIMORFISMO: implementación específica para Medico
    @Override
    public String mostrarInfo() {
        return "MEDICO | CMP: " + cmp + " | Nombre: " + getNombre() + " " + getApellido()
                + " | Especialidad: " + especialidad + " | Tel: " + getTelefono();
    }
}