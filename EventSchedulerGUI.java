import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class EventSchedulerGUI {

    private static final Map<String, String> credentials = new HashMap<>();
    private static final Map<String, Map<String, Color>> userEvents = new HashMap<>(); // Map to store user events

    private static final String[] VALID_USERNAMES = { "Sunil", "Aishwarya", "Bhargavi", "Sahadev", "Client" };
    private static final String[] VALID_PASSWORDS = { "Sunil", "Aishwarya", "Bhargavi", "Sahadev", "Client" };

    private static JFrame eventFrame; // Declare eventFrame as a class variable

    public static void main(String[] args) {
        // Populate credentials map with valid username-password pairs
        for (int i = 0; i < VALID_USERNAMES.length; i++) {
            credentials.put(VALID_USERNAMES[i], VALID_PASSWORDS[i]);
            userEvents.put(VALID_USERNAMES[i], new HashMap<>()); // Initialize event map for each user
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            showLoginDialog();
        });
    }

    private static void showLoginDialog() {
        JFrame frame = new JFrame("Event Scheduler - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(frame, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showEventManagement(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                showLoginDialog(); // Show login dialog again if credentials are invalid
            }
        } else {
            System.exit(0); // Exit the application if login dialog is cancelled
        }
    }

    private static boolean authenticate(String username, String password) {
        return credentials.containsKey(username) && credentials.get(username).equals(password);
    }

    private static void showEventManagement(String username) {
        eventFrame = new JFrame("Event Scheduler - " + username);
        eventFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        eventFrame.setSize(800, 600);
        eventFrame.setLocationRelativeTo(null);

        BorderComponentsExample eventManagementPanel = new BorderComponentsExample(username);
        eventFrame.add(eventManagementPanel);

        eventFrame.setVisible(true);
    }

    private static class BorderComponentsExample extends JPanel {

        private JLabel usernameLabel;
        private static JPanel centerPanel;

        public BorderComponentsExample(String username) {
            setLayout(new BorderLayout());
            JPanel mainPanel = new JPanel(new BorderLayout());

            // North panel for logout and print buttons
            JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            // Print button (no functionality yet)
            JButton printButton = new JButton("Print");
            northPanel.add(printButton); // This button will just display and have no action listener for now.

            // Logout button
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            northPanel.add(logoutButton);

            // Other components setup
            centerPanel = createCenterPanel(username);
            JPanel southPanel = setupSouthPanel(username); // Ensure you have defined or adjusted this method correctly

            mainPanel.add(northPanel, BorderLayout.NORTH);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            add(mainPanel);
        }

        private JPanel setupSouthPanel(String username) {
            JPanel southPanel = new JPanel(new BorderLayout());

            JPanel southWestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel loggedInLabel = new JLabel("Logged in as: " + username);
            southWestPanel.add(loggedInLabel);

            JPanel southEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton createEventButton = new JButton("Create Event");
            createEventButton.addActionListener(e -> showCreateEventDialog(username));
            southEastPanel.add(createEventButton);

            JButton editEventButton = new JButton("Edit Event");
            editEventButton.addActionListener(e -> showEditEventDialog(username));
            southEastPanel.add(editEventButton);

            southPanel.add(southWestPanel, BorderLayout.WEST);
            southPanel.add(southEastPanel, BorderLayout.EAST);

            return southPanel;
        }

        private JPanel createCenterPanel(String username) {
            JPanel panel = new JPanel(new GridLayout(25, 8));

            // Add empty label as the top-left corner
            panel.add(new JLabel("Time/Day", JLabel.CENTER));

            // Add day labels (Sunday to Saturday)
            String[] dayNames = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
            for (String day : dayNames) {
                panel.add(new JLabel(day, JLabel.CENTER));
            }

            // Add hour labels and user events
            for (int hour = 0; hour < 24; hour++) {
                panel.add(new JLabel(hour + ":00", JLabel.CENTER)); // Add hour labels

                for (int day = 0; day < 7; day++) {
                    Color eventColor = Color.WHITE;
                    for (String user : VALID_USERNAMES) {
                        String key = dayNames[day] + "-" + hour;
                        Color color = userEvents.get(user).getOrDefault(key, Color.WHITE);
                        if (color != Color.WHITE) {
                            eventColor = color;
                            break;
                        }
                    }

                    JLabel eventLabel = new JLabel("", JLabel.CENTER);
                    eventLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    eventLabel.setOpaque(true);
                    eventLabel.setBackground(eventColor);
                    panel.add(eventLabel);
                }
            }

            return panel;
        }

        private static void showCreateEventDialog(String username) {

            JTextField eventNameField = new JTextField(20);

            JTextField descriptionField = new JTextField(20); // New description field

            JTextField locationField = new JTextField(20); // New location field

            JTextField startTimeField = new JTextField(5);

            JTextField endTimeField = new JTextField(5);

            JCheckBox[] dayCheckboxes = new JCheckBox[7]; // Checkboxes for days of the week

            JComboBox<String> colorComboBox = new JComboBox<>(new String[] { "Red", "Green", "Blue", "Yellow" });

            // Create checkboxes for days of the week

            String[] dayNames = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

            JPanel dayPanel = new JPanel(new GridLayout(1, 7));

            for (int i = 0; i < 7; i++) {

                dayCheckboxes[i] = new JCheckBox(dayNames[i]);

                dayPanel.add(dayCheckboxes[i]);

            }

            // Create checkboxes for participants

            JCheckBox[] participantCheckboxes = new JCheckBox[VALID_USERNAMES.length];

            JPanel participantPanel = new JPanel(new GridLayout(0, 1));

            for (int i = 0; i < VALID_USERNAMES.length; i++) {

                participantCheckboxes[i] = new JCheckBox(VALID_USERNAMES[i]);

                participantPanel.add(participantCheckboxes[i]);

            }

            JPanel createEventPanel = new JPanel(new GridBagLayout());

            GridBagConstraints labelConstraints = new GridBagConstraints();

            labelConstraints.anchor = GridBagConstraints.WEST;

            labelConstraints.insets = new Insets(5, 10, 5, 10);

            GridBagConstraints fieldConstraints = new GridBagConstraints();

            fieldConstraints.fill = GridBagConstraints.HORIZONTAL;

            fieldConstraints.weightx = 1.0;

            fieldConstraints.insets = new Insets(5, 10, 5, 10);

            fieldConstraints.gridwidth = GridBagConstraints.REMAINDER;

            // Add components to the createEventPanel with appropriate GridBagConstraints

            labelConstraints.gridx = 0;

            labelConstraints.gridy = 0;

            createEventPanel.add(new JLabel("Event Name:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(eventNameField, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Description:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(descriptionField, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Location:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(locationField, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Start Time (0-23):"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(startTimeField, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("End Time (0-23):"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(endTimeField, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Select Days:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(dayPanel, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Color:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(colorComboBox, fieldConstraints);

            labelConstraints.gridy++;

            createEventPanel.add(new JLabel("Select Participants:"), labelConstraints);

            fieldConstraints.gridx = 1;

            createEventPanel.add(participantPanel, fieldConstraints);

            int result = JOptionPane.showConfirmDialog(null, createEventPanel, "Create Event",

                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {

                try {

                    String eventName = eventNameField.getText();

                    String description = descriptionField.getText(); // Get description text

                    String location = locationField.getText(); // Get location text

                    int startTime = Integer.parseInt(startTimeField.getText());

                    int endTime = Integer.parseInt(endTimeField.getText());

                    Color color = getColorByName((String) colorComboBox.getSelectedItem());

                    // Store the event in the user's event map for selected days

                    Map<String, Color> userEventMap = userEvents.get(username);

                    for (int dayIndex = 0; dayIndex < 7; dayIndex++) {

                        if (dayCheckboxes[dayIndex].isSelected()) {

                            String selectedDay = dayNames[dayIndex];

                            for (int hour = startTime; hour <= endTime; hour++) {

                                String key = selectedDay + "-" + hour;

                                userEventMap.put(key, color);

                                // Optionally store description and location in a separate map or object

                                // userEventMap.put(key + "-description", description);

                                // userEventMap.put(key + "-location", location);

                            }

                        }

                    }

                    // Update the center panel to reflect the new event

                    updateCenterPanel(username);

                    // Add selected participants to the event map

                    for (JCheckBox checkbox : participantCheckboxes) {

                        if (checkbox.isSelected()) {

                            String participant = checkbox.getText();

                            Map<String, Color> participantEventMap = userEvents.get(participant);

                            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {

                                if (dayCheckboxes[dayIndex].isSelected()) {

                                    String selectedDay = dayNames[dayIndex];

                                    for (int hour = startTime; hour <= endTime; hour++) {

                                        String key = selectedDay + "-" + hour;

                                        participantEventMap.put(key, color);

                                    }

                                }

                            }

                        }

                    }

                } catch (NumberFormatException e) {

                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numeric values.", "Error",

                            JOptionPane.ERROR_MESSAGE);

                }

            }

        }

        private static Color getColorByName(String colorName) {

            switch (colorName) {

                case "Red":

                    return Color.RED;

                case "Green":

                    return Color.GREEN;

                case "Blue":

                    return Color.BLUE;

                case "Yellow":

                    return Color.YELLOW;

                default:

                    return Color.BLACK; // Default color (should not occur)

            }
        }

        private static void showEditEventDialog(String username) {
            Map<String, Color> userEventMap = userEvents.get(username);

            // Create a list of event names based on the user's events
            DefaultListModel<String> eventListModel = new DefaultListModel<>();
            for (String key : userEventMap.keySet()) {
                eventListModel.addElement(key); // Assuming 'key' represents event names
            }

            JList<String> eventList = new JList<>(eventListModel);

            // Create a scroll pane to contain the event list
            JScrollPane scrollPane = new JScrollPane(eventList);

            JPanel dialogPanel = new JPanel(new BorderLayout());
            dialogPanel.add(new JLabel("Select Event to Edit:"), BorderLayout.NORTH);
            dialogPanel.add(scrollPane, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(null, dialogPanel, "Edit Event",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String selectedEvent = eventList.getSelectedValue();
                if (selectedEvent != null) {
                    // Redirect to the create event dialog with pre-filled event details
                    String[] parts = selectedEvent.split("-"); // Parse event name to extract details
                    String day = parts[0];
                    int hour = Integer.parseInt(parts[1]);

                    // Retrieve existing event details
                    Color eventColor = userEventMap.get(selectedEvent); // Retrieve event color

                    // Populate fields in the create event dialog with existing event details
                    JTextField eventNameField = new JTextField(selectedEvent);
                    JTextField descriptionField = new JTextField("Description"); // Example description
                    JTextField locationField = new JTextField("Location"); // Example location
                    JTextField startTimeField = new JTextField(String.valueOf(hour));
                    JTextField endTimeField = new JTextField(String.valueOf(hour));

                    JPanel editEventPanel = new JPanel(new GridLayout(0, 1));
                    editEventPanel.add(new JLabel("Event Name:"));
                    editEventPanel.add(eventNameField);
                    editEventPanel.add(new JLabel("Description:"));
                    editEventPanel.add(descriptionField);
                    editEventPanel.add(new JLabel("Location:"));
                    editEventPanel.add(locationField);
                    editEventPanel.add(new JLabel("Start Time (0-23):"));
                    editEventPanel.add(startTimeField);
                    editEventPanel.add(new JLabel("End Time (0-23):"));
                    editEventPanel.add(endTimeField);

                    int result = JOptionPane.showConfirmDialog(null, editEventPanel, "Edit Event",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            String newEventName = eventNameField.getText();
                            String newDescription = descriptionField.getText();
                            String newLocation = locationField.getText();
                            int newStartTime = Integer.parseInt(startTimeField.getText());
                            int newEndTime = Integer.parseInt(endTimeField.getText());

                            // Update the event in the user's event map
                            for (int h = newStartTime; h <= newEndTime; h++) {
                                String newKey = day + "-" + h;
                                userEventMap.remove(selectedEvent); // Remove old event
                                userEventMap.put(newKey, eventColor); // Add updated event
                            }

                            // Update the center panel to reflect the changes
                            updateCenterPanel(username);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numeric values.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an event to edit.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private static void updateCenterPanel(String username) {
            // Retrieve user's events map
            Map<String, Color> userEventMap = userEvents.get(username);

            // Get the existing components from the center panel
            Component[] components = centerPanel.getComponents();

            // Update the color of existing labels based on the user's events
            String[] dayNames = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
            for (int hour = 0; hour < 24; hour++) {
                for (int day = 0; day < 7; day++) {
                    String key = dayNames[day] + "-" + hour;
                    int index = 1 + day + (hour + 1) * 8; // Calculate the index in the GridLayout

                    JLabel hourLabel = (JLabel) components[index];
                    Color eventColor = userEventMap.getOrDefault(key, Color.WHITE); // Retrieve event color or default
                                                                                    // to white
                    hourLabel.setBackground(eventColor);
                }
            }

            // Refresh the center panel to apply the color updates
            centerPanel.revalidate();
            centerPanel.repaint();
        }
    }

    private static void logout() {
        eventFrame.dispose(); // Dispose of the eventFrame to logout the user
        showLoginDialog(); // Show the login dialog again
    }
}