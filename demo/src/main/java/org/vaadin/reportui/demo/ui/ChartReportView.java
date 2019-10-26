package org.vaadin.reportui.demo.ui;

import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJPieChartBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.vaadin.reports.PrintPreviewReport;
import org.vaadin.reportui.demo.domain.CallRepository;
import org.vaadin.reportui.demo.domain.City;
import org.vaadin.reportui.demo.domain.CityCallsCount;

import javax.servlet.annotation.WebServlet;

@Route(value = "chart", layout = MainLayout.class)
public class ChartReportView extends VerticalLayout {

    @WebServlet(value = "/report-image")
    public static class ReportsImageServlet extends ImageServlet {
    }

    public ChartReportView(CallRepository callRepository) {
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

        add(report);
        setPadding(false);
    }

}
