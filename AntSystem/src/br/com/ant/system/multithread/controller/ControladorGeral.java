package br.com.ant.system.multithread.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class ControladorGeral implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future				formigaExecutionFuture;

	private Set<Formiga>		formigasDisponiveis	= new HashSet<Formiga>();
	private Set<Formiga>		formigasFinalizadas	= new HashSet<Formiga>();

	// private ExecutorService executor = Executors.newFixedThreadPool(3, new
	// SimpleThreadFactory());
	private ExecutorService		executor			= Executors.newCachedThreadPool(new SimpleThreadFactory());

	private Logger				logger				= Logger.getLogger(this.getClass());

	private ASAlgoritmo			algoritmo;
	private PercursoController	percurso;
	private AtomicInteger		maximoIteracoes		= new AtomicInteger();

	private Lock				lock				= new ReentrantLock();
	private Condition			canContinue			= lock.newCondition();

	public ControladorGeral(ASAlgoritmo algoritmo, PercursoController percurso) {
		this.algoritmo = algoritmo;
		this.percurso = percurso;
	}

	public void setPercurso(PercursoController percurso) {
		this.percurso = percurso;
	}

	public void setFormigasDisponiveis(Collection<Formiga> formigasDisponiveis) {
		this.formigasDisponiveis.addAll(formigasDisponiveis);
	}

	@Override
	public void run() {
		// Aciona o processamento das formigas
		formigaExecutionFuture = executor.submit(new FormigaExecution(formigasDisponiveis));

		this.waitForAllFinished();

		this.stop();

		EstatisticasControler.getInstance().loggerEstatisticas();

		executor.shutdownNow();
	}

	public void stop() {
		formigaExecutionFuture.cancel(true);
	}

	public void waitForAllFinished() {
		try {
			lock.lock();

			canContinue.await();
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}

	}

	public class FormigaExecution implements Runnable {

		Collection<Formiga>	formigas;

		public FormigaExecution(Collection<Formiga> formigas) {
			this.formigas = formigas;
		}

		@Override
		public void run() {
			for (Formiga formiga : formigas) {

				// executando a thread
				Future<Formiga> formigaFuture = executor.submit(new MultiThreadDispatched(formiga, percurso, algoritmo, maximoIteracoes.get()));

				FormigaWait formigaWait = new FormigaWait(formigaFuture);
				Thread thread = new Thread(formigaWait);
				thread.setName("FormigaWait " + formiga.getId());

				thread.start();
			}
		}
	}

	public class FormigaWait implements Runnable {

		Future<Formiga>	formigaFuture;

		public FormigaWait(Future<Formiga> formigaFuture) {
			this.formigaFuture = formigaFuture;
		}

		@Override
		public void run() {
			try {

				Formiga formiga = formigaFuture.get();
				formigasFinalizadas.add(formiga);

				if (formigasDisponiveis.size() == formigasFinalizadas.size()) {
					try {
						lock.lock();
						logger.info("Todas as formigas finalizaram o percurso.");
						canContinue.signal();
					} finally {
						lock.unlock();
					}
				}
			} catch (Exception e) {
				logger.info("Thread de execucao de formigas foi interrompida.");
			}
		}
	}

	public void setMaximoIteracoes(int maximoIteracoes) {
		this.maximoIteracoes.set(maximoIteracoes);
	}

	public int getMaximoIteracoes() {
		return maximoIteracoes.get();
	}
}
