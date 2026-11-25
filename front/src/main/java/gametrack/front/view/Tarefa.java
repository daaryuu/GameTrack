package gametrack.front.view;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingWorker;

/**
 * Executa uma operação fora da thread da interface, para a tela não travar, e entrega o
 * resultado ou o erro de volta a ela.
 *
 * @param <T> tipo do resultado
 */
class Tarefa<T> extends SwingWorker<T, Void> {

    private final Supplier<T> operacao;
    private final Consumer<T> aoConcluir;
    private final Consumer<Throwable> aoFalhar;

    Tarefa(Supplier<T> operacao, Consumer<T> aoConcluir, Consumer<Throwable> aoFalhar) {
        this.operacao = operacao;
        this.aoConcluir = aoConcluir;
        this.aoFalhar = aoFalhar;
    }

    @Override
    protected T doInBackground() {
        return operacao.get();
    }

    @Override
    protected void done() {
        try {
            aoConcluir.accept(get());
        } catch (ExecutionException e) {
            aoFalhar.accept(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
