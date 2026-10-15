package com.example.demo.controller;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.service.MovimentacaoFinanceiraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FinanceiroController {

	private final MovimentacaoFinanceiraService service;

	public FinanceiroController(MovimentacaoFinanceiraService service) {
		this.service = service;
	}

	@GetMapping("/financeiro")
	public String listar(Model model) {
		prepararModelo(model, new MovimentacaoFinanceira());
		return "financeiro";
	}

	@PostMapping("/financeiro/salvar")
	public String salvar(
			@ModelAttribute("movimentacao") MovimentacaoFinanceira movimentacao,
			RedirectAttributes redirectAttributes) {
		try {
			service.registrarManual(movimentacao);
			redirectAttributes.addFlashAttribute(
					"sucesso", "Movimentação registrada com sucesso.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/financeiro";
	}

	private void prepararModelo(Model model, MovimentacaoFinanceira movimentacao) {
		model.addAttribute("movimentacao", movimentacao);
		model.addAttribute("movimentacoes", service.listarTodas());
		model.addAttribute("totalEntradas", service.totalEntradas());
		model.addAttribute("totalSaidas", service.totalSaidas());
		model.addAttribute("saldoAtual", service.saldoAtual());
	}
}
