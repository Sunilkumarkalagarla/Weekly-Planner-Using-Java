import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class WeeklyPlanner extends JFrame {
    private final String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
    private final String[] hours = { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" };
    private final Color[] userColors = { Color.YELLOW, Color.GREEN, Color.RED, Color.RED };
    private final JComboBox<String> userRoleComboBox;
    private final Map<String, Map<String, Boolean>> userSelections = new HashMap<>();
    private Map<String, String> bookedSlots = new HashMap<>();
    private final Map<String, String> eventBookings = new HashMap<>();
    private String currentEventName = "";

    public WeeklyPlanner() {
        setTitle("Weekly Planner");
        setSize(1067, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize user selection map
        for (String userRole : new String[] { "User1", "User2", "User3", "Admin" }) {
            userSelections.put(userRole, new HashMap<>());
        }

        // User role dropdown
        userRoleComboBox = new JComboBox<>(new String[] { "User1", "User2", "User3", "Admin" });

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSelections());

        // Calendar grid
        // JPanel calendarGrid = createCalendarGrid();

        // Layout setup
        JPanel topPanel = createTopPanel();
        JPanel bottomPanel = createBottomPanel(); // Ensure this line is present in the constructor

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(createCalendarGrid()), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void modifyEvent() {
        String currentUser = (String) userRoleComboBox.getSelectedItem();
        Map<String, String> userEvents = new HashMap<>();

        // Filter events created by the current user
        eventBookings.forEach((slotId, eventInfo) -> {
            if (eventInfo.endsWith(" - " + currentUser)) {
                userEvents.put(slotId, eventInfo);
            }
        });

        if (userEvents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events available to modify.",
                    "Modify Event", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convert userEvents values to an array for the popup dialog
        String[] eventsArray = userEvents.values().toArray(new String[0]);
        String selectedEvent = (String) JOptionPane.showInputDialog(
                this,
                "Select the event to be modified:",
                "Modify Event",
                JOptionPane.QUESTION_MESSAGE,
                null,
                eventsArray,
                eventsArray[0]);

        if (selectedEvent != null && !selectedEvent.trim().isEmpty()) {
            String newEventName = JOptionPane.showInputDialog(this, "Rename the event:", selectedEvent.split(" - ")[0]);
            if (newEventName != null && !newEventName.trim().isEmpty()) {
                String newName = newEventName + " - " + currentUser;
                // Update all occurrences of the event
                eventBookings.replaceAll((k, v) -> v.equals(selectedEvent) ? newName : v);

                // Update UI to reflect the modified event names
                updateUI();
            }
        }
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userRoleComboBox, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton createEventButton = new JButton("Create Event");
        createEventButton.addActionListener(e -> promptForEventName());

        JButton modifyEventButton = new JButton("Modify Event");
        modifyEventButton.addActionListener(e -> modifyEvent());

        JButton deleteEventButton = new JButton("Delete Event");
        deleteEventButton.addActionListener(e -> deleteEvent());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSelections());

        bottomPanel.add(createEventButton);
        bottomPanel.add(modifyEventButton);
        bottomPanel.add(deleteEventButton);
        bottomPanel.add(saveButton);

        return bottomPanel;
    }

    private void promptForEventName() {
        // Prompt for the event name
        currentEventName = JOptionPane.showInputDialog(this, "Enter the name of the event:");
        if (currentEventName != null && !currentEventName.trim().isEmpty()) {
            // Enable selecting time slots for the event
            // Here you could also change the GUI to indicate event creation mode if needed
        } else {
            currentEventName = ""; // Reset event name if nothing was entered or canceled
        }
    }

    private JPanel createCalendarGrid() {
        JPanel calendarGrid = new JPanel(new GridLayout(hours.length + 1, days.length + 1, 5, 5));

        // Add an empty label for upper left corner
        calendarGrid.add(new JLabel(""));

        // Create headers for days
        for (String day : days) {
            calendarGrid.add(new JLabel(day, SwingConstants.CENTER));
        }

        // Create hour labels and time slots for each day
        for (String hour : hours) {
            calendarGrid.add(new JLabel(hour, SwingConstants.CENTER)); // Hour label on the left side
            for (String day : days) {
                JPanel timeSlot = createTimeSlot(hour + " " + day);
                calendarGrid.add(timeSlot);
            }
        }
        return calendarGrid;
    }

    private JPanel createTimeSlot(String slotId) {
        JPanel timeSlot = new JPanel(new BorderLayout());
        timeSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        timeSlot.setBackground(Color.WHITE);
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        timeSlot.add(label, BorderLayout.CENTER);

        // Mouse listener to change color on click and prevent overlapping schedules
        timeSlot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedUser = (String) userRoleComboBox.getSelectedItem();
                Map<String, Boolean> selections = userSelections.get(selectedUser);
                boolean isSelected = selections.getOrDefault(slotId, false);

                // Check if the slot is already booked
                if (!isSelected && bookedSlots.containsKey(slotId)) {
                    // Slot is already booked by another user, show popup
                    JOptionPane.showMessageDialog(WeeklyPlanner.this,
                            "This slot is already booked by " + bookedSlots.get(slotId),
                            "Slot Unavailable",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // Toggle selection
                    isSelected = !isSelected;
                    selections.put(slotId, isSelected);
                    if (isSelected) {
                        timeSlot.setBackground(userColors[userRoleComboBox.getSelectedIndex()]);
                        label.setText(currentEventName + " - " + selectedUser);
                        eventBookings.put(slotId, currentEventName + " - " + selectedUser);
                    } else {
                        timeSlot.setBackground(Color.WHITE);
                        label.setText("");
                        eventBookings.remove(slotId);
                    }
                }
            }
        });

        // Check if the slot has an associated event booking
        if (eventBookings.containsKey(slotId)) {
            String eventInfo = eventBookings.get(slotId);
            label.setText("<html>" + eventInfo + "</html>");
            String bookingUser = bookedSlots.get(slotId);
            for (int i = 0; i < userRoleComboBox.getItemCount(); i++) {
                if (userRoleComboBox.getItemAt(i).equals(bookingUser)) {
                    timeSlot.setBackground(userColors[i]);
                    break;
                }
            }
        }

        return timeSlot;
    }

    private void deleteEvent() {
        String currentUser = (String) userRoleComboBox.getSelectedItem();

        // Filter events that can be deleted based on the current user role
        Map<String, String> deletableEvents = new HashMap<>();
        if (currentUser.equals("Admin")) {
            // Admin can delete any event
            deletableEvents.putAll(eventBookings);
        } else {
            // Other users can only delete their own events
            eventBookings.forEach((slotId, eventInfo) -> {
                if (eventInfo.endsWith(" - " + currentUser)) {
                    deletableEvents.put(slotId, eventInfo);
                }
            });
        }

        // No events available to delete
        if (deletableEvents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No deletable events available.",
                    "Delete Event", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convert deletable event info to an array for the dialog
        String[] eventsArray = deletableEvents.values().toArray(new String[0]);
        String selectedEvent = (String) JOptionPane.showInputDialog(
                this,
                "Select the event to be deleted:",
                "Delete Event",
                JOptionPane.QUESTION_MESSAGE,
                null,
                eventsArray,
                eventsArray[0]);

        if (selectedEvent != null && !selectedEvent.trim().isEmpty()) {
            // Find all slots with the selected event and remove them
            eventBookings.entrySet().removeIf(entry -> entry.getValue().equals(selectedEvent));
            bookedSlots.entrySet().removeIf(entry -> {
                // Check if the slot contains the selected event and belongs to the current user
                return eventBookings.get(entry.getKey()) != null
                        && eventBookings.get(entry.getKey()).equals(selectedEvent);
            });

            // Update the UI to reflect the deleted event
            updateUI();
        }
    }

    private void updateUI() {
        // Refresh the calendar to reflect any changes like deleted events
        getContentPane().removeAll();
        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createCalendarGrid()), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void saveSelections() {
        String selectedUser = (String) userRoleComboBox.getSelectedItem();
        Map<String, Boolean> selections = userSelections.get(selectedUser);

        // Save the event for the booked slots and check for conflicts
        for (Map.Entry<String, Boolean> entry : selections.entrySet()) {
            String slotId = entry.getKey();
            Boolean isSelected = entry.getValue();

            if (isSelected && !bookedSlots.containsKey(slotId)) {
                // No conflict, book the slot and save the event
                bookedSlots.put(slotId, selectedUser);
                if (!currentEventName.isEmpty()) {
                    eventBookings.put(slotId, currentEventName + " - " + selectedUser);
                }
            } else if (!isSelected) {
                // Clear the selection
                bookedSlots.remove(slotId);
                eventBookings.remove(slotId);
            }
        }

        // Refresh the calendar to show event names
        getContentPane().removeAll();
        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createCalendarGrid()), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        validate();
        repaint();

        // Reset the current event name after saving
        currentEventName = "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeeklyPlanner planner = new WeeklyPlanner();
            planner.setVisible(true);
        });
    }
}
