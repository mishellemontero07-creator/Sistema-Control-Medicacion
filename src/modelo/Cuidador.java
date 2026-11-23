package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Cuidador hereda el nombre de Persona.
// Un cuidador puede tener varios pacientes asignados.
public class Cuidador extends Persona {
    private String telefono;
    private ArrayList<VinculoCuidador> vinculos = new ArrayList<>();

    public Cuidador(String nombreCompleto, String telefono) {
        super(nombreCompleto);
        setTelefono(telefono);
    }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) {
        if (!validarTelefonoIngresado(telefono)) {
            throw new IllegalArgumentException("Ingrese un teléfono ecuatoriano válido");
        }
        this.telefono = telefono;
    }

    // Regla del proyecto: celular 09 + 8 cifras, o fijo 0[2-7] + 7 cifras.
    // Puede ajustarse si el equipo decide aceptar otros formatos.
    public static boolean validarTelefonoIngresado(String telefono) {
        return telefono != null && telefono.matches("(09[0-9]{8}|0[2-7][0-9]{7})");
    }

    public boolean validarTelefono() {
        return validarTelefonoIngresado(telefono);
    }

    // Devuelve solo los pacientes que todavía tienen un vínculo activo.
    public ArrayList<Paciente> getPacientes() {
        ArrayList<Paciente> pacientes = new ArrayList<>();
        for (VinculoCuidador vinculo : vinculos) {
            if (vinculo.estaActivo() && !pacientes.contains(vinculo.getPaciente())) {
                pacientes.add(vinculo.getPaciente());
            }
        }
        return pacientes;
    }

    // El paciente crea el vínculo y el cuidador guarda una referencia al MISMO objeto.
    public boolean agregarVinculo(VinculoCuidador vinculo) {
        if (vinculo == null || vinculo.getCuidador() != this || vinculos.contains(vinculo)) {
            return false;
        }
        for (VinculoCuidador anterior : vinculos) {
            if (anterior.estaActivo() && vinculo.estaActivo()
                    && anterior.getPaciente() == vinculo.getPaciente()) {
                return false; // No repetimos vínculos activos con el mismo paciente.
            }
        }
        vinculos.add(vinculo);
        return true;
    }

    // Conserva tanto los vínculos activos como los históricos.
    public ArrayList<VinculoCuidador> buscarVinculosPorPaciente(Paciente paciente) {
        ArrayList<VinculoCuidador> resultado = new ArrayList<>();
        for (VinculoCuidador vinculo : vinculos) {
            if (vinculo.getPaciente() == paciente) {
                resultado.add(vinculo);
            }
        }
        return resultado;
    }

    public int contarPacientesActivos() {
        int contador = 0;
        for (Paciente paciente : getPacientes()) {
            if (paciente.isActivo()) {
                contador++;
            }
        }
        return contador;
    }

    // Estas tres operaciones usan clases que programa otro integrante.
    // Requieren que Toma tenga getFechaHoraProgramada() y getEstado(),
    // además del confirmar(...) que está definido en el UML.
    public ArrayList<Toma> consultarTomasPendientes(Paciente paciente, LocalDate fecha) {
        ArrayList<Toma> pendientes = new ArrayList<>();
        if (paciente == null || fecha == null || !getPacientes().contains(paciente)) {
            return pendientes;
        }
        for (Toma toma : paciente.getTomas()) {
            if (toma.getFechaHoraProgramada() != null
                    && toma.getFechaHoraProgramada().toLocalDate().equals(fecha)
                    && "pendiente".equalsIgnoreCase(toma.getEstado())) {
                pendientes.add(toma);
            }
        }
        return pendientes;
    }

    public ArrayList<Alerta> consultarAlertasPendientes(Paciente paciente) {
        if (paciente == null || !getPacientes().contains(paciente)) {
            return new ArrayList<>();
        }
        return paciente.buscarAlertasPendientes();
    }

    public boolean confirmarTomaSupervisada(Paciente paciente, Toma toma,
                                            LocalDateTime ahora, String observaciones) {
        if (paciente == null || toma == null || ahora == null
                || !getPacientes().contains(paciente)
                || !paciente.getTomas().contains(toma)) {
            return false;
        }
        return toma.confirmar(ahora, getNombreCompleto(), observaciones);
    }
}
