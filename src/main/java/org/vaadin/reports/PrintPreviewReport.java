package org.vaadin.reports;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Alejandro Duarte
 */
public class PrintPreviewReport<T> extends Composite {

    private DynamicReportBuilder reportBuilder;
    private DynamicReport report;
    private JasperPrint print;

    private Label htmlLabel = new Label("", ContentMode.HTML);
    private String imageServletPathPattern = "report-image?image={0}";

    public PrintPreviewReport() {
        VerticalLayout mainLayout = new VerticalLayout(htmlLabel);
        mainLayout.setMargin(false);
        setCompositionRoot(mainLayout);
        reportBuilder = buildReportBuilder();
    }

    public PrintPreviewReport(Class<T> type) {
        this();
        PropertySet<T> propertySet = BeanPropertySet.get(type);
        propertySet.getProperties().forEach(this::addColumn);
    }

    public PrintPreviewReport(Class<T> type, String... columnIds) {
        this();
        PropertySet<T> propertySet = BeanPropertySet.get(type);

        for (int i = 0; i < columnIds.length; i++) {
            String columnId = columnIds[i];
            PropertyDefinition<T, ?> propertyDefinition = propertySet.getProperties()
                    .filter(p -> columnId.equals(p.getName()))
                    .findFirst().get();
            addColumn(propertyDefinition);
        }
    }

    @Override
    public void detach() {
        super.detach();
        VaadinSession.getCurrent().getSession().removeAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE);
    }

    public void setItems(List<? extends T> items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (report == null) {
                report = reportBuilder.build();
            }

            print = buildJasperPrint(items, report);
            HtmlExporter exporter = buildHtmlExporter();

            SimpleHtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(outputStream);
            exporterOutput.setImageHandler(new WebHtmlResourceHandler(imageServletPathPattern));

            exporter.setExporterOutput(exporterOutput);
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.exportReport();
            outputStream.flush();
            htmlLabel.setValue(outputStream.toString("UTF-8"));

        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadPdfOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getPdfStream(itemsSupplier));
    }

    public void downloadXlsOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getXlsStream(itemsSupplier));
    }

    public void downloadDocxOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getDocxStream(itemsSupplier));
    }

    public void downloadPptxOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getPptxStream(itemsSupplier));
    }

    public void downloadRtfOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getRtfStream(itemsSupplier));
    }

    public void downloadOdtOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getOdtStream(itemsSupplier));
    }

    public void downloadCsvOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getCsvStream(itemsSupplier));
    }

    public void downloadXmlOnClick(AbstractComponent component, String fileName, SerializableSupplier<List<? extends T>> itemsSupplier) {
        downloadOnClick(component, fileName, () -> getXmlStream(itemsSupplier));
    }

    public DynamicReportBuilder getReportBuilder() {
        return reportBuilder;
    }

    public Label getHtmlLabel() {
        return htmlLabel;
    }

    public String getImageServletPathPattern() {
        return imageServletPathPattern;
    }

    public void setImageServletPathPattern(String imageServletPathPattern) {
        this.imageServletPathPattern = imageServletPathPattern;
    }

    protected void downloadOnClick(AbstractComponent component, String fileName, StreamResource.StreamSource streamSource) {
        FileDownloader downloader = new FileDownloader(new StreamResource(streamSource, fileName));
        downloader.extend(component);
    }

    protected InputStream getPdfStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildPdfExporter(), os -> new SimpleOutputStreamExporterOutput(os));
    }

    protected InputStream getXlsStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildXlsExporter(), os -> new SimpleOutputStreamExporterOutput(os));
    }

    protected InputStream getDocxStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildDocxExporter(), os -> new SimpleOutputStreamExporterOutput(os));
    }

    protected InputStream getPptxStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildPptxExporter(), os -> new SimpleOutputStreamExporterOutput(os));
    }

    protected InputStream getRtfStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildRtfExporter(), os -> new SimpleWriterExporterOutput(os));
    }

    protected InputStream getOdtStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildOdtExporter(), os -> new SimpleOutputStreamExporterOutput(os));
    }

    protected InputStream getCsvStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildCsvExporter(), os -> new SimpleWriterExporterOutput(os));
    }

    protected InputStream getXmlStream(SerializableSupplier<List<? extends T>> itemsSupplier) {
        return getStream(itemsSupplier, buildXmlExporter(), os -> new SimpleXmlExporterOutput(os));
    }

    protected InputStream getStream(SerializableSupplier<List<? extends T>> itemsSupplier, JRAbstractExporter exporter, SerializableFunction<OutputStream, ExporterOutput> exporterOutputFunction) {
        List<? extends T> items = itemsSupplier.get();
        setItems(items);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            exporter.setExporterOutput(exporterOutputFunction.apply(outputStream));
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.exportReport();
            outputStream.flush();

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected AbstractColumn addColumn(PropertyDefinition<T, ?> propertyDefinition) {
        AbstractColumn column = ColumnBuilder.getNew()
                .setColumnProperty(new ColumnProperty(propertyDefinition.getName(), propertyDefinition.getType().getName()))
                .build();

        column.setTitle(propertyDefinition.getCaption());
        reportBuilder.addColumn(column);

        return column;
    }

    protected DynamicReportBuilder buildReportBuilder() {
        return new FastReportBuilder()
                .setUseFullPageWidth(true)
                .setWhenNoData("(no data)", new Style());
    }

    protected JasperPrint buildJasperPrint(List<? extends T> items, DynamicReport report) throws JRException {
        JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), items);
        VaadinSession.getCurrent().getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
        return print;
    }

    protected HtmlExporter buildHtmlExporter() {
        return new HtmlExporter();
    }

    protected JRPdfExporter buildPdfExporter() {
        return new JRPdfExporter();
    }

    protected JRXlsExporter buildXlsExporter() {
        return new JRXlsExporter();
    }

    protected JRDocxExporter buildDocxExporter() {
        return new JRDocxExporter();
    }

    protected JRPptxExporter buildPptxExporter() {
        return new JRPptxExporter();
    }

    protected JRRtfExporter buildRtfExporter() {
        return new JRRtfExporter();
    }

    protected JROdtExporter buildOdtExporter() {
        return new JROdtExporter();
    }

    protected JRCsvExporter buildCsvExporter() {
        return new JRCsvExporter();
    }

    protected JRXmlExporter buildXmlExporter() {
        return new JRXmlExporter();
    }

}
