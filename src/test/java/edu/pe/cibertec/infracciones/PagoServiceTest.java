package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void procesarPago_CuandoEsProntoPago_AplicaDescuento20Porciento() {

        Long multaId = 1L;
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setMonto(500.00);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(10)); // No vencida
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PagoResponseDTO response = pagoService.procesarPago(multaId);

        Assertions.assertEquals(400.00, response.getMontoPagado());
        Assertions.assertEquals(100.00, response.getDescuentoAplicado());
        Assertions.assertEquals(0.00, response.getRecargo());
        Assertions.assertEquals(EstadoMulta.PAGADA, multa.getEstado());

        Mockito.verify(multaRepository).save(multa);
        Mockito.verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void procesarPago_CuandoEstaVencida_AplicaRecargoYCapturaObjeto() {

        Long multaId = 2L;
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setMonto(500.00);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setFechaEmision(LocalDate.now().minusDays(12));
        multa.setFechaVencimiento(LocalDate.now().minusDays(2));

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);

        pagoService.procesarPago(multaId);

        verify(pagoRepository, times(1)).save(pagoCaptor.capture());

        Pago pagoCapturado = pagoCaptor.getValue();

        Assertions.assertEquals(575.00, pagoCapturado.getMontoPagado());
        Assertions.assertEquals(75.00, pagoCapturado.getRecargo());
        Assertions.assertEquals(0.00, pagoCapturado.getDescuentoAplicado());
        Assertions.assertEquals(EstadoMulta.PAGADA, multa.getEstado());
    }

}