import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class WelcomePanel extends JPanel {
    public static String selectedProfession;
    public static String jobDescription;
    public static File selectedFile;
    public static String resumeText;
    boolean chosen = false;
    JLabel welcomeUserLabel;
    JTextArea jobDescriptionText;

    public interface ContinueListener { void proceed(); }

    public WelcomePanel(ContinueListener listener) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 600));
        JPanel panel = new JPanel(new BorderLayout());

        // Welcome username label
        welcomeUserLabel = new JLabel("", SwingConstants.CENTER);
        welcomeUserLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeUserLabel, BorderLayout.NORTH);

        JLabel professionLabel = new JLabel("Choose your profession - ");
        JLabel resumeLabel = new JLabel("Upload your resume.");
        JLabel jobDescriptionLabel = new JLabel("Insert your job description - ");
        String[] itProfessions = {
            "Select--", "Software Developer", "Cybersecurity Analyst",
            "Data Scientist", "Network Administrator", "Cloud Engineer",
            "IT Support Specialist", "Systems Analyst", "Database Administrator",
            "DevOps Engineer", "AI/Machine Learning Engineer"
        };
        JComboBox<String> comboBox = new JComboBox<>(itProfessions);
        JButton chooseButton = new JButton("Choose file");
        jobDescriptionText = new JTextArea();
        jobDescriptionText.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(jobDescriptionText);
        scrollPane.setPreferredSize(new Dimension(250, 100));

        JButton nextButton = new JButton("Next");

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; grid.add(professionLabel, gbc);
        gbc.gridx = 1; grid.add(comboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; grid.add(resumeLabel, gbc);
        gbc.gridx = 1; grid.add(chooseButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; grid.add(jobDescriptionLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1; gbc.weighty=1;
        grid.add(scrollPane, gbc);

        JPanel wrapper = new JPanel();
        wrapper.add(grid);
        panel.add(wrapper, BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.SOUTH);

        comboBox.addActionListener(e -> selectedProfession = (String) comboBox.getSelectedItem());

        nextButton.addActionListener(e -> {
            if (comboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(panel, "Please select the job description");
            } else if (chosen) {
                jobDescription = jobDescriptionText.getText().trim(); // empty ok
                resumeText = Parse.extractTextFromDocx(selectedFile);
                listener.proceed();
            } else {
                JOptionPane.showMessageDialog(panel, "Please select the resume file");
            }
        });

        chooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("DOCS", "doc", "docx");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                chosen = true;
                selectedFile = chooser.getSelectedFile();
                chooseButton.setText(selectedFile.getName());
            }
        });

        add(panel);
    }

    public void setUsername(String username) {
        welcomeUserLabel.setText("Welcome, " + username + "!");
    }
}
