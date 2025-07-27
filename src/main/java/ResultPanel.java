import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResultPanel extends JPanel {

    private JLabel matchPercentageLabel;
    private JPanel matchedSkillsPanel;
    private JPanel missingSkillsPanel;
    
    private JPanel jobSoftSkillsPanel;
    private JPanel jobTechnicalSkillsPanel;
    
    private JPanel resumeSoftSkillsPanel;
    private JPanel resumeTechnicalSkillsPanel;
    
    private JLabel technicalSkillsValue, softSkillsValue, overallSkillsValue, textSimilarityValue;

    public ResultPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 650));
        
        // Title
        JLabel title = new JLabel("Resume Matching Results", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Main scroll panel to avoid scroll on window
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Container panel inside scroll pane with vertical BoxLayout
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(10, 20, 10, 20));
        container.setOpaque(false);
        scrollPane.setViewportView(container);

        // Match Percentage (big and centered)
        matchPercentageLabel = new JLabel("", SwingConstants.CENTER);
        matchPercentageLabel.setFont(new Font("Arial", Font.BOLD, 28));
        matchPercentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(matchPercentageLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sections with labels and panels
        container.add(createSectionLabel("Matched Skills:"));
        matchedSkillsPanel = createWrappingPanel();
        container.add(matchedSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));

        container.add(createSectionLabel("Missing Skills:"));
        missingSkillsPanel = createWrappingPanel();
        container.add(missingSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Job Skills
        container.add(createSectionLabel("Job Description Soft Skills:"));
        jobSoftSkillsPanel = createWrappingPanel();
        container.add(jobSoftSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));

        container.add(createSectionLabel("Job Description Technical Skills:"));
        jobTechnicalSkillsPanel = createWrappingPanel();
        container.add(jobTechnicalSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Resume Skills
        container.add(createSectionLabel("Resume Soft Skills:"));
        resumeSoftSkillsPanel = createWrappingPanel();
        container.add(resumeSoftSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));

        container.add(createSectionLabel("Resume Technical Skills:"));
        resumeTechnicalSkillsPanel = createWrappingPanel();
        container.add(resumeTechnicalSkillsPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Similarity scores grid
        container.add(createSectionLabel("Similarity Scores:"));
        JPanel scoresPanel = new JPanel(new GridLayout(2, 2, 20, 10));
        scoresPanel.setOpaque(false);
        technicalSkillsValue = createScoreLabel();
        softSkillsValue = createScoreLabel();
        overallSkillsValue = createScoreLabel();
        textSimilarityValue = createScoreLabel();

        scoresPanel.add(labeledPanel("Technical Skills:", technicalSkillsValue));
        scoresPanel.add(labeledPanel("Soft Skills:", softSkillsValue));
        scoresPanel.add(labeledPanel("Overall Skills:", overallSkillsValue));
        scoresPanel.add(labeledPanel("Text Similarity:", textSimilarityValue));

        container.add(scoresPanel);
        container.add(Box.createVerticalGlue());
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    private JPanel createWrappingPanel() {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));  // Custom WrapLayout to wrap components
        panel.setOpaque(false);
        return panel;
    }

    private JLabel createScoreLabel() {
        JLabel lbl = new JLabel();
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        return lbl;
    }

    private JPanel labeledPanel(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label);
        panel.add(valueLabel);
        return panel;
    }

    private JLabel skillChip(String skill, Color bg, Color fg) {
        JLabel label = new JLabel(skill);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker()),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        return label;
    }

    /**
     * Call this method with the raw JSON string from your API.
     * It parses and updates UI components accordingly.
     */
    public void setResultString(String result) {
        try {
            JSONObject obj = new JSONObject(result);

            // Match Percentage
            double matchPercent = obj.optDouble("match_percentage", 0.0);
            matchPercentageLabel.setText(String.format("Match: %.2f%%", matchPercent));
            matchPercentageLabel.setForeground(matchPercent >= 70 ? new Color(26, 170, 52) : new Color(213, 128, 0));

            // Matched Skills
            populateSkillPanel(matchedSkillsPanel, obj.optJSONArray("matched_skills"), new Color(34, 139, 34), Color.WHITE);

            // Missing Skills
            populateSkillPanel(missingSkillsPanel, obj.optJSONArray("missing_skills"), new Color(220, 20, 60), Color.WHITE);

            // Job Skills (soft & technical)
            JSONObject jobSkills = obj.optJSONObject("job_skills");
            if (jobSkills != null) {
                populateSkillPanel(jobSoftSkillsPanel, jobSkills.optJSONArray("soft_skills"), new Color(70, 130, 180), Color.WHITE);
                populateSkillPanel(jobTechnicalSkillsPanel, jobSkills.optJSONArray("technical_skills"), new Color(255, 140, 0), Color.WHITE);
            } else {
                clearPanel(jobSoftSkillsPanel);
                clearPanel(jobTechnicalSkillsPanel);
            }

            // Resume Skills
            JSONObject resumeSkills = obj.optJSONObject("resume_skills");
            if (resumeSkills != null) {
                populateSkillPanel(resumeSoftSkillsPanel, resumeSkills.optJSONArray("soft_skills"), new Color(0, 128, 128), Color.WHITE);
                populateSkillPanel(resumeTechnicalSkillsPanel, resumeSkills.optJSONArray("technical_skills"), new Color(128, 0, 128), Color.WHITE);
            } else {
                clearPanel(resumeSoftSkillsPanel);
                clearPanel(resumeTechnicalSkillsPanel);
            }

            // Similarity Scores
            JSONObject scores = obj.optJSONObject("similarity_scores");
            if (scores != null) {
                technicalSkillsValue.setText(String.format("%.3f", scores.optDouble("technical_skills", 0)));
                softSkillsValue.setText(String.format("%.3f", scores.optDouble("soft_skills", 0)));
                overallSkillsValue.setText(String.format("%.3f", scores.optDouble("overall_skills", 0)));
                textSimilarityValue.setText(String.format("%.3f", scores.optDouble("text_similarity", 0)));
            } else {
                technicalSkillsValue.setText("0.000");
                softSkillsValue.setText("0.000");
                overallSkillsValue.setText("0.000");
                textSimilarityValue.setText("0.000");
            }

            revalidate();
            repaint();

        } catch (Exception e) {
            // Fallback: show raw message on error
            removeAll();
            setLayout(new BorderLayout());
            JTextArea errorArea = new JTextArea("Error parsing results:\n" + e.getMessage() + "\nRaw data:\n" + result);
            errorArea.setWrapStyleWord(true);
            errorArea.setLineWrap(true);
            errorArea.setEditable(false);
            add(new JScrollPane(errorArea), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void populateSkillPanel(JPanel panel, JSONArray jsonArray, Color bgColor, Color fgColor) {
        panel.removeAll();
        if (jsonArray == null || jsonArray.length() == 0) {
            panel.add(new JLabel("None"));
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                String skill = jsonArray.optString(i);
                if (!skill.isEmpty()) {
                    panel.add(skillChip(skill, bgColor, fgColor));
                }
            }
        }
    }

    private void clearPanel(JPanel panel) {
        panel.removeAll();
        panel.add(new JLabel("None"));
    }


    /**
     * Custom WrapLayout to wrap components on new lines automatically.
     * Source: https://tips4java.wordpress.com/2008/11/06/wrap-layout/
     */
    public static class WrapLayout extends FlowLayout {

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                addRow(dim, rowWidth, rowHeight);

                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;

                Container scrollPane = SwingUtilities.getUnwrappedParent(target);
                if (scrollPane instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) scrollPane;
                    JScrollBar vBar = sp.getVerticalScrollBar();
                    if (vBar != null && vBar.isVisible()) {
                        dim.width -= (vBar.getWidth() + hgap);
                    }
                }

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            dim.height += rowHeight;
        }
    }
}
