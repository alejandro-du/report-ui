package org.vaadin.reportui.demo.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.PWA;
import org.vaadin.reportui.demo.DemoUtils;

import java.util.HashMap;
import java.util.Map;

@PWA(name = "Report-ui add-on demo", shortName = "Report-ui demo")
public class MainLayout extends AppLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private Tabs tabs = new Tabs();
    private Map<Tab, Class<? extends HasComponents>> tabToView = new HashMap<>();
    private Map<Class<? extends HasComponents>, Tab> viewToTab = new HashMap<>();

    public MainLayout() {
        AppLayout appLayout = new AppLayout();

        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        img.setHeight("44px");
        addToNavbar(new DrawerToggle(), img);

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(event -> tabsSelectionChanged(event));
        addToDrawer(tabs);

        addTab(HomeView.class);
        addTab(SimpleReportView.class);
        addTab(ConfiguredReportView.class);
        addTab(ChartReportView.class);
        addTab(DownloadableReportView.class);
    }

    private void tabsSelectionChanged(Tabs.SelectedChangeEvent event) {
        if (event.isFromClient()) {
            UI.getCurrent().navigate((Class<? extends Component>) tabToView.get(event.getSelectedTab()));
        }
    }

    private void addTab(Class<? extends HasComponents> clazz) {
        Tab tab = new Tab(DemoUtils.getNameAsPhrase(clazz));
        tabs.add(tab);
        tabToView.put(tab, clazz);
        viewToTab.put(clazz, tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Class<?> viewClass = event.getNavigationTarget();
        tabs.setSelectedTab(viewToTab.get(viewClass));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Class<? extends HasComponents> viewClass = tabToView.get(tabs.getSelectedTab());
        if (!HomeView.class.equals(viewClass)) {
            HorizontalLayout footer = new HorizontalLayout(new Anchor(DemoUtils.getGitHubLink(viewClass), "Source code"));
            footer.setMargin(true);
            ((HasComponents) getContent()).add(footer);
        }
    }

}
