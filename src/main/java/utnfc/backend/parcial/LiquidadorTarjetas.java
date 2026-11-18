package utnfc.backend.parcial;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LiquidadorTarjetas {
    private final List<Consumo> consumos;
    public LiquidadorTarjetas(List<Consumo> consumos) { this.consumos = List.copyOf(consumos); }
    public List<Consumo> getConsumos() { return consumos; }
    public int getTotalLiquidacion() { return consumos.stream().mapToInt(Consumo::comision).sum(); }
    public int getPromedioComision() {
        return consumos.isEmpty() ? 0 : getTotalLiquidacion() / consumos.size();
    }
    public Optional<Consumo> getMaximo() {
        return consumos.stream().max(Comparator.comparingInt(Consumo::comision));
    }
    public Optional<Consumo> getMinimo() {
        return consumos.stream().min(Comparator.comparingInt(Consumo::comision));
    }
    public List<Consumo> filtrar(Predicate<Consumo> criterio) {
        return consumos.stream().filter(criterio).toList();
    }
    public Map<String, Integer> totalesPorRubro() {
        return consumos.stream().collect(Collectors.groupingBy(Consumo::getRubro, Collectors.summingInt(Consumo::comision)));
    }
    public String panorama() {
        return "Total: " + getTotalLiquidacion()
                + " | promedio: " + getPromedioComision()
                // agrege eso para mostrar lo hecho abajo
                + " | por modalidad: " + new java.util.TreeMap<>(contarConsumosPorModlaidad())
                + " | por rubro: " + new java.util.TreeMap<>(totalesPorRubro())
                + " | hasta $1000: " + filtrar(c -> c.getCentavos() <= 100_000).size();
    }

    // Agregar el conteo por modalidad, es decir si es asegurado o sin seguro, va a ir contando para luego devolver el resultado
    public Map<String, Long> contarConsumosPorModlaidad() {
        return consumos.stream()
                .collect(Collectors.groupingBy(
                        c -> (c instanceof  ConsumoAsegurado) ? "ASEGURADO" : "SIN_SEGURO",
                        Collectors.counting()));
    }

}
