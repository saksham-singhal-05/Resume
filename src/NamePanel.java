import javax.swing.*;
import java.awt.*;

public class NamePanel extends JPanel
{
    //variables
    JTextField nameText;
    JButton nextButton;
    JLabel nameLabel;
    JPanel panel;
    JLabel enterName;

    //constructor
    public NamePanel()
    {
        super();
        panel = new JPanel();
        panel.setLayout(new GridLayout(5,1));

        enterName = new JLabel("Enter your Name");
        nameText = new JTextField();

        nameLabel = new JLabel();

        nextButton = new JButton("Yes");
        nextButton.setVisible(false);

        nameText.addActionListener(e ->
        {
            if(nameText.getText().equals("")){
                JOptionPane.showMessageDialog(panel, "Please enter your name");
            }
            else{
                enterName.setVisible(false);
                nameLabel.setText(e.getActionCommand() + ", are you ready to analyse your resume?");
                nameText.setVisible(false);
                nextButton.setVisible(true);
            }
        });

        nextButton.addActionListener(e ->
        {
                panel.setVisible(false);
                add(new WelcomePanel());
        });

        add(panel);

        panel.add(enterName);
        panel.add(nameLabel);
        panel.add(nameText);
        panel.add(nextButton);
    }

}
