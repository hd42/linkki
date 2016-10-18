package org.linkki.framework.ui.application;

import java.io.Serializable;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.linkki.util.StreamUtil;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Overall layout frame for the application. Contains the application header, the main work area and
 * a footer.
 */
@UIScoped
public class ApplicationFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * This is the layout that contains all content. It is the outermost UI component that contains
     * all child-components that are displayed.
     */
    private VerticalLayout content;

    /** The header that is displayed at the top. */
    @Inject
    private ApplicationHeader header;

    /**
     * The main work area displayed in the center. This component takes up most of the available
     * space. All views that are displayed using {@link ApplicationFrame#showView(Class)} are added
     * as child-components to it.
     */
    private VerticalLayout mainArea;

    /** The footer that is displayed at the bottom. */
    @Inject
    private ApplicationFooter footer;

    @Inject
    private CDIViewProvider viewProvider;

    private Navigator navigator;

    public ApplicationFrame() {
        super();
    }

    /**
     * Initializes the layout for the given UI.
     * <p>
     * Can't be done in a layout's constructor as we need the other beans to be injected and also
     * the UI which is still under construction at this point in time.
     */
    public void init(UI ui) {
        content = new VerticalLayout();
        content.setMargin(false);
        content.setSizeFull();

        // Header
        content.addComponent(header);
        header.init();

        // Main area
        mainArea = new VerticalLayout();
        mainArea.setSizeFull();
        content.addComponent(mainArea);
        content.setExpandRatio(mainArea, 1);

        // Footer
        content.addComponent(footer);

        navigator = new Navigator(ui, mainArea);
        navigator.addProvider(viewProvider);
    }

    /**
     * Returns the vertical layout that displays all content. Make sure that it was initialized
     * using {@link #init(UI)} method before calling this method.
     */
    public VerticalLayout getContent() {
        return content;
    }

    /**
     * Attempts to show a view of the given class.
     * <p>
     * Note that navigation to a view might not be performed if there is a
     * {@link com.vaadin.navigator.ViewChangeListener ViewChangeListener} that prohibits the
     * navigation. Thus there is no guarantee that a view of the given class is displayed after this
     * method returns.
     */
    public <T extends View> void showView(Class<T> clazz) {
        showView(clazz, StringUtils.EMPTY);
    }

    /**
     * Attempts to show a view of the given class, appending pathAndParameters to the URL to provide
     * information to the called view.
     * <p>
     * Note that navigation to a view might not be performed if there is a
     * {@link com.vaadin.navigator.ViewChangeListener ViewChangeListener} that prohibits the
     * navigation. Thus there is no guarantee that a view of the given class is displayed after this
     * method returns.
     * <p>
     * Note2: Switching to the same view does not clear the view scope
     * (https://github.com/vaadin/cdi/issues/166). To get a clean view scope we first navigate to
     * the {@link EmptyCdiView} before navigating to the correct new view.
     * 
     * @param pathAndParameters a string containing an URL-Like path as well as URL parameters. Must
     *            not start with &quot;/&quot;. Example:
     *            &quot;part1/part2/arg1=23&amp;arg2=42&quot;.
     */
    public <T extends View> void showView(Class<T> clazz, String pathAndParameters) {
        String currentFragment = navigator.getUI().getPage().getUriFragment();
        String newViewName = Conventions.deriveMappingForView(clazz);
        String newFragment = newViewName + "/" + pathAndParameters;
        if (currentFragment != null && newViewName.equals(getViewName(currentFragment))) {
            navigator.navigateTo(Conventions.deriveMappingForView(EmptyCdiView.class));
        }
        navigator.navigateTo(newFragment);
    }

    protected String getViewName(String fragment) {
        int begin = 0;
        // according to vaadin documentation the currentFragment may start with ! or not
        if (fragment.startsWith("!")) {
            begin = 1;
        }
        int end = fragment.indexOf('/');
        return fragment.substring(begin, end > begin ? end : fragment.length());
    }

    /**
     * Returns the view that is currently displayed in an {@link Optional}, which is empty if no
     * view is displayed (yet).
     */
    public Optional<Component> getCurrentView() {
        return StreamUtil.stream(mainArea).findFirst();
    }

}
