import javax.swing.*;
import java.awt.*;

public class NamePanel extends JPanel {
    JTextField nameText;
    JButton nextButton;
    JLabel nameLabel, enterName;
    JPanel panel;

    public interface ProceedListener { void proceed(); }

    public NamePanel(ProceedListener listener) {
        super();
        panel = new JPanel(new GridLayout(5,1));
        enterName = new JLabel("Enter your Name");
        nameLabel = new JLabel();
        nameText = new JTextField();
        nextButton = new JButton("Yes");
        nextButton.setVisible(false);

        nameText.addActionListener(e -> {
            if (nameText.getText().equals("")) {
                JOptionPane.showMessageDialog(panel, "Please enter your name");
            } else {
                enterName.setVisible(false);
                nameLabel.setText(e.getActionCommand() + ", are you ready to analyse your resume?");
                nameText.setVisible(false);
                nextButton.setVisible(true);
            }
        });

        nextButton.addActionListener(e -> {
            listener.proceed();
        });

        panel.add(enterName);
        panel.add(nameLabel);
        panel.add(nameText);
        panel.add(nextButton);
        add(panel);
    }
}
