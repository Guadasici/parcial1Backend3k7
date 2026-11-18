package utnfc.backend.parcial;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParserLiquidaciones {
    private static final String CABECERA = "id,rubro,importeCentavos,bonificacionCentavos,estado";
    // defino nueva cabecera
    private static final String NUEVA_CABECERA = "id,rubro,importeCentavos,bonificacionCentavos,estado,diasCobertura";

    public ResultadoParseo leer(Path archivo) throws IOException {
        List<Consumo> consumos = new ArrayList<>();
        Map<Integer, String> errores = new TreeMap<>();
        Map<Integer, String> descartes = new TreeMap<>();
        int leidas = 0;
        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            // declaro columnas esperadas
            int columnasEsperadas;
            // guarda la primera linea
            String primeraLiena = reader.readLine();

            // declaro cuantas columnas voy a esperar
            if (CABECERA.equals(primeraLiena)) {
                columnasEsperadas = 5;
            } else if (NUEVA_CABECERA.equals(primeraLiena)) {
                columnasEsperadas = 6;
            } else {
                throw new IllegalArgumentException("encabezado incorrecto");
            }
            String linea;
            while ((linea = reader.readLine()) != null) {
                leidas++;
                int numero = leidas + 1;
                try {
                    String[] c = Arrays.stream(linea.split(",", -1)).map(String::strip).toArray(String[]::new);
                    // si no coincide con las columnas esperadas, q tire error
                    if (c.length != columnasEsperadas) {
                        throw new IllegalArgumentException("cantidad de columnas incorrecta");
                    }
                    if (c[4].equals("ANULADO")) {
                        descartes.put(numero, "ANULADO");
                        continue;
                    }
                    if (!c[4].equals("APROBADO")) throw new IllegalArgumentException("estado desconocido: " + c[4]);
                    consumos.add(Consumo.desdeCampos(c));
                } catch (IllegalArgumentException e) {
                    errores.put(numero, e.getMessage());
                }
            }
        }
        return new ResultadoParseo(consumos, leidas, errores, descartes);
    }
}
