package utnfc.backend.parcial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class EvolucionTest {
    @TempDir Path dir;
    private ResultadoParseo leer(String filas) throws IOException {
        Path p=dir.resolve("nuevo.csv");
        Files.writeString(p,"id,rubro,importeCentavos,bonificacionCentavos,estado,diasCobertura\n"+filas);
        return new ParserLiquidaciones().leer(p);
    }
    @Test void nuevasTarifasYColector() throws IOException {
        var r=leer("A,Alimentos,120000,20000,APROBADO,0\nB,Alimentos,120000,20000,APROBADO,90\nC,Combustible,520000,20000,APROBADO,90\n");
        assertEquals(3,r.getProcesadas()); assertEquals(0,r.getInvalidas());
        assertEquals(2000,r.getConsumos().get(0).comision()); assertEquals(2600,r.getConsumos().get(1).comision());
        assertEquals(10600,r.getConsumos().get(2).comision());
        var l=new LiquidadorTarjetas(r.getConsumos());
        assertEquals(15200,l.getTotalLiquidacion()); assertEquals(Map.of("Alimentos",4600,"Combustible",10600),l.totalesPorRubro());
        assertEquals(2,l.filtrar(c->c.comision()>2000).size());
    }
    @Test void limitesYClasificacion() throws IOException {
        var r=leer("A,Alimentos,520100,20000,APROBADO,90\nB,Alimentos,1,0,APROBADO,90\nC,Alimentos,1,0,APROBADO,\nD,Alimentos,1,0,APROBADO,15\nE,Alimentos,0,0,ANULADO,60\n");
        assertEquals(5,r.getLeidas()); assertEquals(1,r.getProcesadas()); assertEquals(3,r.getInvalidas());
        assertEquals(1,r.getDescartadas()); assertEquals(600,r.getConsumos().get(0).comision());
    }
    @Test void formatoAnteriorSigueVigente() throws IOException {
        Path p=dir.resolve("viejo.csv");
        Files.writeString(p,"id,rubro,importeCentavos,bonificacionCentavos,estado\nA,Alimentos,120000,20000,APROBADO\n");
        var r=new ParserLiquidaciones().leer(p); assertEquals(1,r.getProcesadas()); assertEquals(2000,r.getConsumos().get(0).comision());
    }
    @Test void anchoDebeCoincidirConCabecera() throws IOException {
        var r=leer("A,Alimentos,10000,0,APROBADO\nB,Alimentos,10000,0,APROBADO,0,extra\n");
        assertEquals(2,r.getInvalidas()); assertEquals(0,r.getProcesadas());
    }
}
