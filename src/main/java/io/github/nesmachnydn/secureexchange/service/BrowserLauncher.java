package io.github.nesmachnydn.secureexchange.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher {
    private final boolean autoOpen;

    public BrowserLauncher(@Value("${app.browser.auto-open:false}") boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openLocalUi() {
        if (!autoOpen || !Desktop.isDesktopSupported()) {
            return;
        }
        try {
            Desktop.getDesktop().browse(URI.create("http://127.0.0.1:8080"));
        } catch (Exception ignored) {
            // Browser launch is a convenience only; the local UI remains available on localhost.
        }
    }
}
