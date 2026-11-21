
package Pruebas;

import modelo.Toma;
import modelo.EsquemaDosificacion;
import java.time.LocalDateTime;

public class PruebaToma {

    public static void main(String[] args) {

        EsquemaDosificacion esquema = new EsquemaDosificacion();

        LocalDateTime programada = LocalDateTime.now().minusMinutes(20);
        LocalDateTime limite = programada.plusMinutes(10);

        // PRUEBA DE CREACION
        Toma toma = new Toma(esquema, programada, limite);

        System.out.println("=== PRUEBA DEL MODULO 4 ===");
        System.out.println("Estado inicial: " + toma.getEstado());
        System.out.println("Hora programada: " + toma.getFechaHoraProgramada());
        System.out.println("Hora limite: " + toma.getFechaHoraLimite());

        // PRUEBA DE CONFIRMACION
        System.out.println("\n=== PRUEBA DE CONFIRMACION ===");

        LocalDateTime horaConfirmacion = programada.minusMinutes(10);

        boolean resultado = toma.confirmar(
                horaConfirmacion,
                "Cuidador",
                "Toma confirmada correctamente"
        );

        System.out.println("¿Se confirmó?: " + resultado);
        System.out.println("Nuevo estado: " + toma.getEstado());
        System.out.println("Confirmada por: " + toma.getConfirmadaPor());
        System.out.println("Observaciones: " + toma.getObservaciones());

        // PRUEBA DE DOBLE CONFIRMACION
        System.out.println("\n=== PRUEBA DE DOBLE CONFIRMACION ===");

        boolean segundaConfirmacion = toma.confirmar(
                LocalDateTime.now(),
                "Otro cuidador",
                "Segunda confirmación"
        );

        System.out.println("¿Permite confirmar nuevamente?: "
                + segundaConfirmacion);

        // PRUEBA DE OMISION
        System.out.println("\n=== PRUEBA DE OMISION ===");

        Toma tomaOmitida = new Toma(esquema, programada, limite);

        System.out.println("Estado inicial: " + tomaOmitida.getEstado());

        tomaOmitida.omitir();

        System.out.println("Estado después de omitir: "
                + tomaOmitida.getEstado());

        // PRUEBA DE CANCELACION
        System.out.println("\n=== PRUEBA DE CANCELACION ===");

        Toma tomaCancelada = new Toma(esquema, programada, limite);

        System.out.println("Estado inicial: " + tomaCancelada.getEstado());

        tomaCancelada.cancelar();

        System.out.println("Estado después de cancelar: "
                + tomaCancelada.getEstado());

        boolean confirmarCancelada = tomaCancelada.confirmar(
                LocalDateTime.now(),
                "Cuidador",
                "Intento de confirmar toma cancelada"
        );

        System.out.println("¿Permite confirmar una toma cancelada?: "
                + confirmarCancelada);

        // PRUEBA DE VENCIMIENTO
        System.out.println("\n=== PRUEBA DE VENCIMIENTO ===");

        Toma tomaVencida = new Toma(esquema, programada, limite);

        boolean vencida = tomaVencida.estaVencida(LocalDateTime.now());

        System.out.println("¿La toma está vencida?: " + vencida);

        // PRUEBA DE CLASIFICACION
        System.out.println("\n=== PRUEBA DE CLASIFICACION ===");

        String clasificacion = toma.clasificar();

        System.out.println("Clasificación de la toma: " + clasificacion);

        // PRUEBA DE TOMA TARDIA
        System.out.println("\n=== PRUEBA DE TOMA TARDIA ===");

        Toma tomaTardia = new Toma(esquema, programada, limite);

        LocalDateTime horaTardia = LocalDateTime.now().minusMinutes(5);

        boolean confirmadaTardia = tomaTardia.confirmar(
                horaTardia,
                "Cuidador",
                "Confirmación después de la hora límite"
        );

        System.out.println("¿Se confirmó?: " + confirmadaTardia);

        System.out.println("Clasificación: "
                + tomaTardia.clasificar());
    }
}

