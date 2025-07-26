
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileInputStream;

public class Parse {
    public static String extractTextFromDocx(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder sb = new StringBuilder();

            // Extract normal paragraphs
            for (XWPFParagraph para : document.getParagraphs()) {
                sb.append(para.getText()).append("\n");
            }

            // Extract text from tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        sb.append(cell.getText()).append(" | ");
                    }
                    sb.append("\n");
                }
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading DOCX file.";
        }
    }
}


