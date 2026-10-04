package cat.uvic.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DescompteServiceTest {

    private DescompteService service;

    @BeforeEach
    void setUp() {
        service = new DescompteService();
    }

    // Agrupacio dels casoso de probes amb CsvSource, aixi nomes fa falta la execucio amb un test
    @ParameterizedTest(name = "Import {0}, Premium: {1} -> Esperat: {2}")
    @CsvSource({
        "0.0, false, 0.0",       // Nou: Valor límit exacte per matar el mutant de la línia 7
        "50.0, false, 50.0",     // Normal
        "99.99, false, 99.99",   // Sota del límit
        "100.0, false, 90.0",    // Límit per descompte
        "100.0, true, 80.0",     // Premium amb descompte
        "50.0, true, 50.0"       // Adicional: Premium sense descompte 
    })
    void calcular_aplicaDescomptesCorrectament(double importCompra, boolean clientPremium, double resultatEsperat) {
        // Añadimos un delta de 0.001 porque estamos comparando números decimales (double)
        assertEquals(resultatEsperat, service.calcular(importCompra, clientPremium), 0.001);
    }


    // Import negatiu, ha de llançar una excepció
    @Test
    void calcular_importNegatiu_llancaExcepcio() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.calcular(-1.0, false);
        });
    }
}