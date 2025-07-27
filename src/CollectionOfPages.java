import javax.swing.*;
import java.awt.*;

public class CollectionOfPages extends JFrame {
    CardLayout cardLayout;
    JPanel cards;
    String loggedInUsername;

    public CollectionOfPages() {
        super("Resume Analyzer");
        setSize(700, 650);
        setMinimumSize(new Dimension(700, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        ResultPanel resultPanel = new ResultPanel();
        ProgressPanel progressPanel = new ProgressPanel();
        WelcomePanel welcomePanel = new WelcomePanel(() -> {
            cardLayout.show(cards, "progress");
            progressPanel.startProgress(
                WelcomePanel.resumeText,
                WelcomePanel.jobDescription,
                WelcomePanel.selectedProfession,
                apiResponse -> {
                    resultPanel.setResultString(apiResponse);  // display parsed API output
                    cardLayout.show(cards, "result");
                }
            );
        });

        LoginPanel loginPanel = new LoginPanel(username -> {
            loggedInUsername = username;
            welcomePanel.setUsername(loggedInUsername);
            cardLayout.show(cards, "welcome");
        });

        cards.add(loginPanel, "login");
        cards.add(welcomePanel, "welcome");
        cards.add(progressPanel, "progress");
        cards.add(resultPanel, "result");

        add(cards);
        setVisible(true);
        cardLayout.show(cards, "login");
    }
}
