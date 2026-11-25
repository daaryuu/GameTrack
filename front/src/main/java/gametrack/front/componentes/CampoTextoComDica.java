package gametrack.front.componentes;

import java.awt.Graphics;
import javax.swing.JTextField;

/**
 * Campo de texto escuro de cantos arredondados, com uma dica exibida enquanto está vazio.
 * O contorno fica roxo quando o campo está em foco.
 */
public class CampoTextoComDica extends JTextField {

    private final String dica;

    /**
     * Cria o campo.
     *
     * @param dica texto de ajuda exibido quando o campo está vazio
     */
    public CampoTextoComDica(String dica) {
        this.dica = dica;
        EstiloCampo.aplicar(this);
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        EstiloCampo.desenharFundo(this, grafico);
        super.paintComponent(grafico);
        EstiloCampo.desenharDica(this, grafico, dica);
        EstiloCampo.desenharContorno(this, grafico);
    }
}
