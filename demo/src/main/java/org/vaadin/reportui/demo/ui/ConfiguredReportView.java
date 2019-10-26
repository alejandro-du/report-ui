package org.vaadin.reportui.demo.ui;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;
import org.vaadin.reports.PrintPreviewReport;
import org.vaadin.reportui.demo.domain.Call;
import org.vaadin.reportui.demo.domain.CallRepository;
import org.vaadin.reportui.demo.domain.City;
import org.vaadin.reportui.demo.domain.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route(value = "configured", layout = MainLayout.class)
public class ConfiguredReportView extends VerticalLayout {

    public ConfiguredReportView(CallRepository callRepository) {
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

        add(report);
        setPadding(false);
    }

}
