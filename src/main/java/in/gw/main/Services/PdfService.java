package in.gw.main.Services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import in.gw.main.Entity.RentPayment;
import in.gw.main.Entity.StudentProfile;
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

    // =============================================
    // ADMISSION FORM PDF GENERATION
    // =============================================

    /**
     * Generate an Admission Form PDF for a student.
     * Includes personal details, academic details, parent/guardian info.
     */
    public byte[] generateAdmissionForm(StudentProfile profile) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            // --- Fonts ---
            Font titleFont   = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(26, 26, 46));
            Font headingFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(67, 97, 238));
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(26, 26, 46));
            Font normalFont  = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font boldFont    = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
            Font smallFont   = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);

            // --- Header ---
            Paragraph title = new Paragraph("SHIVTIRTH HOSTEL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph subtitle = new Paragraph("Student Admission Form", headingFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(5);
            doc.add(subtitle);

            // --- Status Badge ---
            String statusText = profile.getStatus() != null ? profile.getStatus().name() : "N/A";
            Paragraph statusPara = new Paragraph("Status: " + statusText, boldFont);
            statusPara.setAlignment(Element.ALIGN_CENTER);
            statusPara.setSpacingAfter(15);
            doc.add(statusPara);

            // --- Divider ---
            addDivider(doc);

            // --- PERSONAL INFORMATION ---
            Paragraph personalTitle = new Paragraph("Personal Information", sectionFont);
            personalTitle.setSpacingBefore(10);
            personalTitle.setSpacingAfter(8);
            doc.add(personalTitle);

            PdfPTable personalTable = new PdfPTable(2);
            personalTable.setWidthPercentage(100);
            personalTable.setWidths(new float[]{1f, 1.5f});

            String name = profile.getUser() != null ? profile.getUser().getName() : "-";
            String email = profile.getUser() != null ? profile.getUser().getEmail() : "-";

            addRow(personalTable, "Full Name", name, boldFont, normalFont);
            addRow(personalTable, "Email", email, boldFont, normalFont);
            addRow(personalTable, "Mobile Number", profile.getPhoneNumber(), boldFont, normalFont);
            addRow(personalTable, "Date of Birth", profile.getDateOfBirth(), boldFont, normalFont);
            addRow(personalTable, "Gender", profile.getGender(), boldFont, normalFont);
            addRow(personalTable, "Aadhar Number", profile.getAadharNumber(), boldFont, normalFont);
            addRow(personalTable, "Permanent Address", profile.getAddress(), boldFont, normalFont);

            doc.add(personalTable);

            // --- ACADEMIC DETAILS ---
            addDivider(doc);
            Paragraph academicTitle = new Paragraph("Academic Details", sectionFont);
            academicTitle.setSpacingBefore(10);
            academicTitle.setSpacingAfter(8);
            doc.add(academicTitle);

            PdfPTable academicTable = new PdfPTable(2);
            academicTable.setWidthPercentage(100);
            academicTable.setWidths(new float[]{1f, 1.5f});

            addRow(academicTable, "College Name", profile.getCollegeName(), boldFont, normalFont);
            addRow(academicTable, "Course", profile.getCourse(), boldFont, normalFont);
            addRow(academicTable, "Year of Study", profile.getYearOfStudy(), boldFont, normalFont);
            addRow(academicTable, "Academic Year", profile.getAcademicYear(), boldFont, normalFont);

            doc.add(academicTable);

            // --- PARENT / GUARDIAN DETAILS ---
            addDivider(doc);
            Paragraph parentTitle = new Paragraph("Parent / Guardian Details", sectionFont);
            parentTitle.setSpacingBefore(10);
            parentTitle.setSpacingAfter(8);
            doc.add(parentTitle);

            PdfPTable parentTable = new PdfPTable(2);
            parentTable.setWidthPercentage(100);
            parentTable.setWidths(new float[]{1f, 1.5f});

            addRow(parentTable, "Parent/Guardian Name", profile.getParentName(), boldFont, normalFont);
            addRow(parentTable, "Parent Contact", profile.getParentContact(), boldFont, normalFont);

            doc.add(parentTable);

            // --- ROOM DETAILS (if assigned) ---
            if (profile.getRoom() != null) {
                addDivider(doc);
                Paragraph roomTitle = new Paragraph("Room Assignment", sectionFont);
                roomTitle.setSpacingBefore(10);
                roomTitle.setSpacingAfter(8);
                doc.add(roomTitle);

                PdfPTable roomTable = new PdfPTable(2);
                roomTable.setWidthPercentage(100);
                roomTable.setWidths(new float[]{1f, 1.5f});

                addRow(roomTable, "Room Number", profile.getRoom().getRoomNumber(), boldFont, normalFont);
                addRow(roomTable, "Room Type", profile.getRoom().getRoomType().name(), boldFont, normalFont);
                addRow(roomTable, "Floor", String.valueOf(profile.getRoom().getFloor()), boldFont, normalFont);
                addRow(roomTable, "Monthly Rent", "₹" + String.format("%.0f", profile.getRoom().getMonthlyRent()), boldFont, normalFont);

                doc.add(roomTable);
            }

            // --- Footer ---
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("This is a computer-generated document. No signature required.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            Paragraph brand = new Paragraph("© 2026 Shivtirth Hostel Management System", smallFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            doc.add(brand);

        } catch (Exception e) {
            System.err.println("⚠ ADMISSION PDF GENERATION FAILED: " + e.getMessage());
        } finally {
            doc.close();
        }

        return out.toByteArray();
    }

    /** Helper: add a blue divider line */
    private void addDivider(Document doc) throws DocumentException {
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingBefore(5);
        PdfPCell divCell = new PdfPCell();
        divCell.setBorderWidthBottom(1.5f);
        divCell.setBorderColorBottom(new Color(67, 97, 238));
        divCell.setBorderWidthTop(0);
        divCell.setBorderWidthLeft(0);
        divCell.setBorderWidthRight(0);
        divCell.setFixedHeight(3);
        divider.addCell(divCell);
        doc.add(divider);
    }
}
