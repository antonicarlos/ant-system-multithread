/**
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ant.system.action;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.multithread.controller.GerenciadorFormigaExecution;
import br.com.ant.system.util.AntSystemUtil;

/**
 * Efetua o processamento do algoritmo em multiplas threads.
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMultithread implements ColoniaFormigasActionInterface {

	private GerenciadorFormigaExecution	control;
	private int							maximoIteracoes;
	private ExecutorService				executor	= Executors.newCachedThreadPool();

	@SuppressWarnings("rawtypes")
	Future								controlFuture;

	public ColoniaFormigaMultithread(PercursoController percurso, ASAlgoritmo algoritmo) {
		control = new GerenciadorFormigaExecution(algoritmo, percurso);
		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());
	}

	/**
	 * Adiciona formigas para a execu��o
	 * 
	 * @param formigas
	 */
	public void addFormigas(Collection<Formiga> formigas) {
		control.setFormigasDisponiveis(formigas);
	}

	/**
	 * Executa o algoritmo.
	 */
	@Override
	public void action() {
		control.setMaximoIteracoes(maximoIteracoes);

		try {
			controlFuture = executor.submit(control);

			controlFuture.get();

			executor.shutdown();
		} catch (InterruptedException e) {
			AntSystemUtil.getIntance().logar("A Thread de controle foi interrompida.");
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public void setMaximoIteracoes(int maximo) {
		this.maximoIteracoes = maximo;
	}

	@Override
	public int getMaximoIteracoes() {
		return maximoIteracoes;
	}
}
