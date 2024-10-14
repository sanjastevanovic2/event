package com.event.service;

import com.event.dto.EventDto;
import com.event.entity.Event;
import com.event.entity.User;
import com.event.exception.ExceptionCode;
import com.event.repository.EventRepository;
import com.event.validator.EventValidator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;

    @Transactional(readOnly = true)
    public byte[] generateExcel(HttpServletResponse response) throws Exception {

        List<Event> eventList = eventRepository.findAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Event Info");
        HSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("Event id");
        row.createCell(1).setCellValue("Name");
        row.createCell(2).setCellValue("Description");
        row.createCell(3).setCellValue("Location");
        row.createCell(4).setCellValue("Start time");
        row.createCell(5).setCellValue("End time");

        HSSFCellStyle dateCellStyle = workbook.createCellStyle();
        HSSFDataFormat dateFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd-mm-yyyy"));

        int dataRowIndex = 1;

        for (Event event : eventList) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(event.getId());
            dataRow.createCell(1).setCellValue(event.getName());
            dataRow.createCell(2).setCellValue(event.getDescription());
            dataRow.createCell(3).setCellValue(event.getLocation());

            if(event.getStartTime() != null) {
                HSSFCell dateCell = dataRow.createCell(4);
                dateCell.setCellValue((event.getStartTime()));
                dateCell.setCellStyle(dateCellStyle);
            }
            if(event.getEndTime() != null) {
                HSSFCell dateCell = dataRow.createCell(5);
                dateCell.setCellValue((event.getEndTime()));
                dateCell.setCellStyle(dateCellStyle);
            }

            dataRowIndex++;
        }


        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();
            return byteArrayOutputStream.toByteArray(); // Return the byte array
        }
    }

    @Transactional(readOnly = true)
    public void generate(Long eventId, HttpServletResponse response) throws DocumentException, IOException {
        eventValidator.validateEventExistence(eventId);
        Event event = eventRepository.findById(eventId).get();
        Set<User> users = event.getSubscribedUserList();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);
        Paragraph paragraph1 = new Paragraph("List of the Subscribed Users", fontTiltle);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph1);
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new int[] {2,2,2,2,2});
        table.setSpacingBefore(5);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.BLUE);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);
        cell.setPhrase(new Phrase("JMBG", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("First name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Username", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);
        for (User user: users) {
            table.addCell(user.getJmbg());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getUsername());
            table.addCell(user.getEmail());
        }
        document.add(table);
        document.close();
    }
}
