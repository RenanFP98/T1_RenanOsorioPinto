package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void verificarBloqueo_CuandoTieneDosVencidas_NoSeBloquea() {

        Long id = 1L;
        Infractor infractor = new Infractor();
        infractor.setId(id);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(id)).thenReturn(Optional.of(infractor));

        when(infractorRepository.countMultasVencidas(id)).thenReturn(2L);

        infractorService.verificarBloqueo(id);

        Assertions.assertFalse(infractor.isBloqueado());

        verify(infractorRepository, never()).save(any());
    }
}