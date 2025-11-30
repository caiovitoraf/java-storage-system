
package com.iqbase.iqbase;

import com.iqbase.iqbase.view.TelaInicial;
import javax.swing.SwingUtilities;

/**
 *
 * @author caiov-fedora
 */
public class IQBase {

    public static void main(String[] args) {
        // Garante que a UI seja criada e exibida na Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaInicial telaInicial = new TelaInicial();
                telaInicial.setVisible(true);
            }
        });
    }
}
