package org.vaadin.reportui.demo;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJPieChartBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.Route;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.springframework.data.domain.Sort;
import org.vaadin.reports.PrintPreviewReport;

import javax.servlet.annotation.WebServlet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alejandro Duarte
 */
@Route("")
public class MainView extends SplitLayout {

    private final CallRepository callRepository;

    @WebServlet("/report-image")
    public static class ReportsImageServlet extends ImageServlet {
    }

    private VerticalLayout menuLayout = new VerticalLayout();
    private VerticalLayout reportContainer = new VerticalLayout();

    public MainView(CallRepository callRepository) {
        this.callRepository = callRepository;

        reportContainer.getElement().setAttribute("theme", "");
        Div div = new Div(reportContainer);

        addToPrimary(menuLayout);
        addToSecondary(div);
        setSizeFull();
        setSplitterPosition(30);

        addReport("Simple report", () -> buildSimpleReport());
        addReport("Configured report", () -> buildConfiguredReport());
        addReport("Chart report", () -> buildChartReport());
        addReport("Downloadable report", () -> buildDownloadableReport());
    }

    private void addReport(String name, SerializableSupplier<Component> reportSupplier) {
        Button button = new Button(name, e -> {
            reportContainer.removeAll();
            reportContainer.add(reportSupplier.get());
        });
        button.getElement().setAttribute("theme", "tertiary");
        menuLayout.add(button);
    }

    private Component buildSimpleReport() {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        report.setItems(callRepository.findAll(Sort.by("city", "status")));
        return report;
    }

    private Component buildConfiguredReport() {
        Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
        Style groupStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();

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
                        .setStyle(groupStyle)
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

        report.setItems(callRepository.findAll(Sort.by("city", "status")));

        return report;
    }

    private Component buildChartReport() {
        AbstractColumn city;
        AbstractColumn calls;
        PrintPreviewReport<CityCallsCount> report = new PrintPreviewReport<>();
        report.getReportBuilder()
                .setTitle("Worldwide Distribution")
                .addColumn(city = ColumnBuilder.getNew()
                        .setColumnProperty("city", City.class)
                        .setTitle("City")
                        .build())
                .addColumn(calls = ColumnBuilder.getNew()
                        .setColumnProperty("calls", Long.class)
                        .setTitle("Calls")
                        .build())
                .addChart(new DJPieChartBuilder()
                        .setColumnGroup((PropertyColumn) city)
                        .addSerie(calls)
                        .build());

        report.setItems(callRepository.getCountPerCity());

        return report;
    }

    private Component buildDownloadableReport() {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        SerializableSupplier<List<? extends Call>> itemsSupplier = () -> callRepository.findAll(Sort.by("city", "status"));
        report.setItems(itemsSupplier.get());

        HorizontalLayout anchors = new HorizontalLayout();

        for (PrintPreviewReport.Format format : Arrays.asList(PrintPreviewReport.Format.values())) {
            Anchor anchor = new Anchor(report.getStreamResource("call-report." + format.name().toLowerCase(), itemsSupplier, format), format.name());
            anchor.getElement().setAttribute("download", true);
            anchors.add(anchor);
        }

        VerticalLayout layout = new VerticalLayout(anchors, report);
        layout.getElement().setAttribute("theme", "spacing");
        return layout;
    }

}
