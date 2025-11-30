package dev.iamkavindu.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import dev.iamkavindu.service.JsonParserService;
import dev.iamkavindu.errors.TemplateRenderException;
import dev.iamkavindu.service.TemplateRenderService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MainView - Velocity Template Renderer
 * <p>
 * A web-based tool for rendering HTML content with Apache Velocity syntax.
 * Uses sandboxed iframe for secure HTML preview with optional JavaScript execution.
 */
@Route("")
public class MainView extends VerticalLayout implements LocaleChangeObserver {

    // Supported locales
    private static final Locale LOCALE_EN = Locale.ENGLISH;
    private static final Locale LOCALE_SI = Locale.forLanguageTag("si");

    // Services (injected via constructor)
    private final TemplateRenderService templateRenderService;
    private final JsonParserService jsonParserService;

    // Editor components
    private TextArea htmlEditor;
    private TextArea jsonEditor;
    
    // Preview components
    private IFrame previewFrame;
    private Checkbox jsToggle;
    
    // Action buttons
    private Button saveButton;
    private Button clearButton;
    
    // Locale selector
    private ComboBox<Locale> localeSelector;

    public MainView(TemplateRenderService templateRenderService,
                    JsonParserService jsonParserService) {
        this.templateRenderService = templateRenderService;
        this.jsonParserService = jsonParserService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        
        initializeComponents();
        buildLayout();
        loadStoredContent();
        loadStoredLocale();
    }

    private void initializeComponents() {
        createEditors();
        createPreviewFrame();
        createButtons();
        createSecurityToggle();
        createLocaleSelector();
    }

    private void createEditors() {
        // HTML Editor
        htmlEditor = new TextArea();
        htmlEditor.setLabel(getTranslation("ui.label.html.editor"));
        htmlEditor.setPlaceholder(getTranslation("ui.placeholder.html.editor"));
        htmlEditor.setSizeFull();
        htmlEditor.setValueChangeMode(ValueChangeMode.LAZY);
        htmlEditor.getStyle().set("font-family", "'Courier New', monospace");
        htmlEditor.getStyle().set("font-size", "14px");
        // Add value change listener for automatic preview update
        htmlEditor.addValueChangeListener(e -> updatePreview());

        // JSON Editor
        jsonEditor = new TextArea();
        jsonEditor.setLabel(getTranslation("ui.label.json.editor"));
        jsonEditor.setPlaceholder(getTranslation("ui.placeholder.json.editor"));
        jsonEditor.setSizeFull();
        jsonEditor.setValueChangeMode(ValueChangeMode.LAZY);
        jsonEditor.getStyle().set("font-family", "'Courier New', monospace");
        jsonEditor.getStyle().set("font-size", "14px");
        // Add value change listener for automatic preview update
        jsonEditor.addValueChangeListener(e -> updatePreview());
    }

    private void createPreviewFrame() {
        previewFrame = new IFrame();
        previewFrame.setSizeFull();
        previewFrame.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        previewFrame.getStyle().set("background", "white");

        // Default: Sandboxed (no JavaScript execution)
        previewFrame.getElement().setAttribute("sandbox", "allow-same-origin");

        // Set initial empty content
        previewFrame.getElement().setAttribute("srcdoc", getTranslation("ui.message.preview.default-message"));
    }

    private void createButtons() {
        saveButton = new Button(getTranslation("ui.button.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveToLocalStorage());
        
        clearButton = new Button(getTranslation("ui.button.clear"));
        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addClickListener(e -> clearAll());
    }

    private void createSecurityToggle() {
        jsToggle = new Checkbox(getTranslation("ui.toggle.javascript"));
        jsToggle.getStyle().set("color", "var(--lumo-error-text-color)");
        jsToggle.setValue(false);
        
        jsToggle.addValueChangeListener(e -> {
            if (e.getValue()) {
                // Allow scripts (user accepts risk)
                previewFrame.getElement().setAttribute("sandbox", "allow-same-origin allow-scripts");
            } else {
                // Disable scripts (safe mode)
                previewFrame.getElement().setAttribute("sandbox", "allow-same-origin");
            }
            updatePreview();
            
            // Save the toggle state
            saveToLocalStorageJs(getTranslation("ui.storage.key.js"), String.valueOf(e.getValue()));
        });
    }

    private void createLocaleSelector() {
        localeSelector = new ComboBox<>();
        localeSelector.setLabel(getTranslation("ui.locale.selector.label"));
        localeSelector.setItems(LOCALE_EN, LOCALE_SI);
        localeSelector.setItemLabelGenerator(locale -> {
            if (locale.equals(LOCALE_EN)) {
                return getTranslation("ui.locale.en");
            } else if (locale.equals(LOCALE_SI)) {
                return getTranslation("ui.locale.si");
            }
            return locale.getDisplayName(locale);
        });
        localeSelector.setValue(getLocale());
        localeSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                getUI().ifPresent(ui -> {
                    ui.setLocale(e.getValue());
                    // Save locale preference
                    saveToLocalStorageJs(getTranslation("ui.storage.key.locale"), 
                                       e.getValue().toLanguageTag());
                });
            }
        });
    }

    private void buildLayout() {
        // Left panel: Create nested vertical splits for 3 sections
        SplitLayout leftUpperMiddleSplit = new SplitLayout();
        leftUpperMiddleSplit.setOrientation(SplitLayout.Orientation.VERTICAL);
        leftUpperMiddleSplit.addToPrimary(htmlEditor);
        leftUpperMiddleSplit.addToSecondary(jsonEditor);
        leftUpperMiddleSplit.setSplitterPosition(55); // ~55% for HTML editor
        leftUpperMiddleSplit.setSizeFull();
        
        // Buttons and toggle layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);
        buttonLayout.setWidthFull();
        buttonLayout.add(saveButton, clearButton, jsToggle, localeSelector);
        buttonLayout.setFlexGrow(0, saveButton, clearButton, localeSelector);
        buttonLayout.setFlexGrow(1, jsToggle);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        
        // Container for middle section and buttons
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setSizeFull();
        leftPanel.setPadding(false);
        leftPanel.setSpacing(false);
        leftPanel.add(leftUpperMiddleSplit, buttonLayout);
        leftPanel.setFlexGrow(1, leftUpperMiddleSplit);
        leftPanel.setFlexGrow(0, buttonLayout);
        
        // Right panel: Preview frame
        Div rightPanel = new Div();
        rightPanel.setSizeFull();
        rightPanel.add(previewFrame);
        
        // Main vertical split: Left panel | Right panel
        SplitLayout mainSplit = new SplitLayout();
        mainSplit.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        mainSplit.addToPrimary(leftPanel);
        mainSplit.addToSecondary(rightPanel);
        mainSplit.setSplitterPosition(50); // 50/50 split
        mainSplit.setSizeFull();
        
        add(mainSplit);
    }

    private void updatePreview() {
        String htmlTemplate = htmlEditor.getValue();
        String jsonString = jsonEditor.getValue();
        
        // Handle empty template case
        if (htmlTemplate == null || htmlTemplate.trim().isEmpty()) {
            previewFrame.getElement().setAttribute("srcdoc", 
                getTranslation("ui.message.preview.default-message"));
            return;
        }
        
        try {
            // Parse JSON variables into context map
            Map<String, Object> context;
            if (jsonString == null || jsonString.trim().isEmpty()) {
                // Empty JSON - use empty context
                context = new HashMap<>();
            } else {
                // Parse JSON using the service
                context = jsonParserService.parseJsonToMap(jsonString);
            }
            
            // Render template with Velocity
            String renderedHtml = templateRenderService.render(htmlTemplate, context);
            
            // Update preview iframe with rendered content
            previewFrame.getElement().setAttribute("srcdoc", renderedHtml);
            
        } catch (IllegalArgumentException e) {
            // JSON parsing error
            showErrorNotification(getTranslation("ui.message.error.invalid-json.prefix") + e.getMessage());
            // Show error in preview as well
            previewFrame.getElement().setAttribute("srcdoc",
                "<html><body style='font-family: Arial, sans-serif; padding: 20px; color: #d32f2f;'>" +
                "<h3>" + getTranslation("ui.message.error.json-parse.title") + "</h3>" +
                "<p>" + escapeHtml(e.getMessage()) + "</p>" +
                "</body></html>");
                
        } catch (TemplateRenderException e) {
            // Template rendering error
            showErrorNotification(getTranslation("ui.message.error.template") + e.getMessage());
            // Show error in preview as well
            previewFrame.getElement().setAttribute("srcdoc",
                "<html><body style='font-family: Arial, sans-serif; padding: 20px; color: #d32f2f;'>" +
                "<h3>" + getTranslation("ui.message.error.template-render.title") + "</h3>" +
                "<p>" + escapeHtml(e.getMessage()) + "</p>" +
                "</body></html>");
        }
    }
    
    /**
     * Display error notification to the user
     */
    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
    /**
     * Escape HTML special characters to prevent XSS in error messages
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    private void saveToLocalStorage() {
        String htmlContent = htmlEditor.getValue();
        String jsonContent = jsonEditor.getValue();
        
        saveToLocalStorageJs(getTranslation("ui.storage.key.html"), htmlContent != null ? htmlContent : "");
        saveToLocalStorageJs(getTranslation("ui.storage.key.json"), jsonContent != null ? jsonContent : "");
        saveToLocalStorageJs(getTranslation("ui.storage.key.js"), String.valueOf(jsToggle.getValue()));
        
        // Show feedback
        saveButton.setText(getTranslation("ui.button.saved"));
        saveButton.setEnabled(false);
        
        // Reset button after a delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                getUI().ifPresent(ui -> ui.access(() -> {
                    saveButton.setText(getTranslation("ui.button.save"));
                    saveButton.setEnabled(true);
                }));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void clearAll() {
        htmlEditor.clear();
        jsonEditor.clear();
        jsToggle.setValue(false);
        
        removeFromLocalStorageJs(getTranslation("ui.storage.key.html"));
        removeFromLocalStorageJs(getTranslation("ui.storage.key.json"));
        removeFromLocalStorageJs(getTranslation("ui.storage.key.js"));
        
        updatePreview();
    }

    private void loadStoredContent() {
        // Load HTML content
        getFromLocalStorageJs(getTranslation("ui.storage.key.html"), value -> {
            if (value != null && !value.isEmpty()) {
                htmlEditor.setValue(value);
                updatePreview();
            }
        });
        
        // Load JSON content
        getFromLocalStorageJs(getTranslation("ui.storage.key.json"), value -> {
            if (value != null && !value.isEmpty()) {
                jsonEditor.setValue(value);
            }
        });
        
        // Load JavaScript toggle state
        getFromLocalStorageJs(getTranslation("ui.storage.key.js"), value -> {
            if (value != null && !value.isEmpty()) {
                jsToggle.setValue(Boolean.parseBoolean(value));
            }
        });
    }

    private void loadStoredLocale() {
        // Load stored locale preference from localStorage
        getFromLocalStorageJs(getTranslation("ui.storage.key.locale"), value -> {
            if (value != null && !value.isEmpty()) {
                try {
                    Locale storedLocale = Locale.forLanguageTag(value);
                    if (storedLocale.equals(LOCALE_EN) || storedLocale.equals(LOCALE_SI)) {
                        getUI().ifPresent(ui -> ui.setLocale(storedLocale));
                        localeSelector.setValue(storedLocale);
                    }
                } catch (Exception e) {
                    // Invalid locale tag, ignore and use default
                }
            }
        });
    }

    // Helper methods for localStorage access via JavaScript
    private void saveToLocalStorageJs(String key, String value) {
        UI.getCurrent().getPage().executeJs(
            "localStorage.setItem($0, $1)", key, value
        );
    }

    private void removeFromLocalStorageJs(String key) {
        UI.getCurrent().getPage().executeJs(
            "localStorage.removeItem($0)", key
        );
    }

    private void getFromLocalStorageJs(String key, ValueCallback callback) {
        UI.getCurrent().getPage().executeJs(
            "return localStorage.getItem($0)", key
        ).then(String.class, callback::onValue);
    }

    @FunctionalInterface
    private interface ValueCallback {
        void onValue(String value);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        // Update all UI component labels and texts when locale changes
        htmlEditor.setLabel(getTranslation("ui.label.html.editor"));
        htmlEditor.setPlaceholder(getTranslation("ui.placeholder.html.editor"));
        
        jsonEditor.setLabel(getTranslation("ui.label.json.editor"));
        jsonEditor.setPlaceholder(getTranslation("ui.placeholder.json.editor"));
        
        saveButton.setText(getTranslation("ui.button.save"));
        clearButton.setText(getTranslation("ui.button.clear"));
        
        jsToggle.setLabel(getTranslation("ui.toggle.javascript"));
        
        localeSelector.setLabel(getTranslation("ui.locale.selector.label"));
        // Refresh locale selector item labels
        localeSelector.setItemLabelGenerator(locale -> {
            if (locale.equals(LOCALE_EN)) {
                return getTranslation("ui.locale.en");
            } else if (locale.equals(LOCALE_SI)) {
                return getTranslation("ui.locale.si");
            }
            return locale.getDisplayName(locale);
        });
        
        // Update preview if editors are empty (showing default message)
        // or if there's content (to update error messages in new language)
        String htmlContent = htmlEditor.getValue();
        String jsonContent = jsonEditor.getValue();
        
        if ((htmlContent == null || htmlContent.trim().isEmpty()) && 
            (jsonContent == null || jsonContent.trim().isEmpty())) {
            // Editors are empty - update to show default message in new language
            previewFrame.getElement().setAttribute("srcdoc", 
                getTranslation("ui.message.preview.default-message"));
        } else {
            // Editors have content - re-render to update any error messages
            updatePreview();
        }
    }
}
