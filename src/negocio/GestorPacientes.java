package negocio;

import modelo.Paciente;
import java.util.ArrayList;

public class GestorPacientes {

    private ArrayList<Paciente> pacientes = new ArrayList<>();

    public boolean registrarPaciente(Paciente nuevo) {
        if (nuevo == null) {
            return false;
        }

        for (Paciente paciente : pacientes) {
            if (paciente.getCedula().equals(nuevo.getCedula())) {
                return false;
            }
        }

        pacientes.add(nuevo);
        return true;
    }

    public ArrayList<Paciente> listarPacientes() {
        return new ArrayList<>(pacientes);
    }
}
