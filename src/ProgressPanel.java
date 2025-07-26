import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProgressPanel extends JPanel {

    int progressValue;

    JPanel panel;
    JProgressBar progressBar;
    JLabel progressLabel;

    ProgressPanel()
    {
        super();

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        add(panel);
        panel.setBackground(Color.LIGHT_GRAY);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        progressLabel = new JLabel("Analyzing your resume...");

        //customise

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(progressLabel, gbc);
        gbc.gridx = 1;
        panel.add(progressBar, gbc);

        startProgress();

        setVisible(true);

    }

    void startProgress() {
        SwingWorker<Void, Integer> worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    progressValue = i;
                    Thread.sleep(25);
                    publish(i); //UI ko value dene kei liye
                }
                return null;
            }

            protected void process(java.util.List chunks) {
                int value = (int) chunks.get(chunks.size() - 1);
                progressBar.setValue(value);
            }

            protected void done() {
                setVisible(false);
                // add(new ResultPanel());
            }
        };
        worker.execute(); // background task
    }
}
