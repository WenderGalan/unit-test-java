package br.ce.wcaquino.runners;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelRunner extends BlockJUnit4ClassRunner {

    public ParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ThreadPoll());
    }

    private static class ThreadPoll implements RunnerScheduler {
        private ExecutorService executorService;

        public ThreadPoll() {
            // Quantidade de threads que estão executando os testes
            this.executorService = Executors.newFixedThreadPool(5);
        }

        @Override
        public void schedule(Runnable runnable) {
            executorService.submit(runnable);
        }

        @Override
        public void finished() {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
