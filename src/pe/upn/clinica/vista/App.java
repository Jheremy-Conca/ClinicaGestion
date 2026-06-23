package pe.upn.clinica.vista;

import java.awt.EventQueue;

public class App {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaPrincipal ventana = new VentanaPrincipal();
                    ventana.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}