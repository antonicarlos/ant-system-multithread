package br.com.ant.system.multithread.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class ControladorGeral implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future				auxServicesFuture;
	@SuppressWarnings("rawtypes")
	private Future				formigaExecutionFuture;

	private Set<Formiga>		formigas	= new HashSet<Formiga>();
	private ExecutorService		executor	= Executors.newCachedThreadPool();

	private Logger				logger		= Logger.getLogger(this.getClass());

	private ASAlgoritmo			algoritmo;
	private PercursoController	percurso;
	private BufferBlockingClass	buffer;
	private int					maximoIteracoes;

	public ControladorGeral(ASAlgoritmo algoritmo, PercursoController percurso, int maximoIteracoes) {
		this.algoritmo = algoritmo;
		this.percurso = percurso;
		this.maximoIteracoes = maximoIteracoes;

		this.buffer = new BufferBlockingClass();

	}

	@Override
	public void run() {
		// Aciona o processamento das formigas
		formigaExecutionFuture = executor.submit(new FormigaExecution());

		// Acionando a thread de atualizacao de feromonio e coleta de dados estatisticos
		auxServicesFuture = executor.submit(new AuxServicesUpdateClass(algoritmo, percurso, buffer));

		while (!isAllFinished()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.error("Error ", e);
			}
		}

		this.stop();

		EstatisticasControler.getInstance().loggerEstatisticas();

		executor.shutdownNow();
	}

	public void stop() {
		auxServicesFuture.cancel(true);
		formigaExecutionFuture.cancel(true);

		while (!formigaExecutionFuture.isDone() && !auxServicesFuture.isDone()) {
		}
	}

	public void addFormiga(Formiga formiga) {
		try {
			buffer.addFomigaExecution(formiga);
		} catch (InterruptedException e) {
			logger.error("Nao foi possivel incluir a formiga na fila de execucao.", e);
		}
	}

	public boolean isAllFinished() {
		boolean finished = true;
		for (Iterator<Formiga> it = formigas.iterator(); it.hasNext();) {
			Formiga formiga = (Formiga) it.next();

			if (formiga.getQntIteracaoExecutadas() < maximoIteracoes) {
				finished = false;
				break;
			}
		}

		return finished;

	}

	public class FormigaExecution implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Formiga formiga = buffer.takeFormigaExecution();
					formigas.add(formiga);

					// executando a thread
					if (formiga.getQntIteracaoExecutadas() < maximoIteracoes) {
						executor.submit(new MultiThreadDispatched(formiga, percurso, algoritmo, buffer));
					}
				} catch (InterruptedException e) {
					logger.info("Thread de execucao de formigas foi interrompida.", e);
				}
			}
		}
	}
}
