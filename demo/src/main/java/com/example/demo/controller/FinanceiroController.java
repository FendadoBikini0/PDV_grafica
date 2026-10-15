package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.service.MovimentacaoFinanceiraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FinanceiroController {

	private final MovimentacaoFinanceiraService service;

	public FinanceiroController(MovimentacaoFinanceiraService service) {
		this.service = service;
	}

	@GetMapping("/financeiro")
	public String listar(
			@RequestParam(required = false) LocalDate dataInicial,
			@RequestParam(required = false) LocalDate dataFinal,
			Model model) {
		prepararModelo(model, new MovimentacaoFinanceira());
		model.addAttribute("dataInicial", dataInicial);
		model.addAttribute("dataFinal", dataFinal);
		model.addAttribute("relatorioGerado", false);

		if (dataInicial != null || dataFinal != null) {
			if (dataInicial == null || dataFinal == null) {
				model.addAttribute(
						"erroRelatorio",
						"Informe a data inicial e a data final.");
				return "financeiro";
			}

			try {
				List<MovimentacaoFinanceira> movimentacoesPeriodo =
						service.listarPorPeriodo(dataInicial, dataFinal);
				BigDecimal entradasPeriodo =
						service.totalEntradasNoPeriodo(dataInicial, dataFinal);
				BigDecimal saidasPeriodo =
						service.totalSaidasNoPeriodo(dataInicial, dataFinal);

				model.addAttribute("movimentacoesPeriodo", movimentacoesPeriodo);
				model.addAttribute("entradasPeriodo", entradasPeriodo);
				model.addAttribute("saidasPeriodo", saidasPeriodo);
				model.addAttribute(
						"saldoPeriodo", entradasPeriodo.subtract(saidasPeriodo));
				model.addAttribute("relatorioGerado", true);
			} catch (IllegalArgumentException e) {
				model.addAttribute("erroRelatorio", e.getMessage());
			}
		}

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
		model.addAttribute("movimentacoes", service.listarRecentes());
		model.addAttribute("totalEntradas", service.totalEntradas());
		model.addAttribute("totalSaidas", service.totalSaidas());
		model.addAttribute("saldoAtual", service.saldoAtual());
	}
}
