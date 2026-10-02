package cat.uvic.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    @Test
    void donatDosNombresPositius_quanSumem_retornaResultatCorrecte() {
        assertEquals(5, calc.suma(2, 3), "2 + 3 ha de ser 5");
    }
 
    @Test
    void donatValorsNegatius_quanRestem_retornaResultatCorrecte() {
        // Probamos valores negativos como pide el requisito
        assertEquals(-5, calc.resta(-10, -5), "-10 - (-5) ha de ser -5");
    }

    @Test
    void donatZero_quanMultipliquem_retornaZero() {
        // Probamos un caso con ceros
        assertEquals(0, calc.multiplica(5, 0), "Qualsevol número multiplicat per 0 és 0");
    }

    // Añadimos un test adicional para cubrir la división por cero
    @Test
    void donatValorsPositius_quanMultipliquem_retornaResultatCorrecte() {
        assertEquals(15, calc.multiplica(3, 5), "3 * 5 ha de ser 15");
    }

    @Test
    void donatDivisioPerZero_quanDividim_llancaExcepcio() {
        // Verificamos que salte la excepción correcta
        assertThrows(IllegalArgumentException.class, () -> {
            calc.divideix(5.0, 0.0);
        });
    }

    @ParameterizedTest(name = "{0} elevat a {1} ha de ser {2}")
    @CsvSource({
        "2, 3, 8",   // Cas normal
        "5, 0, 1",   // Exponent zero
        "0, 0, 1"    // Cas especial
    })
    void donadaBaseIExponents_quanCalculemPotencia_retornaResultatEsperat(int base, int exponent, int resultatEsperat) {
        assertEquals(resultatEsperat, calc.potencia(base, exponent));
    }

    //Añadidos adicionalmente para cubrir ramas y excepciones según el requisito de la Part B
    @Test
    void donatValorsNormals_quanDividim_retornaResultatCorrecte() {
        // Cubre la rama donde b NO es cero
        assertEquals(2.5, calc.divideix(5.0, 2.0));
    }

    @Test
    void donatExponentNegatiu_quanCalculemPotencia_llancaExcepcio() {
        // Cubre la rama de la excepción en potencia
        assertThrows(IllegalArgumentException.class, () -> {
            calc.potencia(2, -1);
        });
    }

}