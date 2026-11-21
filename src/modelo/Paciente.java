
package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Paciente extends Persona {

    private String cedula;
    private LocalDate fechaNacimiento;
    private String enfermedadesCronicas;
    private String alergias;
    private String condicionesRelevantes;
    private boolean activo;


    private ArrayList<VinculoCuidador> vinculos = new ArrayList<>();
    private ArrayList<Medicamento> medicamentos = new ArrayList<>();
    private ArrayList<EsquemaDosificacion> esquemas = new ArrayList<>();
    private ArrayList<CitaMedica> citas = new ArrayList<>();
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<AdvertenciaFarmacologica> advertencias = new ArrayList<>();
    private ArrayList<ReporteSeguimiento> reportes = new ArrayList<>();
}