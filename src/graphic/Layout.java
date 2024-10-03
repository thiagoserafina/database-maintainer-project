package graphic;

import db.DBBackup;
import db.DBVacuum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.Serial;

public class Layout extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private final JCheckBox checkBoxVacuumAutomatico;
    private final JCheckBox checkBoxVacuum;
    private final JCheckBox checkBoxFullAnalyze;
    private final JCheckBox checkBoxReindex;
    private final JCheckBox checkBoxBackup;
    private final JCheckBox checkBoxCopiarBackupAdicional;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Layout frame = new Layout();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Layout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 350);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        contentPane.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Trabalho Sobre Vacuum");
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);

        JLabel subtitleVacuum = new JLabel("Opcoes de Vacuum:");
        subtitleVacuum.setFont(new Font("Tahoma", Font.BOLD, 16));
        subtitleVacuum.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitleVacuum);

        checkBoxVacuumAutomatico = new JCheckBox("Executar Vacuum Automatico com base nos dias");
        panel.add(checkBoxVacuumAutomatico);

        checkBoxVacuum = new JCheckBox("Executar Vacuum");
        panel.add(checkBoxVacuum);

        checkBoxFullAnalyze = new JCheckBox("Executar Full Analyze");
        checkBoxFullAnalyze.setEnabled(false);
        panel.add(checkBoxFullAnalyze);

        checkBoxReindex = new JCheckBox("Executar Reindex");
        panel.add(checkBoxReindex);

        checkBoxVacuumAutomatico.addItemListener(e -> {
            boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
            checkBoxVacuum.setEnabled(!isSelected);

            if (isSelected) {
                checkBoxVacuum.setSelected(false);
            }
        });

        checkBoxVacuum.addItemListener(e -> {
            boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
            checkBoxVacuumAutomatico.setEnabled(!isSelected);
            checkBoxFullAnalyze.setEnabled(isSelected);

            if (isSelected) {
                checkBoxVacuumAutomatico.setSelected(false);
            } else {
                checkBoxFullAnalyze.setSelected(false);
            }
        });

        JLabel subtitleBackup = new JLabel("Opcoes de Backup:");
        subtitleBackup.setFont(new Font("Tahoma", Font.BOLD, 16));
        subtitleBackup.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitleBackup);

        checkBoxBackup = new JCheckBox("Fazer Backup");
        panel.add(checkBoxBackup);

        checkBoxCopiarBackupAdicional = new JCheckBox("Copiar backup adicional");
        checkBoxCopiarBackupAdicional.setEnabled(false);
        panel.add(checkBoxCopiarBackupAdicional);

        checkBoxBackup.addItemListener(e -> {
            boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
            checkBoxCopiarBackupAdicional.setEnabled(isSelected);

            if (!isSelected) {
                checkBoxCopiarBackupAdicional.setSelected(false);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton btnIniciar = getjButton();
        buttonPanel.add(btnIniciar);
        panel.add(btnIniciar);
    }

    private JButton getjButton() {
        JButton btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(e -> {
            boolean vacuumAutomatico = checkBoxVacuumAutomatico.isSelected();
            boolean vacuum = checkBoxVacuum.isSelected();
            boolean fullAnalyze = checkBoxFullAnalyze.isSelected();
            boolean reindex = checkBoxReindex.isSelected();
            boolean backup = checkBoxBackup.isSelected();
            boolean copiarBackupAdicional = checkBoxCopiarBackupAdicional.isSelected();

            if (vacuumAutomatico) {
                DBVacuum.autoVacuum();
            }
            if (vacuum) {
                DBVacuum.vacuumManual(true, fullAnalyze);
            }
            if (backup) {
                DBBackup.executar(copiarBackupAdicional);
            }
            if (reindex) {
                DBVacuum.reindex();
            }

            JOptionPane.showMessageDialog(null, "Operacoes iniciadas! Verifique os logs para mais detalhes.");
        });
        return btnIniciar;
    }
}
