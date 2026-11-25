package gametrack.front.componentes;

import java.awt.Graphics;
import javax.swing.JPasswordField;

/**
 * Campo de senha com a mesma aparência do {@link CampoTextoComDica}. O texto digitado fica
 * oculto.
 */
public class CampoSenhaComDica extends JPasswordField {

    private final String dica;

    /**
     * Cria o campo.
     *
     * @param dica texto de ajuda exibido quando o campo está vazio
     */
    public CampoSenhaComDica(String dica) {
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
