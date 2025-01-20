---

# Sahay - A Crisis Relief Logistics and Resource Management System  

## Overview  
The **Crisis Relief Logistics and Resource Management System** is an application designed to manage resources effectively during emergencies or disaster situations. It enables users to request essential supplies, tracks these requests, and optimizes resource allocation using intelligent algorithms. The system ensures seamless coordination between users, volunteers, and administrators to provide efficient disaster response.  

## Features  

- **Resource Request Management:**  
  Users can request essential supplies such as food packs, water bottles, first aid kits, blankets, and clothes.  

- **Role-Based Access:**  
  Differentiates functionalities for admins, volunteers, and requesters to enhance security and streamline tasks.  

- **Real-Time Updates:**  
  Requests and statuses are updated dynamically using efficient backend mechanisms.  

- **Algorithmic Optimization:**  
  - **Dijkstra's Algorithm:** Determines the shortest path to deliver resources to affected areas.  
  - **Priority Queue:** Ensures requests involving medical supplies are prioritized.  

- **Emergency Services:**  
  Quick access to emergency numbers using the **Emergency Services Call Button**, enabling users to contact authorities like paramedics or firefighters instantly.  

- **Hamburger Menu Navigation:**  
  Access features like:
  - **Emergency Services**  
  - **Logout**  
  - **Requests**  

- **User-Friendly Interface:**  
  Intuitive design powered by Java Swing, allowing users to interact with the system effortlessly.  

## Technology Stack  

- **Frontend:** Java Swing 
- **Backend:** Java  
- **Database:** PostgreSQL  
- **Algorithms:** Dijkstra’s Algorithm, Priority Queue  

## Installation  

1. **Clone the Repository:**  
   ```bash
   git clone https://github.com/PranitThomas/Crisis-Relief-Logistics-and-Resource-Management-System.git
   cd Crisis-Relief-Logistics-and-Resource-Management-System
   ```  

2. **Set Up PostgreSQL Database:**  
  
3. **Configure Database Connection:**  
   Update the database URL, username, and password in the Java source code.

4. **Run the Application:**  
   Compile and execute the main Java class:  
   ```bash
   javac ReliefAidApp2.java
   java ReliefAidApp2
   ```  

## Future Enhancements  

1. **Modern Frameworks:**  
   Replace Java Swing with JavaFX or a web-based UI for better user experience and cross-platform compatibility.  

2. **Offline Support:**  
   Implement a local database that syncs with the central PostgreSQL server when online.  

3. **Mobile Support:**  
   Develop a mobile app using Flutter or React Native for broader accessibility.  

4. **Advanced Analytics:**  
   Introduce a dashboard for admins to monitor resource trends and generate reports.  


---
