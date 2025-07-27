import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import org.json.JSONObject;

public class ProgressPanel extends JPanel {
    JProgressBar progressBar;
    JLabel progressLabel;
    private String apiResponseResult = ""; // To pass to result screen

    public interface DoneListener { void onDone(String apiResponse); }

    public ProgressPanel() {
        setPreferredSize(new Dimension(700, 600));
        setLayout(new GridBagLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressLabel = new JLabel("Analyzing your resume...");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridx = 0; gbc.gridy = 0; add(progressLabel, gbc);
        gbc.gridx = 1; add(progressBar, gbc);
    }

    public void startProgress(String resumeText, String jobDescription, String profession, DoneListener listener) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(10);

        SwingWorker<String, Integer> worker = new SwingWorker<String, Integer>() {
            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(250);
                publish(20); // stuck bar at 20%
                String apiResp = callYourAPI(resumeText, jobDescription, profession);
                publish(100); // vroom when API returns
                return apiResp;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int value = chunks.get(chunks.size() - 1);
                progressBar.setValue(value);
            }

            @Override
            protected void done() {
                try {
                    apiResponseResult = get();
                    listener.onDone(apiResponseResult);
                } catch (Exception e) {
                    progressBar.setValue(0);
                    progressLabel.setText("API failed!");
                }
            }
        };
        worker.execute();
    }

    // POST resume, job and role to API endpoint. API must accept: resume, job, role
    

        private String callYourAPI(String resumeText, String jobDescription, String profession) throws Exception {
            String apiUrl = "http://127.0.0.1:5000/compare_skills"; // your API

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject json = new JSONObject();
            json.put("resume", resumeText);
            json.put("job_description", jobDescription == null ? "" : jobDescription);  // correct key
            json.put("role", profession == null || profession.equals("Select--") ? "" : profession);

            String jsonInput = json.toString();

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                try (InputStream err = conn.getErrorStream()) {
                    if (err != null) {
                        String errorResponse = new String(err.readAllBytes(), "UTF-8");
                        System.err.println("API Error Response: " + errorResponse);
                    }
                }
                throw new Exception("API responded with code " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            try (InputStream is = conn.getInputStream()) {
                Scanner scanner = new Scanner(is, "UTF-8");
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
            }
            return response.toString();
        }


    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public String getApiResponseResult() {
        return apiResponseResult;
    }
}
