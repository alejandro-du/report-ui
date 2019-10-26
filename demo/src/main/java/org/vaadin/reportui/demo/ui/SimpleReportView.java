package org.vaadin.reportui.demo.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;
import org.vaadin.reports.PrintPreviewReport;
import org.vaadin.reportui.demo.domain.Call;
import org.vaadin.reportui.demo.domain.CallRepository;

@Route(value = "simple", layout = MainLayout.class)
public class SimpleReportView extends VerticalLayout {

    public SimpleReportView(CallRepository callRepository) {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        report.setItems(callRepository.findAll(Sort.by("city", "status")));

        add(report);
        setPadding(false);
    }

}
