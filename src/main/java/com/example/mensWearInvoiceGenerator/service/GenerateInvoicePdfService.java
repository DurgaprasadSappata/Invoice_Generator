package com.example.mensWearInvoiceGenerator.service;

import com.example.mensWearInvoiceGenerator.dto.InvoiceItemRequest;
import com.example.mensWearInvoiceGenerator.dto.InvoiceRequest;
import com.example.mensWearInvoiceGenerator.model.ClothingItems;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateInvoicePdfService {

    @Autowired
    private ClothingItemService service;

    public byte[] generateInvoicePdf(InvoiceRequest invoiceRequest) throws IOException, MalformedURLException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(5, 5, 5, 5);


        Table topBlock = new Table(new float[]{1});
        topBlock.setWidth(UnitValue.createPercentValue(100));

        float pageWidth = pdf.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        Paragraph p = new Paragraph();
        p.add("GSTIN/UIN: 27ABCDE1234F1Z5").setFontSize(9).setBold();

        p.add(new Tab());
        p.addTabStops(new TabStop(pageWidth / 2, TabAlignment.CENTER));
        p.add("TAX INVOICE").setFontSize(9).setBold();

        p.add(new Tab());
        p.addTabStops(new TabStop(pageWidth, TabAlignment.RIGHT));
        p.add("Original/Recipient").setFontSize(9).setBold();

        topBlock.addCell(p);
        document.add(topBlock);

        // Logo (Assuming you have the image on the classpath)
        String logoUrl = "https://as2.ftcdn.net/jpg/03/64/47/97/1000_F_364479716_YrB8mGlcz5TwzEyOzeYZG3UGjZ5AzrLO.jpg";
        ImageData imageData = ImageDataFactory.create(logoUrl);
        Image logo = new Image(imageData).scaleToFit(80,80);

        float[] columnWidth = {12, 88};
        Table details = new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
        details.setBorder(new SolidBorder(0.5f));

        Cell logoCell = new Cell()
                .add(logo)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5);
        Paragraph companyDetails = new Paragraph()
                .add(new Text("AllAbout Mens Wear\n").setBold().setFontSize(12))
                .add("Shop No.10, Medical Square , Nagpur, Maharashtra, India\nMobile No: 8805606816, 8432462777\nEmail: allAboutmens@gmail.in\nState Code: 27")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginLeft(-20);
        Cell detailsCell = new Cell()
                .add(companyDetails)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5);

        details.addCell(logoCell);
        details.addCell(detailsCell);
        document.add(details);

        // Meta Info + Bill To Table
        Table infoTable = new Table(new float[]{50, 50});
        infoTable.setWidth(UnitValue.createPercentValue(100));

        // Left column: Bill to
        StringBuilder billTo = new StringBuilder();
        billTo.append("Bill to: " + invoiceRequest.getCustomerName()).append("\n")
                .append(invoiceRequest.getCustomerAddress()).append("\n")
                .append("Email: ").append(invoiceRequest.getCustomerEmail()).append("\n")
                .append("Mobile: ").append(invoiceRequest.getCustomerPhone()).append("\n");

        infoTable.addCell(new Cell().add(new Paragraph(billTo.toString()))
                .setPadding(3).setTextAlignment(TextAlignment.LEFT));

        // Right column: Meta info
        String invoiceNo = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String rightInfo = "Date: " + date + "\nInvoice No: " + invoiceNo +
                "\nPayment Mode: "+invoiceRequest.getPaymentMode();

        infoTable.addCell(new Cell().add(new Paragraph(rightInfo))
                .setPadding(3).setTextAlignment(TextAlignment.LEFT));

        document.add(infoTable);

        // Table Headers
        float[] columnWidths = {30f, 130f, 50f, 30f, 50f, 50f, 70f, 40f, 40f, 40f, 60f};
        Table table = new Table(UnitValue.createPointArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        String[] headers = {"Sr.No", "Product Name & Description", "HSN", "Qty", "Rate", "Disc.", "Taxable Amt.", "CGST %", "SGST %", "IGST %", "Amount"};
        for (String headerText : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(headerText).setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(0.5f))
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
        }

        int srNo = 1;
        double totalTaxable = 0, totalCgst = 0, totalSgst = 0, totalAmount = 0;
        int totalQty = 0;

        for (InvoiceItemRequest itemReq : invoiceRequest.getItems()) {
            ClothingItems item = service.findItemByTagId(itemReq.getTagId());

            double gross = item.getPrice() * itemReq.getQuantity();
            double discount = gross * (itemReq.getDiscount() / 100.0);
            double taxable = gross - discount;
            double cgst = taxable * (itemReq.getCgstRate() / 100.0);
            double sgst = taxable * (itemReq.getSgstRate() / 100.0);
            double igst = 0;
            double amount = taxable + cgst + sgst + igst;

            totalQty += itemReq.getQuantity();
            totalTaxable += taxable;
            totalCgst += cgst;
            totalSgst += sgst;
            totalAmount += amount;

            table.addCell(new Cell().add(new Paragraph(String.valueOf(srNo++)))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(item.getName()))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(item.getHsnCode()))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(itemReq.getQuantity())))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(item.getPrice())))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(discount)))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(taxable)))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(itemReq.getCgstRate())))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(itemReq.getSgstRate())))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph("0"))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
            table.addCell(new Cell().add(new Paragraph(format(amount)))
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(0.5f))
                    .setBorderRight(new SolidBorder(0.5f)));
        }

        int itemCount = invoiceRequest.getItems().size();
        int columns = 11;
        for (int i = itemCount; i < 10; i++) {
            for (int j = 0; j < columns; j++) {
                table.addCell(new Cell().add(new Paragraph(" "))
                        .setMinHeight(20)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorderTop(Border.NO_BORDER)
                        .setBorderBottom(Border.NO_BORDER)
                        .setBorderLeft(new SolidBorder(0.5f))
                        .setBorderRight(new SolidBorder(0.5f)));
            }
        }

        document.add(table);

        // Summary Section
        Table summary = new Table(new float[]{1,1});
        summary.setWidth(UnitValue.createPercentValue(100));

        Cell rightBlock = new Cell();
        rightBlock.setBorder(new SolidBorder(0.5f));
        rightBlock.setPadding(8);

        Table totals = new Table(2);
        totals.setWidth(UnitValue.createPercentValue(100));
        totals.addCell(getCell("Total Quantity:", TextAlignment.LEFT).setBold());
        totals.addCell(getCell(String.valueOf(totalQty), TextAlignment.RIGHT).setBold());
        totals.addCell(getCell("Total Amount Before Tax:", TextAlignment.LEFT));
        totals.addCell(getCell("₹ " + format(totalTaxable), TextAlignment.RIGHT));
        totals.addCell(getCell("Total CGST Amt.:", TextAlignment.LEFT));
        totals.addCell(getCell("₹ " + format(totalCgst), TextAlignment.RIGHT));
        totals.addCell(getCell("Total SGST Amt.:", TextAlignment.LEFT));
        totals.addCell(getCell("₹ " + format(totalSgst), TextAlignment.RIGHT));
        totals.addCell(getCell("Total IGST Amt.:", TextAlignment.LEFT));
        totals.addCell(getCell("₹ " + format(0), TextAlignment.RIGHT));
        totals.addCell(getCell("Tax Amount : GST", TextAlignment.LEFT));
        totals.addCell(getCell("₹ " + format(totalCgst + totalSgst), TextAlignment.RIGHT));
        totals.addCell(getCell("Total Amount After Tax:", TextAlignment.LEFT).setBold());
        totals.addCell(getCell("₹ " + format(totalAmount), TextAlignment.RIGHT).setBold());

        rightBlock.add(totals);
        summary.addCell(rightBlock);

        document.add(summary);

        Table amountInWordsTable = new Table(new float[]{1});
        amountInWordsTable.setWidth(UnitValue.createPercentValue(100));

        // Add amount in words
        Paragraph amountInWords = new Paragraph()
                .add(new Text("Amount In Word: ").setBold())
                .add(new Text(" Rupees "+ convert((int) totalAmount) + " Only"));
        amountInWordsTable.addCell(amountInWords);
        document.add(amountInWordsTable);

        Table footerTable = new Table(new float[]{5, 1, 2});
        footerTable.setWidth(UnitValue.createPercentValue(100));
        // Terms & Signatures
        Cell terms = new Cell();
        terms.add(new Paragraph("Terms & Conditions :-").setBold());
        terms.add(new Paragraph("""
                1. Subject to Maharashtra Jurisdiction.E. & O.E.
                2. No liability accepted for any breakage.
                3. Goods once sold will not be taken back or exchange.
                4. Any warranty claim will not be accepted without warranty card."""));
        terms.setTextAlignment(TextAlignment.LEFT);
        terms.setVerticalAlignment(VerticalAlignment.TOP);
        terms.setBorder(new SolidBorder(0.5f));

        Cell receiverSign = new Cell();
        receiverSign.add(new Paragraph("\n\n\nReceiver's Signature").setBold());
        receiverSign.setTextAlignment(TextAlignment.CENTER);
        receiverSign.setVerticalAlignment(VerticalAlignment.BOTTOM);
        receiverSign.setBorder(new SolidBorder(0.5f));

        Cell authSign = new Cell();
        authSign.add(new Paragraph("\n\n\nAuthorised Signatory").setBold());
        authSign.setTextAlignment(TextAlignment.CENTER);
        authSign.setVerticalAlignment(VerticalAlignment.BOTTOM);
        authSign.setBorder(new SolidBorder(0.5f));

        footerTable.addCell(terms);
        footerTable.addCell(receiverSign);
        footerTable.addCell(authSign);


        document.add(footerTable);
        document.add(new Paragraph("\nTHANK YOU FOR SHOPPING WITH US").setTextAlignment(TextAlignment.CENTER).setFontSize(10));

        document.close();
        return output.toByteArray();
    }

    private Cell getCell(String text, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(2)
                .setTextAlignment(alignment)
                .setBorder(Border.NO_BORDER);
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }

    public static String convert(int number) {
        return new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT).format(number);
    }
}
