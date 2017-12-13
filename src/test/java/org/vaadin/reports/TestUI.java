package org.vaadin.reports;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.jetty.VaadinJettyServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Alejandro Duarte
 */
public class TestUI extends UI {

    public static void main(String[] args) throws Exception {
        JPAService.init();
        new VaadinJettyServer(9090, TestUI.class).start();
    }

    private TabSheet tabs = new TabSheet();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        addBasicReport();
        addConfiguredReport();
        addDownloadableReport();
        setContent(tabs);
    }

    private void addBasicReport() {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        report.setItems(CallRepository.findAll());
        tabs.addTab(report, "Simple report");
    }

    private void addConfiguredReport() {
        Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();

        AbstractColumn city;

        PrintPreviewReport<Call> report = new PrintPreviewReport<>();
        report.getReportBuilder()
                .setMargins(20, 20, 40, 40)
                .setTitle("Call report")
                .addAutoText("For internal use only", AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 200, headerStyle)
                .addAutoText(LocalDateTime.now().toString(), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, headerStyle)
                .addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 10, headerStyle)
                .setPrintBackgroundOnOddRows(true)
                .addColumn(city = ColumnBuilder.getNew()
                        .setColumnProperty("city", City.class)
                        .setTitle("City")
                        .build())
                .addGroup(new GroupBuilder()
                        .setCriteriaColumn((PropertyColumn) city)
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("client", String.class)
                        .setTitle("Client")
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("phoneNumber", String.class)
                        .setTitle("Phone number")
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("startTime", LocalDateTime.class)
                        .setTitle("Date")
                        .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("startTime", LocalDateTime.class)
                        .setTextFormatter(DateTimeFormatter.ISO_LOCAL_TIME.toFormat())
                        .setTitle("Start time")
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("duration", Integer.class)
                        .setTitle("Duration (seconds)")
                        .build())
                .addColumn(ColumnBuilder.getNew()
                        .setColumnProperty("status", Status.class)
                        .setTitle("Status").build());

        report.setItems(CallRepository.findAll());

        tabs.addTab(report, "Configured report");
    }

    private void addDownloadableReport() {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        SerializableSupplier<List<? extends Call>> itemsSupplier = () -> CallRepository.findAll();
        report.setItems(itemsSupplier.get());

        Button pdf = new Button("Pdf");
        report.downloadPdfOnClick(pdf, "call-report.pdf", itemsSupplier);

        Button xls = new Button("Xls");
        report.downloadXlsOnClick(xls, "call-report.xls", itemsSupplier);

        Button docx = new Button("Docx");
        report.downloadDocxOnClick(docx, "call-report.docx", itemsSupplier);

        Button pptx = new Button("Pptx");
        report.downloadPptxOnClick(pptx, "call-report.pptx", itemsSupplier);

        Button rtf = new Button("Rtf");
        report.downloadRtfOnClick(rtf, "call-report.rtf", itemsSupplier);

        Button odt = new Button("Odt");
        report.downloadOdtOnClick(odt, "call-report.odt", itemsSupplier);

        Button csv = new Button("Csv");
        report.downloadCsvOnClick(csv, "call-report.csv", itemsSupplier);

        Button xml = new Button("Xml");
        report.downloadXmlOnClick(xml, "call-report.xml", itemsSupplier);


        HorizontalLayout buttons = new HorizontalLayout(pdf, xls, docx, pptx, rtf, odt, csv, xml);
        VerticalLayout layout = new VerticalLayout(buttons, report);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);

        tabs.addTab(layout, "Downloadable report");
    }

}
