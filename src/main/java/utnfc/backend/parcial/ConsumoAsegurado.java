package utnfc.backend.parcial;
// ejercicio polimorfico


public class ConsumoAsegurado extends Consumo {
    private final int diasCobertura;

    public ConsumoAsegurado(String id, String rubro, int importe, int bonificacion, int diasCobertura) {
        super(id, rubro, importe, bonificacion);
        // se verifica si los centavos son mayores a 500k q es el max de rango
        if (getCentavos() > 500_000) {
            throw new IllegalArgumentException("fuera de rango los centavos");
        }
        // se verifica si los dias no son 30,90 0 180 y da error
        if (diasCobertura != 30 && diasCobertura != 90 && diasCobertura != 180) {
            throw new IllegalArgumentException("plazo invalido");
        }
        this.diasCobertura = diasCobertura;
    }

    public int getDiasCobertura() {
        return diasCobertura;
    }

    // se rehace la comision y se agrega la suma de dias/30 * 200
    @Override
    public int comision() {
        return super.comision() + (diasCobertura / 30) * 200;
    }
}
