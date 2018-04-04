[![Published on vaadin.com/directory](https://img.shields.io/vaadin-directory/status/report-ui.svg)](https://img.shields.io/vaadin-directory/status/report-ui.svg)
[![Stars on vaadin.com/directory](https://img.shields.io/vaadin-directory/star/report-ui.svg)](https://img.shields.io/vaadin-directory/star/report-ui.svg)
[![Latest version on vaadin.com/directory](https://img.shields.io/vaadin-directory/v/report-ui.svg)](https://img.shields.io/vaadin-directory/v/report-ui.svg)

Report UI Add-on provides an easy way to render JasperReports in Vaadin applications through DynamicJasper.

# Basic usage
Say, you have the following domain/entity/Java Bean class:
```java
public class Call {

    private Long id;
    private String client;
    private String phoneNumber;
    private City city; // enum
    private LocalDateTime startTime;
    private Integer duration;
    private Status status; // enum

    ... getters and setters ...
}
```
&nbsp;

You can create a new report and add it to any Vaadin layout as follows:
```java
PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class);
report.setItems(Repository.findAll());
layout.addComponent(report);
```
&nbsp;

# Advanced usage

You can optionally set the order of the columns as follows:
```java
PrintPreviewReport<Call> report = new PrintPreviewReport<>(Call.class, "client", "city", "phoneNumber", "startTime", "duration", "status");
```
&nbsp;

If you prefer, you can use the default constructor to avoid creating the columns automatically. In that case, you can use the `getReportBuilder()` method to set the columns manually:
```java
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
```
&nbsp;

See the [DynamicJasper documentation](http://dynamicjasper.com/documentation-examples) for more configuration examples.

You can make extend a component for downloading in several formats. For example, to make a button download a PDF file when clicked, you can use the following:
```java
Button button = new Button("Pdf");
report.downloadPdfOnClick(button, "call-report.pdf", itemsSupplier);
```
&nbsp;

Apache POI is required when using some of the exporting methods:
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>3.10-FINAL</version>
</dependency>
```
&nbsp;

In order to render charts you have to configure an `ImageServlet`. For example:
```
@WebServlet("/report-image")
public static class ReportsImageServlet extends ImageServlet {
}
```

You can configure the URL pattern using the `setImageServletPathPattern` method  (default to `report-image?image={0}`).
