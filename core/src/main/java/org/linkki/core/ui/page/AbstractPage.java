package org.linkki.core.ui.page;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.PostConstruct;

import org.linkki.core.binding.BindingContext;
import org.linkki.core.binding.BindingManager;
import org.linkki.core.ui.section.AbstractSection;
import org.linkki.core.ui.util.ComponentFactory;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * A page allows displaying and editing data in a vertical layout. It usually consists of so called
 * sections but can also contain arbitrary UI components. Sections (or other components) can be
 * added to take up either the whole width or 50% of the page.
 * 
 * If the page is created via injection framework, the {@link #init()} method is called
 * automatically and ensures that the {@link #createContent()} method is called. Additionally,
 * margins are added to the page.
 * 
 * Note: If the page is not injected you need to call {@link #init()} manually!
 * 
 * @see AbstractSection
 */
public abstract class AbstractPage extends VerticalLayout implements Page {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an page without top margin and with margins on left, right and bottom.
     */
    public AbstractPage() {
        setMargin(new MarginInfo(false, true, true, true));
    }

    @PostConstruct
    public final void init() {
        createContent();
    }

    /**
     * Adds the given component / section to the page taking 100% of the page width.
     */
    protected void add(Component section) {
        addComponent(section);
    }

    /**
     * Adds the two components / sections to the page, each taking 50% of the page width.
     */
    protected void add(Component leftSection, Component rightSection) {
        add(0, leftSection, rightSection);
    }

    /**
     * Adds the two components / sections to the page, each taking 50% of the page width.
     */
    protected void add(Component... sections) {
        add(0, sections);
    }

    /**
     * Adds the two components / sections to the page, and indents them. Each section is taking 50%
     * of the rest of the page width.
     * 
     * @param indentation level of indentation, indentation size is indentation * 30px. Indentation
     *            0 or less does not indent.
     */
    protected void add(int indentation, Component... sections) {
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidth("100%");
        wrapper.setSpacing(true);
        addComponent(wrapper);
        if (indentation > 0) {
            ComponentFactory.addHorizontalFixedSizeSpacer(wrapper, 30 * indentation);
        }
        wrapper.addComponents(sections);
        for (Component component : wrapper) {
            wrapper.setExpandRatio(component, 1f / sections.length);
        }
    }

    /**
     * Scrolls the view so that the given model object is visible.
     * 
     * @param modelObject The model object to show
     */
    public void scrollIntoView(Object modelObject) {
        // TODO not implemented, yet. Can this be done in a generic way?
    }

    /**
     * Refreshes the content displayed on this page. Data bindings are reloaded, but no sections are
     * removed/added.
     * <p>
     * This method should be overridden if the page contains components that do not use data binding
     * and have to be refreshed manually.
     */
    @Override
    @OverridingMethodsMustInvokeSuper
    public void reloadBindings() {
        getBindingContext().updateUI();
    }

    /**
     * Returns the binding manager used to obtain or create binding contexts.
     * 
     * @see #getBindingContext()
     */
    protected abstract BindingManager getBindingManager();

    /**
     * Returns a cached binding context if present, otherwise creates a new one. Caches one binding
     * context for each class.
     */
    protected BindingContext getBindingContext() {
        return getBindingManager().getExistingContextOrStartNewOne(getClass());
    }

}
