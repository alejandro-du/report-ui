package org.vaadin.reports;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Alejandro Duarte
 */
public class PrintPreviewReport<T> extends Composite {

    private FastReportBuilder reportBuilder = new FastReportBuilder();
    private Class<T> beanType;
    private Label htmlLabel = new Label("", ContentMode.HTML);

    public PrintPreviewReport(Class<T> type) {
        this.beanType = type;
        PropertySet<T> propertySet = BeanPropertySet.get(type);
        propertySet.getProperties().forEach(this::addColumn);
        VerticalLayout mainLayout = new VerticalLayout(htmlLabel);
        setCompositionRoot(mainLayout);
    }

    private AbstractColumn addColumn(PropertyDefinition<T, ?> propertyDefinition) {
        ColumnBuilder columnBuilder = ColumnBuilder.getNew();
        columnBuilder.setColumnProperty(new ColumnProperty(propertyDefinition.getName(), propertyDefinition.getType().getName()));
        AbstractColumn column = columnBuilder.build();

        reportBuilder.addColumn(column);

        return column;
    }

    public void setItems(List<? extends T> items) {
        DynamicReport report = reportBuilder.build();
        try {
            JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), items);
            HtmlExporter exporter = new HtmlExporter();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.exportReport();
            outputStream.flush();
            htmlLabel.setValue(outputStream.toString("UTF-8"));
            outputStream.close();

        } catch (JRException | IOException e) {
            e.printStackTrace();
        }
    }

}
