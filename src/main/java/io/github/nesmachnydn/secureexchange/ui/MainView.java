package io.github.nesmachnydn.secureexchange.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;
import io.github.nesmachnydn.secureexchange.domain.TransferRecord;
import io.github.nesmachnydn.secureexchange.service.ExchangeService;

import java.io.IOException;

@Route("")
@PageTitle("Portable Secure Exchange Client")
public class MainView extends VerticalLayout {
    private final ExchangeService exchangeService;
    private final Grid<TransferRecord> grid = new Grid<>(TransferRecord.class, false);

    public MainView(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H1("Portable Secure Exchange Client"));
        add(new Paragraph("Local web UI. Files are exchanged through controlled IN/OUT folders; network transport is deliberately outside the application boundary."));

        UploadHandler handler = UploadHandler.inMemory((metadata, data) -> {
            try {
                exchangeService.stageOutbound(metadata.fileName(), data);
                getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("File staged in OUT with SHA-256 integrity metadata");
                    refreshGrid();
                }));
            } catch (Exception e) {
                getUI().ifPresent(ui -> ui.access(() -> Notification.show("Upload failed: " + e.getMessage())));
            }
        });
        Upload upload = new Upload(handler);
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);

        Button processInbound = new Button("Process IN folder", event -> {
            try {
                int count = exchangeService.processInbound();
                Notification.show("Processed inbound files: " + count);
                refreshGrid();
            } catch (IOException e) {
                Notification.show("IN processing failed: " + e.getMessage());
            }
        });
        Button refresh = new Button("Refresh", event -> refreshGrid());
        add(new HorizontalLayout(upload, processInbound, refresh));

        configureGrid();
        add(grid);
        expand(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(TransferRecord::createdAt).setHeader("Created").setAutoWidth(true);
        grid.addColumn(TransferRecord::direction).setHeader("Direction").setAutoWidth(true);
        grid.addColumn(TransferRecord::status).setHeader("Status").setAutoWidth(true);
        grid.addColumn(TransferRecord::originalFileName).setHeader("File").setFlexGrow(1);
        grid.addColumn(record -> record.sha256().substring(0, 16) + "…").setHeader("SHA-256").setAutoWidth(true);
        grid.addColumn(TransferRecord::reason).setHeader("Integrity result").setFlexGrow(1);
    }

    private void refreshGrid() {
        grid.setItems(exchangeService.listTransfers());
    }
}
