package org.example;

import javax.swing.*;   //Java Swing Library
import java.awt.*;      // Abstract Window Toolkit - swing is built on top of awt
import java.awt.event.ActionEvent;  //to work in response to a button
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;  //if there's an error in accessing a particular textfield because the field does not exist
import javax.swing.text.PlainDocument;   //to handle the contact number field
import javax.swing.text.AttributeSet;  //to handle attributes
import java.net.URI;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.sql.*;

//Geolocation API imports

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ReliefAidApp2 {

    public static void main(String[] args) {
        // To create the main frame (window) with a mobile aspect ratio (9:16)
        JFrame frame = new JFrame("Sahay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640); // 9:16 aspect ratio (360 x 640)
        frame.setResizable(false); //prevents the window from being resized

        // Create a panel for holding components
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Load an image for the top half
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\chhaj\\OneDrive\\Desktop\\gaurav\\college\\Minor Project\\homeimg.jpg");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(imageLabel, BorderLayout.NORTH);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Create rounded "Request for Aid" button
        JButton requestAidButton = createRoundedButton("Request for Aid");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(20, 20, 10, 20); // Add padding between buttons
        buttonPanel.add(requestAidButton, constraints);

        // Create rounded "Login" button
        JButton loginButton = createRoundedButton("Login");
        constraints.gridy = 1; //to place it below the request button
        buttonPanel.add(loginButton, constraints);

        // Create a panel for the gradient effect
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Create a gradient from the image's color to white
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 255, 255, 0), 0, 100, Color.WHITE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(gradientPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        requestAidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Request for Aid form
                openRequestForm();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login form
                openLoginForm();
            }
        });

        // Add the panel to the frame
        frame.add(panel);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible - By default, the frame is not visible
        frame.setVisible(true);
    }

    //Custom method to create a rounded button using arcWidth and arcHeight for the curves
    private static JButton createRoundedButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Create rounded corners
                super.paintComponent(g);
            }
        };

        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Makes the background transparent
        button.setBorderPainted(false); // Removes the border otherwise it produces a weird border since the corners are rounded
        button.setOpaque(false); // Makes the button background transparent
        button.setForeground(Color.WHITE); // Button text color
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Set button font
        button.setBackground(new Color(245, 71, 74)); // Button background color
        button.setPreferredSize(new Dimension(300, 50)); // Set button size

        return button;
    }

    //Geolocation API
    // Method to fetch location using an IP-based geolocation API
    private static String fetchLocation() {
        final String GEOLOCATION_API_URL = "https://ipinfo.io/json?token=e4caf047eb768a";
        try {
            URL url = new URL(GEOLOCATION_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON to extract location
            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject.getString("loc"); // e.g., "37.7749,-122.4194"
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to open the Request for Aid form
    private static void openRequestForm() {
        JFrame formFrame = new JFrame("Request for Aid");
        formFrame.setSize(360, 640);
        formFrame.setResizable(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(formPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formFrame.add(scrollPane, BorderLayout.CENTER);

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        contactField.setDocument(new NumericDocument());

        int permission = JOptionPane.showConfirmDialog(formFrame, "Do you allow access to your location?", "Location Permission", JOptionPane.YES_NO_OPTION);
        if (permission == JOptionPane.YES_OPTION) {
            String location = fetchLocation();
            if (location != null) {
                locationField.setText(location);
            } else {
                JOptionPane.showMessageDialog(formFrame, "Could not retrieve location.");
            }
        }

        JCheckBox[] itemCheckboxes = new JCheckBox[5];
        QuantityControl[] quantityControls = new QuantityControl[5];
        String[] items = {"Food Pack", "Water Bottle", "First Aid Kit", "Blanket", "Clothes"};

        for (int i = 0; i < items.length; i++) {
            itemCheckboxes[i] = new JCheckBox(items[i]);
            quantityControls[i] = new QuantityControl(itemCheckboxes[i]);
            formPanel.add(itemCheckboxes[i]);
            formPanel.add(quantityControls[i]);
        }

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactField);

        JButton submitButton = createRoundedButton("Submit Request");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String location = locationField.getText();
                    String contactNumber = contactField.getText();

                    if (name.isEmpty() || location.isEmpty() || contactNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(formFrame, "Please fill all fields.");
                        return;
                    }

                    if (!location.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) {
                        JOptionPane.showMessageDialog(formFrame, "Location must be in the format: latitude,longitude.");
                        return;
                    }

                    String[] nameParts = name.split(" ", 2);
                    String firstName = nameParts[0];
                    String lastName = (nameParts.length > 1) ? nameParts[1] : "";
                    String username = (firstName + "_" + lastName).toLowerCase();

                    // Generate a basic password (for now, using username + fixed suffix)
                    String password = username + "_123"; // Example logic. Replace with secure generation.

                    int[] quantities = new int[5];
                    for (int i = 0; i < quantities.length; i++) {
                        quantities[i] = quantityControls[i].getQuantity();
                    }

                    String url = "jdbc:postgresql://localhost:5432/CRLRMS";
                    String user = "postgres";
                    String dbPassword = "12345678";

                    try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
                        insertUserDetails(connection, firstName, lastName, username, password, contactNumber, location);
                        for (int i = 0; i < items.length; i++) {
                            if (quantities[i] > 0) {
                                insertRequest(connection, location, items[i], quantities[i]);
                            }
                        }
                        JOptionPane.showMessageDialog(formFrame, "Request submitted successfully!");
                        formFrame.dispose();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(formFrame, "Error: " + ex.getMessage());
                }
            }
        });

        formPanel.add(submitButton);
        formFrame.setLocationRelativeTo(null);
        formFrame.setVisible(true);
    }

    private static void insertUserDetails(Connection connection, String firstName, String lastName, String username, String password, String contactNumber, String locationId) throws SQLException {
        String userSql = "INSERT INTO users (first_name, last_name, username, password, contact_number, location_id, role, is_busy) VALUES (?, ?, ?, ?, ?, ?, 2, false)";
        try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
            userStmt.setString(1, firstName);
            userStmt.setString(2, lastName);
            userStmt.setString(3, username);
            userStmt.setString(4, password);
            userStmt.setString(5, contactNumber);
            userStmt.setString(6, locationId);
            userStmt.executeUpdate();
        }
    }

    private static void insertRequest(Connection connection, String locationId, String resourceName, int quantity) throws SQLException {
        // Map resourceName to resource_id
        int resourceId = getResourceId(resourceName);

        String requestSql = "INSERT INTO requests (requesting_location_id, resource_id, requested_quantity, request_date, status) VALUES (?, ?, ?, CURRENT_DATE, 'Pending')";
        try (PreparedStatement requestStmt = connection.prepareStatement(requestSql)) {
            requestStmt.setString(1, locationId);  // Location ID as String
            requestStmt.setInt(2, resourceId);     // Resource ID as Integer
            requestStmt.setInt(3, quantity);       // Quantity as Integer
            requestStmt.executeUpdate();
        }
    }

    // Maps resource names to their corresponding resource IDs
    private static int getResourceId(String resourceName) {
        switch (resourceName.toLowerCase()) {
            case "food pack": return 1;
            case "water bottle": return 2;
            case "first aid kit": return 3;
            case "blanket": return 4;
            case "clothes": return 5;
            default: throw new IllegalArgumentException("Invalid resource name: " + resourceName);
        }
    }

    private static void openDriverDashboard(String driverName, int warehouseId) {
//      Create the dashboard frame
        JFrame dashboardFrame = new JFrame("Driver Dashboard");
        dashboardFrame.setSize(360, 640); // 9:16 aspect ratio
        dashboardFrame.setResizable(false);
        dashboardFrame.setLayout(null); // Absolute positioning, needed for sliding menu

//      Create a main panel for the dashboard
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBounds(0, 0, 360, 640); // Full screen panel

//      Add a hamburger menu button
        JButton hamburgerButton = new JButton("☰");
        hamburgerButton.setFont(new Font("Arial", Font.BOLD, 24));
        hamburgerButton.setBackground(new Color(245, 71, 74)); // Button background color
        hamburgerButton.setForeground(Color.WHITE);
        hamburgerButton.setFocusPainted(false);
        hamburgerButton.setBorderPainted(false);
        hamburgerButton.setOpaque(true);
        hamburgerButton.setBounds(10, 10, 40, 40); // Position the hamburger button

//      Create the sliding hamburger menu panel
        JPanel sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setBackground(new Color(245, 71, 74)); // Red background for menu
        sideMenuPanel.setBounds(-180, 0, 180, dashboardFrame.getHeight()); // Initially offscreen

//      Menu options
        JMenuItem emergencyItem = new JMenuItem("Emergency Service");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem requestsItem = new JMenuItem("Requests");

//      Style menu items
        styleMenuItem(emergencyItem);
        styleMenuItem(logoutItem);
        styleMenuItem(requestsItem);

//      Add action listeners to the menu items
        emergencyItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URL("tel:112").toURI()); // Call 112 for emergency
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to make emergency call.");
            }
        });

        logoutItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                dashboardFrame.dispose(); // Close the current frame
                openLoginForm(); // Open the login form
            }
        });

        requestsItem.addActionListener(e -> {
            // This can just refocus the screen on the current page
            // No need to navigate anywhere since the requests are already on the page.
            // Close the hamburger menu when Requests is clicked
            sideMenuPanel.setBounds(-180, 0, 180, dashboardFrame.getHeight()); // Slide out
        });

//      Add the menu items to the side panel
        sideMenuPanel.add(emergencyItem);
        sideMenuPanel.add(logoutItem);
        sideMenuPanel.add(requestsItem);

//      Add a close button to the hamburger menu
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(245, 71, 74)); // Red color for close button
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setOpaque(true);
        closeButton.setBounds(140, 10, 40, 40); // Position the close button in the menu

        closeButton.addActionListener(e -> {
            // Close the menu when the close button is clicked
            sideMenuPanel.setBounds(-180, 0, 180, dashboardFrame.getHeight()); // Slide out
        });

//      Add the close button to the side menu
        sideMenuPanel.add(closeButton);

//      Set the layout of the main panel to null for absolute positioning
        mainPanel.setLayout(null);

//      Add the hamburger button to the top left corner of the panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBounds(0, 0, 360, 50); // Set panel height for top area
        topPanel.setBackground(new Color(245, 71, 74)); // Top panel background color
        topPanel.add(hamburgerButton, BorderLayout.WEST); // Add hamburger button to the top left

//      Show the menu when the hamburger button is clicked
        hamburgerButton.addActionListener(e -> {
            // Slide the menu in or out
            if (sideMenuPanel.getBounds().x == -180) {
                sideMenuPanel.setBounds(0, 0, 180, dashboardFrame.getHeight()); // Slide in
            } else {
                sideMenuPanel.setBounds(-180, 0, 180, dashboardFrame.getHeight()); // Slide out
            }
        });

//      Add welcome label at the top
        JLabel welcomeLabel = new JLabel("Welcome, " + driverName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setForeground(new Color(255, 255, 255)); // Set text color to match button color
        welcomeLabel.setBounds(0, 8, 360, 40); // Position the welcome label

//      Add the top panel with the hamburger menu
        mainPanel.add(topPanel);
        mainPanel.add(welcomeLabel);

//      Ensure the welcome label is in front of the top panel
        mainPanel.setComponentZOrder(welcomeLabel, 0); // Move the welcome label to the front
        mainPanel.setComponentZOrder(topPanel, 1); // Move the top panel to the background

//      Add the side menu and main panel to the dashboard frame
        dashboardFrame.add(sideMenuPanel);
        dashboardFrame.add(mainPanel);

//      Display the dashboard frame
        dashboardFrame.setVisible(true);


        // Create a panel for the requests
        JPanel requestsPanel = new JPanel();
        requestsPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Dynamic rows for requests
        requestsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        requestsPanel.setBounds(0, 90, 360, 550);

        Comparator<Request> requestComparator = (r1, r2) -> {
            // Prioritize first aid kits (ResourceId == 3)
            if (r1.getResourceId() == 3 && r2.getResourceId() != 3) return -1;  // r1 is first aid kit, r2 is not
            if (r1.getResourceId() != 3 && r2.getResourceId() == 3) return 1;   // r1 is not first aid kit, r2 is

            // If both requests are first aid kits or both are not, prioritize by quantity (descending order)
            return 0; // Descending order based on quantity
        };



        // Fetch data from the database and populate the requestsPanel
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CRLRMS", "postgres", "12345678");
//            String query = "SELECT request_id, resource_id, requested_quantity FROM requests WHERE status = 'Pending' ORDER BY CASE WHEN resource_id = 3 THEN 0 ELSE 1 END, resource_id ASC";
            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(query);
//
//            while (resultSet.next()) {
//                int requestId = resultSet.getInt("request_id");
//                int resourceId = resultSet.getInt("resource_id");
//                int quantity = resultSet.getInt("requested_quantity");
//
//                JPanel requestCard = createRequestCard(requestId, resourceId, quantity);
//                requestsPanel.add(requestCard);
//            }

            String query = "SELECT request_id, resource_id, requested_quantity FROM requests WHERE status = 'Pending'";
            PriorityQueue<Request> priorityQueue = new PriorityQueue<>(requestComparator);

            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Fetching requests...");

            while (resultSet.next()) {
                int requestId = resultSet.getInt("request_id");
                int resourceId = resultSet.getInt("resource_id");
                int quantity = resultSet.getInt("requested_quantity");
                System.out.println("Adding request: ID=" + requestId + ", ResourceID=" + resourceId + ", Quantity=" + quantity);

                priorityQueue.add(new Request(requestId, resourceId, quantity));
            }


            while (!priorityQueue.isEmpty()) {
                Request request = priorityQueue.poll();
                JPanel requestCard = createRequestCard(request.getRequestId(), request.getResourceId(), request.getQuantity());
                System.out.println("Request card created for request ID: " + request.getRequestId());
                requestsPanel.add(requestCard);
            }

            requestsPanel.revalidate();
            requestsPanel.repaint();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dashboardFrame, "Failed to fetch requests from the database.");
        }

        // Add requests panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(requestsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(0, 90, 360, 550);
        mainPanel.add(scrollPane);

        // Maps Button
        JButton mapsButton = new JButton("Maps");
        mapsButton.setFont(new Font("Arial", Font.BOLD, 14));
        //mapsButton.setBackground(new Color(100, 149, 237)); // Blue
        mapsButton.setBounds(10, 580, 110, 40);
        mapsButton.addActionListener(e -> {
            // Simulate map functionality (you can integrate actual maps API here)
            JOptionPane.showMessageDialog(dashboardFrame, "Opening Maps for Navigation...");
        });

        // Call Button
        JButton callButton = new JButton("Call");
        callButton.setFont(new Font("Arial", Font.BOLD, 14));
        //callButton.setBackground(new Color(34, 139, 34)); // Green
        callButton.setBounds(120, 580, 110, 40);
        callButton.addActionListener(e -> {
            // Simulate call functionality (you can integrate actual call API here)
            JOptionPane.showMessageDialog(dashboardFrame, "Calling the Requester...");
        });

        // Done Button
        JButton doneButton = new JButton("Done");
        doneButton.setFont(new Font("Arial", Font.BOLD, 14));
        //doneButton.setBackground(new Color(255, 69, 0)); // Red
        doneButton.setBounds(230, 580, 110, 40);
        doneButton.addActionListener(e -> {
            // Simulate marking the request as done (you can implement the actual logic here)
            JOptionPane.showMessageDialog(dashboardFrame, "Request marked as Done!");
        });

        // Add the buttons to the main panel
        mainPanel.add(mapsButton);
        mainPanel.add(callButton);
        mainPanel.add(doneButton);

        // Add the sliding menu to the dashboard
        //dashboardFrame.add(slidingMenu);
        mainPanel.add(hamburgerButton);
        mainPanel.add(welcomeLabel);
        dashboardFrame.add(mainPanel);

        // Center the dashboard frame on the screen
        dashboardFrame.setLocationRelativeTo(null);

        // Make the dashboard frame visible
        dashboardFrame.setVisible(true);
    }

    private static JPanel createRequestCard(int requestId, int resourceId, int quantity, double distance) {
        // Create a panel for the request card
        JPanel requestCard = new JPanel();
        requestCard.setLayout(new BorderLayout());
        requestCard.setBackground(new Color(245, 245, 245)); // Light grey background
        requestCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Create and set components for request card details
        JLabel requestIdLabel = new JLabel("Request ID: " + requestId);
        JLabel resourceIdLabel = new JLabel("Resource ID: " + resourceId);
        JLabel quantityLabel = new JLabel("Quantity: " + quantity);
        JLabel distanceLabel = new JLabel("Distance: " + String.format("%.2f km", distance));

        requestCard.add(requestIdLabel, BorderLayout.NORTH);
        requestCard.add(resourceIdLabel, BorderLayout.CENTER);
        requestCard.add(quantityLabel, BorderLayout.SOUTH);
        requestCard.add(distanceLabel, BorderLayout.EAST);

        return requestCard;
    }

    // Helper method to create a request card
    private static JPanel createRequestCard(int requestId, int resourceId, int quantity) {
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setBackground(Color.WHITE);

        // Display request details
        JLabel detailsLabel = new JLabel(
                "<html><b>Request ID:</b> " + requestId + "<br>" +
                        "<b>Resource ID:</b> " + resourceId + "<br>" +
                        "<b>Quantity:</b> " + quantity + "</html>");
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        card.add(detailsLabel);

        // Add action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JButton mapsButton = createSmallRoundedButton("MAPS");
        JButton callButton = createSmallRoundedButton("CALL");
        JButton doneButton = createSmallRoundedButton("DONE");

        mapsButton.addActionListener(e -> openMapsForRequest(requestId));
        callButton.addActionListener(e -> makeCallForRequest(requestId));
        doneButton.addActionListener(e -> markRequestAsDone(requestId));

        buttonPanel.add(mapsButton);
        buttonPanel.add(callButton);
        buttonPanel.add(doneButton);

        card.add(buttonPanel);
        return card;
    }

    // Helper method to calculate distance using the Haversine formula
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    // Request class to store details
    static class Request {
        private int requestId;
        private int resourceId;
        private int quantity;
        private double distance;

        public Request(int requestId, int resourceId, int quantity) {
            this.requestId = requestId;
            this.resourceId = resourceId;
            this.quantity = quantity;
            this.distance = distance;
        }

        public int getRequestId() { return requestId; }
        public int getResourceId() { return resourceId; }
        public int getQuantity() { return quantity; }
        public double getDistance() { return distance; }
    }

    // Methods for button actions
    private static void openMapsForRequest(int requestId) {
        try {
            // Step 1: Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CRLRMS", "postgres", "12345678");

            // Step 2: Fetch source coordinates (latitude, longitude) for the request
            String requestQuery = "SELECT requesting_location_id FROM requests WHERE request_id = ?";
            PreparedStatement requestStmt = connection.prepareStatement(requestQuery);
            requestStmt.setInt(1, requestId);
            ResultSet requestResult = requestStmt.executeQuery();

            if (!requestResult.next()) {
                JOptionPane.showMessageDialog(null, "Request not found.");
                connection.close();
                return;
            }

            String sourceCoordinates = requestResult.getString("requesting_location_id"); // Format: "latitude,longitude"
            String[] sourceCoordsArray = sourceCoordinates.split(",");
            String sourceLatitude = sourceCoordsArray[0].trim();
            String sourceLongitude = sourceCoordsArray[1].trim();

            // Step 3: Fetch destination coordinates (latitude, longitude) from warehouses table
            String warehouseQuery = "SELECT latitude, longitude FROM warehouses LIMIT 1"; // Modify this query if multiple warehouses exist
            Statement warehouseStmt = connection.createStatement();
            ResultSet warehouseResult = warehouseStmt.executeQuery(warehouseQuery);

            if (!warehouseResult.next()) {
                JOptionPane.showMessageDialog(null, "Warehouse data not found.");
                connection.close();
                return;
            }

            String destinationLatitude = warehouseResult.getString("latitude");
            String destinationLongitude = warehouseResult.getString("longitude");

            // Step 4: Construct Google Maps URL
            String mapsUrl = "https://www.google.com/maps/dir/?api=1&origin=" + sourceLatitude + "," + sourceLongitude +
                    "&destination=" + destinationLatitude + "," + destinationLongitude;

            // Step 5: Open the URL in the default browser
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(mapsUrl));

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to open Google Maps. Please check your internet connection or database setup.");
        }
    }


    private static void makeCallForRequest(int requestId) {
        try {
            // Step 1: Establish a database connection
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CRLRMS", "postgres", "12345678");

            // Step 2: Get requesting_location_id from the requests table
            String requestQuery = "SELECT requesting_location_id FROM requests WHERE request_id = ?";
            PreparedStatement requestStmt = connection.prepareStatement(requestQuery);
            requestStmt.setInt(1, requestId);
            ResultSet requestResult = requestStmt.executeQuery();

            if (!requestResult.next()) {
                JOptionPane.showMessageDialog(null, "Request not found.");
                connection.close();
                return;
            }

            String requestingLocationId = requestResult.getString("requesting_location_id");

            // Step 3: Get the contact_number from the users table where location_id matches
            String userQuery = "SELECT contact_number FROM users WHERE location_id = ?";
            PreparedStatement userStmt = connection.prepareStatement(userQuery);
            userStmt.setString(1, requestingLocationId);
            ResultSet userResult = userStmt.executeQuery();

            if (!userResult.next()) {
                JOptionPane.showMessageDialog(null, "No user found for the specified location.");
                connection.close();
                return;
            }

            String contactNumber = userResult.getString("contact_number");

            // Step 4: Display the contact number in a dialog box
            JOptionPane.showMessageDialog(null, "Contact Number: " + contactNumber, "Call User", JOptionPane.INFORMATION_MESSAGE);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch contact number. Please check your database and try again.");
        }
    }


    private static void markRequestAsDone(int requestId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CRLRMS", "postgres", "12345678");
            String updateQuery = "UPDATE requests SET status = 'Done' WHERE request_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, requestId);
            int rowsUpdated = preparedStatement.executeUpdate();
            connection.close();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Request marked as done successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to mark request as done.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update the request status.");
        }
    }


    // Method to style the menu items
    private static void styleMenuItem(JMenuItem menuItem) {
        menuItem.setFont(new Font("Arial", Font.BOLD, 18));
        menuItem.setForeground(Color.WHITE);
        menuItem.setBackground(new Color(245, 71, 74)); // Red background
        menuItem.setFocusPainted(false);
        menuItem.setOpaque(true);

        // Change background on hover
        menuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuItem.setBackground(Color.DARK_GRAY); // Dark gray on hover
                menuItem.setForeground(Color.WHITE); // White text on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuItem.setBackground(new Color(245, 71, 74)); // Reset to red
                menuItem.setForeground(Color.WHITE); // Reset to white text
            }
        });
    }

    // Method to create a single request card
    private static JPanel createRequestCard(String customerName, String orderId, String time, String item1, String qty1, String item2, String qty2, int total, String location, String phoneNumber) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cardPanel.setBackground(new Color(255, 250, 200)); // Light yellow background

        // Top section with customer name, order ID, and time
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        topPanel.add(new JLabel(customerName));
        topPanel.add(new JLabel(orderId, JLabel.CENTER));
        topPanel.add(new JLabel(time, JLabel.RIGHT));
        cardPanel.add(topPanel, BorderLayout.NORTH);

        // Center section with item details
        JPanel centerPanel = new JPanel(new GridLayout(2, 2));
        centerPanel.add(new JLabel(item1 + ": " + qty1));
        centerPanel.add(new JLabel("Rs. 50")); // Example price
        centerPanel.add(new JLabel(item2 + ": " + qty2));
        centerPanel.add(new JLabel("Rs. 300")); // Example price
        cardPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom section with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create rounded buttons with custom color using the createSmallRoundedButton method
        JButton mapsButton = createSmallRoundedButton("MAPS");
        JButton callButton = createSmallRoundedButton("CALL");
        JButton doneButton = createSmallRoundedButton("DONE");

        // Add action listeners for buttons
        mapsButton.addActionListener(e -> openGoogleMaps(location));
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Calling " + phoneNumber));
        doneButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Order marked as done!"));

        bottomPanel.add(mapsButton);
        bottomPanel.add(callButton);
        bottomPanel.add(doneButton);
        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        return cardPanel;
    }

    // Method to create a small rounded button
    private static JButton createSmallRoundedButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Smaller rounded corners
                super.paintComponent(g);
            }
        };

        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Makes the background transparent
        button.setBorderPainted(false); // Removes the border against the weird rectangle border since we curved the corners
        button.setOpaque(false); // Makes the button background transparent
        button.setForeground(Color.WHITE); // Button text color
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Set button font size
        button.setBackground(new Color(245, 71, 74)); // Button background color
        button.setPreferredSize(new Dimension(100, 30)); // Set button size

        return button;
    }

    // Method to open Google Maps with the given coordinates
    private static void openGoogleMaps(String location) {
        try {
            Desktop.getDesktop().browse(new URL("https://www.google.com/maps?q=" + location).toURI());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to open Google Maps.");
        }
    }

    // Method to open the Login form
    private static void openLoginForm() {
        // Create the login frame
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(360, 640); // 9:16 aspect ratio (360 x 640)
        loginFrame.setResizable(false);

        // Create a panel for the login form with background image
        JPanel loginPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon("C:\\Users\\chhaj\\OneDrive\\Desktop\\gaurav\\college\\Minor Project\\login_bg.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Scale image to fit the panel
            }
        };

        loginPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0; // To stretch horizontally

        // Create an empty component to add vertical space
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(Box.createVerticalStrut(60), gbc); // 60 pixels of vertical space


        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 30));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 30));

        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridy = 2;
        loginPanel.add(Box.createVerticalStrut(5), gbc); // 5 pixels of vertical space

        gbc.gridy = 3;
        loginPanel.add(usernameField, gbc);

        gbc.gridy = 4;
        loginPanel.add(Box.createVerticalStrut(15), gbc); // 15 pixels of vertical space

        gbc.gridy = 5;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridy = 6;
        loginPanel.add(Box.createVerticalStrut(5), gbc); // 5 pixels of vertical space

        gbc.gridy = 7;
        loginPanel.add(passwordField, gbc);

        gbc.gridy = 8;
        loginPanel.add(Box.createVerticalStrut(15), gbc); // 15 pixels of vertical space

        // Create a rounded submit button using the existing createRoundedButton method
        JButton loginSubmitButton = createRoundedButton("Login");
        loginSubmitButton.setPreferredSize(new Dimension(300, 40)); // Same size as used in the request form

        //Add the button to the grid
        gbc.gridy = 9;
        loginPanel.add(loginSubmitButton, gbc);

        loginSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch the username and password entered
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate username and password against the database
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "Please enter both username and password.");
                } else {
                    // JDBC connection and validation
                    try {
                        // Establish connection to the PostgreSQL database
                        String url = "jdbc:postgresql://localhost:5432/CRLRMS"; // Adjust database URL as needed
                        String dbUsername = "postgres"; // Replace with your database username
                        String dbPassword = "12345678"; // Replace with your database password

                        Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

                        // Prepare SQL query to check for the entered username and password
                        String sql = "SELECT * FROM USERS WHERE username = ? AND password = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, username);
                        stmt.setString(2, password);

                        // Execute the query and check if a result is returned
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            // If the username and password are correct, open the dashboard
                            openDriverDashboard(username, 1); // Pass the username (or userID) to the dashboard
                            loginFrame.dispose(); // Close the login form
                        } else {
                            JOptionPane.showMessageDialog(loginFrame, "Invalid credentials. Please try again.");
                        }

                        // Close the connection
                        conn.close();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(loginFrame, "Database connection error. Please try again later.");
                    }
                }
            }
        });

        // Add the panel to the login frame
        loginFrame.add(loginPanel);

        // Center the login frame on the screen
        loginFrame.setLocationRelativeTo(null);

        // Make the login frame visible
        loginFrame.setVisible(true);
    }

    // Quantity control class with +, - buttons and quantity display
    static class QuantityControl extends JPanel {
        private JButton decrementButton;
        private JButton incrementButton;
        private JLabel quantityLabel;
        private int quantity;
        private JCheckBox associatedCheckbox;

        public QuantityControl(JCheckBox checkbox) {
            this.associatedCheckbox = checkbox;
            quantity = 0;

            setLayout(new FlowLayout(FlowLayout.CENTER));

            // Create smaller buttons
            decrementButton = createSmallRoundedButton("-");
            incrementButton = createSmallRoundedButton("+");
            quantityLabel = new JLabel(String.valueOf(quantity));
            quantityLabel.setVisible(false); // Initially hidden

            // Action listeners for buttons
            decrementButton.addActionListener(e -> {
                if (quantity > 0) {
                    quantity--;
                    quantityLabel.setText(String.valueOf(quantity));
                }
            });

            incrementButton.addActionListener(e -> {
                quantity++;
                quantityLabel.setText(String.valueOf(quantity));
                quantityLabel.setVisible(true); // Show when quantity is greater than 0
            });

            add(decrementButton);
            add(quantityLabel);
            add(incrementButton);

            associatedCheckbox.addActionListener(e -> {
                if (associatedCheckbox.isSelected()) {
                    quantityLabel.setVisible(true);
                } else {
                    quantity = 0; // Reset quantity if unchecked
                    quantityLabel.setText(String.valueOf(quantity));
                    quantityLabel.setVisible(false); // Hide when checkbox is unchecked
                }
            });
        }

        // Method to create smaller rounded buttons because the normal function was not fitting in the screen
        private JButton createSmallRoundedButton(String text) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(getBackground());
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Smaller rounded corners
                    super.paintComponent(g);
                }
            };

            button.setFocusPainted(false);
            button.setContentAreaFilled(false); // Makes the background transparent
            button.setBorderPainted(false); // Removes the border again the weird rectangle border since we curved the corners
            button.setOpaque(false); // Makes the button background transparent
            button.setForeground(Color.WHITE); // Button text color
            button.setFont(new Font("Arial", Font.BOLD, 14)); // Set button font size
            button.setBackground(new Color(245, 71, 74)); // Button background color
            button.setPreferredSize(new Dimension(45, 45)); // Set smaller button size

            return button;
        }



        public int getQuantity() {
            return quantity;    //Function to return the quantity of each item to the postgreSQL request
        }
    }



    // Document class to restrict contact textbox input to numbers only
    static class NumericDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;

            // Allow only numeric input
            if (str.matches("[0-9]+")) {
                super.insertString(offs, str, a);
            }
        }
    }
}