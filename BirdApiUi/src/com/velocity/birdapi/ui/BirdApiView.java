package com.velocity.birdapi.ui;

import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.velocity.birdapi.client.BirdApiClient;
import com.velocity.itest.avian.dto.BirdDto;
import com.velocity.itest.avian.dto.SightingDto;

/**
 * An Eclipse ViewPart that provides a user interface for managing birds and sightings.
 * This class implements all the UI requirements specified in the Eclipse Feature section.
 */
public class BirdApiView extends ViewPart {

    public static final String ID = "com.velocity.birdapi.ui.BirdApiView";

    private BirdApiClient apiClient;
    private TableViewer birdTableViewer;
    private TableViewer sightingTableViewer;
    private Text sightingBirdIdText;
    private Text sightingLocationText;

    public BirdApiView() {
        // Initialize the API client. This handles all interaction with the REST API.
        this.apiClient = new BirdApiClient();
    }

    @Override
    public void createPartControl(Composite parent) {
        // Use a SashForm to allow the user to resize the forms and tables sections.
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Left Panel for forms (Add Bird, Add Sighting)
        Composite leftComposite = new Composite(sashForm, SWT.NONE);
        leftComposite.setLayout(new GridLayout(1, false));

        createBirdForm(leftComposite);
        createSightingForm(leftComposite);

        // Right Panel for tables (Show All Birds, Show All Sightings)
        Composite rightComposite = new Composite(sashForm, SWT.NONE);
        rightComposite.setLayout(new GridLayout(1, false));

        createBirdTable(rightComposite);
        createSightingTable(rightComposite);

        // Set initial weights for the sash form.
        sashForm.setWeights(new int[]{40, 60});

        // Add a listener to the bird table to show sightings when a bird is selected.
        birdTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!selection.isEmpty()) {
                    BirdDto selectedBird = (BirdDto) selection.getFirstElement();
                    refreshSightingsTable(selectedBird.getId());
                } else {
                    sightingTableViewer.setInput(null);
                }
            }
        });

        // Initial population of the bird table.
        refreshBirdTable();
    }

    /**
     * Creates the form for adding a new bird.
     */
    private void createBirdForm(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Add New Bird");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        // Name
        new Label(group, SWT.NONE).setText("Name:");
        Text nameText = new Text(group, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Color
        new Label(group, SWT.NONE).setText("Color:");
        Text colorText = new Text(group, SWT.BORDER);
        colorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Weight
        new Label(group, SWT.NONE).setText("Weight:");
        Text weightText = new Text(group, SWT.BORDER);
        weightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Height
        new Label(group, SWT.NONE).setText("Height:");
        Text heightText = new Text(group, SWT.BORDER);
        heightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button addButton = new Button(group, SWT.PUSH);
        addButton.setText("Add Bird");
        GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        addButton.setLayoutData(gd);

        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new Thread(() -> {
                    try {
                        BirdDto newBird = new BirdDto();
                        newBird.setName(nameText.getText());
                        newBird.setColor(colorText.getText());
                        newBird.setWeight(Double.parseDouble(weightText.getText()));
                        newBird.setHeight(Double.parseDouble(heightText.getText()));
                        apiClient.addBird(newBird);
                        
                        // Update the UI on the UI thread
                        getSite().getShell().getDisplay().asyncExec(() -> {
                            refreshBirdTable();
                            nameText.setText("");
                            colorText.setText("");
                            weightText.setText("");
                            heightText.setText("");
                        });
                    } catch (Exception ex) {
                        System.err.println("Error adding bird: " + ex.getMessage());
                    }
                }).start();
            }
        });
    }

    /**
     * Creates the form for adding a new sighting.
     */
    private void createSightingForm(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Add New Sighting");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        // Bird ID
        new Label(group, SWT.NONE).setText("Bird ID:");
        sightingBirdIdText = new Text(group, SWT.BORDER);
        sightingBirdIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Location
        new Label(group, SWT.NONE).setText("Location:");
        sightingLocationText = new Text(group, SWT.BORDER);
        sightingLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Button addButton = new Button(group, SWT.PUSH);
        addButton.setText("Add Sighting");
        GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        addButton.setLayoutData(gd);

        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new Thread(() -> {
                    try {
                        SightingDto newSighting = new SightingDto();
                        BirdDto bird = new BirdDto();
                        bird.setId(Long.parseLong(sightingBirdIdText.getText()));
                        newSighting.setBird(bird);
                        newSighting.setLocation(sightingLocationText.getText());
                        newSighting.setDateTime(LocalDateTime.now());
                        
                        apiClient.addSighting(newSighting);

                        getSite().getShell().getDisplay().asyncExec(() -> {
                            refreshSightingsTable(Long.parseLong(sightingBirdIdText.getText()));
                            sightingBirdIdText.setText("");
                            sightingLocationText.setText("");
                        });
                    } catch (Exception ex) {
                        System.err.println("Error adding sighting: " + ex.getMessage());
                    }
                }).start();
            }
        });
    }

    /**
     * Creates the table to display all birds.
     */
    private void createBirdTable(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("All Birds (Select to see sightings)");
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        birdTableViewer = new TableViewer(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Table table = birdTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        String[] titles = {"ID", "Name", "Color", "Weight", "Height"};
        int[] bounds = {50, 150, 100, 80, 80};

        for (int i = 0; i < titles.length; i++) {
            TableViewerColumn viewerColumn = new TableViewerColumn(birdTableViewer, SWT.NONE);
            TableColumn column = viewerColumn.getColumn();
            column.setText(titles[i]);
            column.setWidth(bounds[i]);
            column.setResizable(true);
            column.setMoveable(true);
        }
        
        birdTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        birdTableViewer.setLabelProvider(new BirdLabelProvider());
    }

    /**
     * Creates the table to display sightings for a selected bird.
     */
    private void createSightingTable(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Sightings for Selected Bird");
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sightingTableViewer = new TableViewer(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Table table = sightingTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        String[] titles = {"ID", "Bird Name", "Location", "Date-Time"};
        int[] bounds = {50, 150, 150, 150};
        
        for (int i = 0; i < titles.length; i++) {
            TableViewerColumn viewerColumn = new TableViewerColumn(sightingTableViewer, SWT.NONE);
            TableColumn column = viewerColumn.getColumn();
            column.setText(titles[i]);
            column.setWidth(bounds[i]);
            column.setResizable(true);
            column.setMoveable(true);
        }
        
        sightingTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        sightingTableViewer.setLabelProvider(new SightingLabelProvider());
    }

    /**
     * Fetches all birds from the API and refreshes the bird table on the UI thread.
     */
    private void refreshBirdTable() {
        new Thread(() -> {
            try {
                final List<BirdDto> birds = apiClient.getAllBirds();
                getSite().getShell().getDisplay().asyncExec(() -> {
                    birdTableViewer.setInput(birds);
                });
            } catch (Exception e) {
                System.err.println("Error refreshing bird table: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Fetches sightings for a specific bird from the API and refreshes the sighting table.
     */
    private void refreshSightingsTable(Long birdId) {
        new Thread(() -> {
            try {
                final List<SightingDto> sightings = apiClient.querySightings(null, birdId, null, null);
                getSite().getShell().getDisplay().asyncExec(() -> {
                    sightingTableViewer.setInput(sightings);
                });
            } catch (Exception e) {
                System.err.println("Error refreshing sightings table: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void setFocus() {
        if (birdTableViewer != null) {
            birdTableViewer.getControl().setFocus();
        }
    }
}