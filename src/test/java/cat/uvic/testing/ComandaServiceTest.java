package cat.uvic.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Esta anotación es vital para que se inicialicen los mocks
@ExtendWith(MockitoExtension.class)
class ComandaServiceTest {

    @Mock
    StockRepository stockRepository;

    @InjectMocks
    ComandaService service;

    @Test
    void potComprar_QuanHiHaStock_RetornaTrue() {
        // Configuramos el mock para que responda 'true'
        when(stockRepository.teStock("P01")).thenReturn(true);
        
        assertTrue(service.potComprar("P01"));
        
        // Verificamos que efectivamente se ha llamado a la base de datos simulada
        verify(stockRepository).teStock("P01");
    }

    @Test
    void potComprar_QuanNoHiHaStock_RetornaFalse() {
        // Configuramos el mock para que responda 'false'
        when(stockRepository.teStock("P02")).thenReturn(false);
        
        assertFalse(service.potComprar("P02"));
        verify(stockRepository).teStock("P02");
    }

    @Test
    void potComprar_ProducteInvalid_LlancaExcepcioINoConsultaStock() {
        // Como el ComandaService lanza excepción si el producto es nulo o blanco...
        assertThrows(IllegalArgumentException.class, () -> {
            service.potComprar("");
        });
        
        // Comprobamos que, al dar error de validación, ni siquiera se ha consultado el StockRepository
        verifyNoInteractions(stockRepository);
    }

    
    // Añadimos un test adicional para cubrir el caso de producto nulo
    @Test
    void potComprar_ProducteNull_LlancaExcepcio() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.potComprar(null);
        });
        verifyNoInteractions(stockRepository);
    }

}