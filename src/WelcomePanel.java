import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class WelcomePanel extends JPanel {

    static String selectedProfession;
    static String jobDescription;
    static File selectedFile;
    static String resumeText;
    boolean choosen = false;

    JPanel panel;
    String[] itProfessions;
    JLabel professionLabel;
    JLabel resumeLabel;
    JButton chooseButton;
    JLabel jobDescriptionLabel;
    JTextArea jobDescriptionText;
    JButton nextButton;

    public WelcomePanel() {

        super();
        panel = new JPanel();
        add(panel);

        panel.setLayout(new BorderLayout());

        professionLabel = new JLabel("Choose your profession - ");
        resumeLabel = new JLabel("Upload your resume.");
        jobDescriptionLabel = new JLabel("Insert your job description - ");

        itProfessions = new String[]{
                "Select--",
                "Software Developer",
                "Cybersecurity Analyst",
                "Data Scientist",
                "Network Administrator",
                "Cloud Engineer",
                "IT Support Specialist",
                "Systems Analyst",
                "Database Administrator",
                "DevOps Engineer",
                "AI/Machine Learning Engineer"
        };

        JComboBox<String> comboBox = new JComboBox<>(itProfessions);
        comboBox.setSelectedIndex(0);
        nextButton = new JButton("Next");
        chooseButton = new JButton("Choose file");

        jobDescriptionText = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(jobDescriptionText);
        jobDescriptionText.setLineWrap(true);

        // JFileChooser chooser = new JFileChooser();

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        grid.add(professionLabel, gbc);
        gbc.gridx = 1;
        grid.add(comboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        grid.add(resumeLabel, gbc);
        gbc.gridx = 1;
        grid.add(chooseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        grid.add(jobDescriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        scrollPane.setPreferredSize(new Dimension(250, 100));
        grid.add(scrollPane, gbc);

        JPanel wrapper = new JPanel();
        wrapper.add( grid );

        panel.add(wrapper, BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.SOUTH);

        setVisible(true);

        comboBox.addActionListener(e ->
        {
            selectedProfession = (String) comboBox.getSelectedItem();
            //System.out.println("Selected profession: " + selectedProfession);
        });

        nextButton.addActionListener(e ->
        {
            if(comboBox.getSelectedIndex() == 0){
                JOptionPane.showMessageDialog(panel, "Please select the job description");
            }
            else{
                if(choosen == true){
                    jobDescription = jobDescriptionText.getText().trim();
                    System.out.println("Selected profession: " + selectedProfession);
                    System.out.println(jobDescription);

                    panel.setVisible(false);
                    add(new ProgressPanel());

                    resumeText = Parse.extractTextFromDocx(WelcomePanel.selectedFile);
                    System.out.println(resumeText);
                }
                else{
                    JOptionPane.showMessageDialog(panel, "Please select the resume file");
                }
            }
        });

        chooseButton.addActionListener(e ->
        {
//            FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                    "PDFs and DOCS", "pdf", "doc");
//            chooser.setFileFilter(filter); // to filter files
//            int returnVal = chooser.showOpenDialog(this);
//            if(returnVal == JFileChooser.APPROVE_OPTION) {
//                selectedFile = chooser.getSelectedFile();
//                resumeFilePath = chooser.getSelectedFile().getAbsolutePath();
//                chooseButton.setText(chooser.getSelectedFile().getName());
//            }
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("DOCS", "doc", "docx");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                choosen = true;
                selectedFile = chooser.getSelectedFile();
                chooseButton.setText(selectedFile.getName());
            }
        });


    }


}
