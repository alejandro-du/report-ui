package org.vaadin.reports;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.jetty.VaadinJettyServer;

/**
 * @author Alejandro Duarte
 */
public class TestUI extends UI {

    public static void main(String[] args) throws Exception {
        JPAService.init();
        new VaadinJettyServer(9090, TestUI.class).start();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        PrintPreviewReport<Person> report = new PrintPreviewReport<>(Person.class);
        report.setItems(PersonRepository.findAll());

        VerticalLayout mainLayout = new VerticalLayout(report);
        setContent(mainLayout);
    }

}
