package in.gw.main.Services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import in.gw.main.Entity.RentPayment;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

/**
 * PDF SERVICE
 * ============
 * Generates a Rent Receipt PDF for students.
 *
 * Uses OpenPDF (free iText fork) to create a professional-looking
 * invoice/receipt document that the student can download.
 */
@Service
public class PdfService {

    /**
     * Generate a Rent Receipt PDF.
     * Returns the PDF as a byte array (ready to send as HTTP response).
     */
    public byte[] generateRentReceipt(RentPayment payment) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A5, 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            // --- Fonts ---
            Font titleFont   = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(26, 26, 46));
            Font headingFont  = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(67, 97, 238));
            Font normalFont   = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font boldFont     = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
            Font smallFont    = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);

            // --- Header ---
            Paragraph title = new Paragraph("SHIVTIRTH HOSTEL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph subtitle = new Paragraph("Rent Payment Receipt", headingFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            doc.add(subtitle);

            // --- Divider ---
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            divider.setSpacingBefore(5);
            PdfPCell divCell = new PdfPCell();
            divCell.setBorderWidthBottom(2f);
            divCell.setBorderColorBottom(new Color(67, 97, 238));
            divCell.setBorderWidthTop(0);
            divCell.setBorderWidthLeft(0);
            divCell.setBorderWidthRight(0);
            divCell.setFixedHeight(3);
            divider.addCell(divCell);
            doc.add(divider);
            doc.add(Chunk.NEWLINE);

            // --- Receipt Details Table ---
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.5f});
            table.setSpacingBefore(10);

            // Student details
            addRow(table, "Student Name", payment.getStudentProfile().getUser().getName(), boldFont, normalFont);
            addRow(table, "Receipt No", "REC-" + payment.getId(), boldFont, normalFont);
            addRow(table, "Payment Date", payment.getPaymentDate().toString(), boldFont, normalFont);
            addRow(table, "Month / Year", payment.getMonth() + " " + payment.getYear(), boldFont, normalFont);
            addRow(table, "Payment Mode", payment.getPaymentMode().name(), boldFont, normalFont);

            // Room details
            if (payment.getStudentProfile().getRoom() != null) {
                addRow(table, "Room Number", payment.getStudentProfile().getRoom().getRoomNumber(), boldFont, normalFont);
            }

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // --- Amount Box ---
            PdfPTable amountTable = new PdfPTable(1);
            amountTable.setWidthPercentage(60);
            amountTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            Font amountFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(6, 214, 160));
            PdfPCell amountCell = new PdfPCell(new Phrase("₹ " + String.format("%.0f", payment.getAmount()), amountFont));
            amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            amountCell.setPadding(12);
            amountCell.setBackgroundColor(new Color(240, 242, 245));
            amountCell.setBorder(Rectangle.NO_BORDER);
            amountTable.addCell(amountCell);

            PdfPCell labelCell = new PdfPCell(new Phrase("AMOUNT PAID", boldFont));
            labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            labelCell.setPadding(5);
            labelCell.setBorder(Rectangle.NO_BORDER);
            amountTable.addCell(labelCell);

            doc.add(amountTable);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            // --- Footer ---
            Paragraph footer = new Paragraph("This is a computer-generated receipt. No signature required.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            Paragraph brand = new Paragraph("© 2026 Shivtirth Hostel Management System", smallFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            doc.add(brand);

        } catch (Exception e) {
            System.err.println("⚠ PDF GENERATION FAILED: " + e.getMessage());
        } finally {
            doc.close();
        }

        return out.toByteArray();
    }

    /** Helper: add a label-value row to PDF table */
    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(6);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(6);
        table.addCell(valueCell);
    }
}
