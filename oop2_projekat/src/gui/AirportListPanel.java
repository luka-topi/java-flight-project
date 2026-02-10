package gui;

import java.awt.*;
import model.Airport;

public class AirportListPanel extends Panel {

    private MainWindow mainWindow;
    private Panel listPanel;
    private ScrollPane scrollPane;

    public AirportListPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());

        // Create scrollable container for airport list
        scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        listPanel = new Panel(new GridLayout(0, 2, 5, 5)); // Two columns: airport info and checkbox
        scrollPane.add(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Header row
        listPanel.add(new TextField("Airport list:") {{
            setEditable(false);
            setBackground(Color.LIGHT_GRAY);
        }});
        listPanel.add(new Label(""));
    }

    public void refreshList() {
        listPanel.removeAll();

        // Recreate header after clearing
        listPanel.add(new TextField("Airport list:") {{
            setEditable(false);
            setBackground(Color.LIGHT_GRAY);
        }});
        listPanel.add(new Label(""));

        for (Airport a : mainWindow.airportList) {
            TextField tf = new TextField(a.getName() + " - " + a.getId() + " (" + a.getX() + ", " + a.getY() + ")");
            tf.setEditable(false);
            tf.setBackground(Color.WHITE);

            // Checkbox allows user to hide or show airport on map
            Checkbox hideCb = new Checkbox("Hide");
            hideCb.addItemListener(e -> {
                if (hideCb.getState()) {
                    mainWindow.selectedAirportsList.remove(a);
                    mainWindow.airportList.remove(a);
                } else {
                    if (!mainWindow.airportList.contains(a)) mainWindow.airportList.add(a);
                }
                // Update visible airports on map
                mainWindow.mapPanel.setAirports(mainWindow.airportList);
            });

            listPanel.add(tf);
            listPanel.add(hideCb);
        }

        // Refresh panel display
        listPanel.validate();
        listPanel.repaint();
    }
}
