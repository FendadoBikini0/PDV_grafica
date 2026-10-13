package com.example.demo.controller;

import com.example.demo.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void excluirClienteInexistenteInformaErroSemTentarExcluir() {
        when(clienteRepository.existsById(1L)).thenReturn(false);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = clienteController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/clientes", view);
        assertEquals(
                "Cliente não encontrado.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(clienteRepository, never()).deleteById(1L);
    }

    @Test
    void excluirClienteSemVinculosInformaSucesso() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = clienteController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/clientes", view);
        assertEquals(
                "Cliente excluído com sucesso.",
                redirectAttributes.getFlashAttributes().get("sucesso"));
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void excluirClienteComVinculosInformaErroExplicativo() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("Cliente vinculado"))
                .when(clienteRepository).deleteById(1L);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = clienteController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/clientes", view);
        assertEquals(
                "Não foi possível excluir este cliente, pois existem registros vinculados a ele.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(clienteRepository).deleteById(1L);
    }
}
