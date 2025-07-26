
import javax.swing.*;
import java.awt.*;

import static java.awt.Font.BOLD;
import static javax.swing.SwingConstants.CENTER;

public class CollectionOfPages extends JFrame
{
    JPanel startPanel, namePanel, welcomePanel, progressPanel, resultPanel, windows;
    JLabel welcomeLabel;

    CollectionOfPages()
    {
        super("Welcome");
        //setLayout(new GridLayout(2,1));
        setLayout(new BorderLayout());
        setSize(600,600);
        setLocation(100,100);

        startPanel = new JPanel();
        startPanel.setLayout(new GridLayout(2,1));
        startPanel.setPreferredSize(new Dimension(600, 125));

        windows = new JPanel(new CardLayout());
        windows.setPreferredSize(new Dimension(600, 475));

        welcomeLabel = new JLabel("ANALYSE YOUR RESUME!");
        welcomeLabel.setFont(new Font("Times New Roman", BOLD, 30));
        welcomeLabel.setHorizontalAlignment(CENTER);

        namePanel = new NamePanel();
        welcomePanel = new WelcomePanel();
        progressPanel = new ProgressPanel();
        // resultPanel = new ResultPanel();

        getContentPane().add(startPanel, BorderLayout.NORTH);
        getContentPane().add(windows, BorderLayout.CENTER);


        startPanel.setSize(200,200);
        startPanel.add(welcomeLabel);

        windows.add(namePanel);
        windows.add(welcomePanel);
        windows.add(progressPanel);
        // windows.add(resultPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

}

