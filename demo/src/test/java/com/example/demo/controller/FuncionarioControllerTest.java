package com.example.demo.controller;

import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.UsuarioRepository;
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
class FuncionarioControllerTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @Test
    void excluirFuncionarioInexistenteInformaErroSemVerificarVinculos() {
        when(funcionarioRepository.existsById(1L)).thenReturn(false);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = funcionarioController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/funcionarios", view);
        assertEquals(
                "Funcionário não encontrado.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(usuarioRepository, never()).existsByFuncionarioId(1L);
        verify(funcionarioRepository, never()).possuiPedidos(1L);
        verify(funcionarioRepository, never()).deleteById(1L);
    }

    @Test
    void excluirFuncionarioSemVinculosInformaSucesso() {
        when(funcionarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.existsByFuncionarioId(1L)).thenReturn(false);
        when(funcionarioRepository.possuiPedidos(1L)).thenReturn(false);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = funcionarioController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/funcionarios", view);
        assertEquals(
                "Funcionário excluído com sucesso.",
                redirectAttributes.getFlashAttributes().get("sucesso"));
        verify(funcionarioRepository).deleteById(1L);
    }

    @Test
    void naoExcluiFuncionarioAssociadoAUsuario() {
        when(funcionarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.existsByFuncionarioId(1L)).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = funcionarioController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/funcionarios", view);
        assertEquals(
                "Não é possível excluir este funcionário, pois ele está associado a um usuário do sistema.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(funcionarioRepository, never()).possuiPedidos(1L);
        verify(funcionarioRepository, never()).deleteById(1L);
    }

    @Test
    void naoExcluiFuncionarioComPedidos() {
        when(funcionarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.existsByFuncionarioId(1L)).thenReturn(false);
        when(funcionarioRepository.possuiPedidos(1L)).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = funcionarioController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/funcionarios", view);
        assertEquals(
                "Não é possível excluir este funcionário, pois existem pedidos registrados por ele.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(funcionarioRepository, never()).deleteById(1L);
    }

    @Test
    void informaErroSeBancoImpedirExclusao() {
        when(funcionarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.existsByFuncionarioId(1L)).thenReturn(false);
        when(funcionarioRepository.possuiPedidos(1L)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("Funcionário vinculado"))
                .when(funcionarioRepository).deleteById(1L);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = funcionarioController.excluir(1L, redirectAttributes);

        assertEquals("redirect:/funcionarios", view);
        assertEquals(
                "Não foi possível excluir este funcionário porque existem registros vinculados a ele.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(funcionarioRepository).deleteById(1L);
    }
}
