package org.vaadin.reportui.demo.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;
import org.vaadin.reports.PrintPreviewReport;
import org.vaadin.reportui.demo.domain.Call;
import org.vaadin.reportui.demo.domain.CallRepository;

import java.util.Arrays;
import java.util.List;

@Route(value = "downloadable", layout = MainLayout.class)
public class DownloadableReportView extends VerticalLayout {

    public DownloadableReportView(CallRepository callRepository) {
        PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
        SerializableSupplier<List<? extends Call>> itemsSupplier = () -> callRepository.findAll(Sort.by("city", "status"));
        report.setItems(itemsSupplier.get());

        HorizontalLayout anchors = new HorizontalLayout();

        for (PrintPreviewReport.Format format : Arrays.asList(PrintPreviewReport.Format.values())) {
            Anchor anchor = new Anchor(report.getStreamResource("call-report." + format.name().toLowerCase(), itemsSupplier, format), format.name());
            anchor.getElement().setAttribute("download", true);
            anchors.add(anchor);
        }

        add(anchors, report);
        setHorizontalComponentAlignment(Alignment.CENTER, anchors);
        setPadding(false);
    }

}
