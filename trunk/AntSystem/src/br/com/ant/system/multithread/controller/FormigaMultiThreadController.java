package br.com.ant.system.multithread.controller;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class FormigaMultiThreadController {

	private FormigaController	formigaController;
	private PercursoController	percurso;

	public FormigaMultiThreadController(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo) {
		this.formigaController = new FormigaController(formiga, percurso, algoritmo);
		this.percurso = percurso;
	}

	public Formiga call() {
		// Setando o tempo inicial
		formigaController.getFormiga().setTempoInicial(System.currentTimeMillis());

		do {
			formigaController.escolherPercurso();
		} while (!percurso.isFinalizouPercurso(formigaController.getFormiga()));

		// Setando o tempo Final
		formigaController.getFormiga().setTempoFinal(System.currentTimeMillis());

		return formigaController.getFormiga();
	}
}
