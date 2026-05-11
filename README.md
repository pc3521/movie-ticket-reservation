# Movie Ticket Reservation System

**Name:** Preethika Chennareddy  
**NetId:** pc3521

A full-stack web-based movie theater reservation system built with Spring Boot, Thymeleaf, Spring Security, WebSocket, and JPA.

---

## Features

- **Browse & Book Screenings** — Search movies, view showtimes, select ticket quantity
- **Interactive Seat Selection** — Real-time visual seat map with WebSocket-based seat locking
- **Booking Management** — View booking history, cancel reservations
- **User Authentication** — Registration, login, role-based access (Customer/Admin)
- **User Profile** — View account details
- **Real-Time Support Chat** — WebSocket-powered live chat between customers and admin
- **Admin Dashboard** — Manage screenings (add/edit/delete), view booking statistics
- **Booking Statistics** — Per-screening occupancy charts and detailed booking tables

---

## Tech Stack

| Layer     | Technology                                      |
|-----------|--------------------------------------------------|
| Backend   | Spring Boot 3.4, Spring Security, Spring Data JPA, Spring WebSocket + STOMP |
| Frontend  | Thymeleaf, JavaScript, AJAX, SockJS + STOMP.js   |
| Database  | H2 (dev) / MySQL (prod)                          |
| Build     | Maven                                            |
| Other     | Lombok, Hibernate                                |

---

## Prerequisites

- **Java 17** or higher 
- **Maven 3.8+** 
- (Optional) **MySQL 8.0+** — only if switching from H2

### Verify Installation

```bash
java -version    # Should show 17+
mvn -version     # Should show 3.8+
```

---

## How to Run

### 1. Clone / Extract the Project

Extract the zip file and navigate into the project folder:

```bash
cd movie-ticket-system
```

### 2. Build the Project

```bash
mvn clean install -DskipTests
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/movie-ticket-system-1.0.0.jar
```

### 4. Open in Browser

```
http://localhost:8080
```

---

## Default Login Credentials

| Role     | Username | Password   |
|----------|----------|------------|
| Admin    | admin    | admin123   |
| Customer | john     | john123    |

You can also register new accounts via the Sign Up page.

---

## Application URLs

| Page                  | URL                                    |
|-----------------------|----------------------------------------|
| Homepage              | http://localhost:8080/                  |
| Login                 | http://localhost:8080/login             |
| Register              | http://localhost:8080/register          |
| Customer Dashboard    | http://localhost:8080/customer/dashboard|
| My Bookings           | http://localhost:8080/customer/bookings |
| Support Chat          | http://localhost:8080/chat              |
| Admin Dashboard       | http://localhost:8080/admin/dashboard   |
| Admin Add Screening   | http://localhost:8080/admin/add-screening|
| Admin Support         | http://localhost:8080/admin/support     |
| H2 Console (dev)      | http://localhost:8080/h2-console        |

---

## Database Configuration

### H2 (Default — No Setup Required)

The app uses H2 file-based database by default. Data persists in `./data/movieticketdb`.

Access the H2 console at `http://localhost:8080/h2-console`:
- JDBC URL: `jdbc:h2:file:./data/movieticketdb`
- Username: `sa`
- Password: *(leave blank)*

### MySQL (Production)

1. Create the database:

```sql
CREATE DATABASE movie_ticket_db;
```

2. Edit `src/main/resources/application.properties`:
   - Comment out the H2 block
   - Uncomment the MySQL block
   - Update username/password
   - Change the Hibernate dialect to `org.hibernate.dialect.MySQLDialect`

---

## UI Flow

1. **Homepage** → Browse movies and screenings (public)
2. **Register/Login** → Create account or sign in
3. **Customer Dashboard** → Browse, search, select tickets
4. **Booking Form** → Review booking details and price
5. **Seat Selection** → Interactive seat map (real-time via WebSocket)
6. **Booking Confirmation** → Booking ref and details
7. **My Bookings** → View/cancel reservations
8. **Live Chat** → Customer initiates, admin responds in real time
9. **Admin Dashboard** → Manage all screenings
10. **Booking Statistics** → Occupancy charts and booking details per screening

---

## Project Structure

```
movie-ticket-system/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/movieticket/
    │   ├── MovieTicketApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── WebSocketConfig.java
    │   │   └── DataInitializer.java
    │   ├── model/
    │   │   ├── User.java
    │   │   ├── Movie.java
    │   │   ├── Screening.java
    │   │   ├── Booking.java
    │   │   ├── BookedSeat.java
    │   │   ├── ChatSession.java
    │   │   └── SupportMessage.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── MovieRepository.java
    │   │   ├── ScreeningRepository.java
    │   │   ├── BookingRepository.java
    │   │   ├── BookedSeatRepository.java
    │   │   ├── ChatSessionRepository.java
    │   │   └── SupportMessageRepository.java
    │   ├── dto/
    │   │   ├── RegistrationDto.java
    │   │   ├── ScreeningDto.java
    │   │   ├── SeatSelectionDto.java
    │   │   └── ChatMessageDto.java
    │   ├── service/
    │   │   ├── CustomUserDetailsService.java
    │   │   ├── UserService.java
    │   │   ├── BookingService.java
    │   │   └── ChatService.java
    │   └── controller/
    │       ├── HomeController.java
    │       ├── AuthController.java
    │       ├── CustomerController.java
    │       ├── AdminController.java
    │       ├── BookingApiController.java
    │       └── ChatController.java
    └── resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/
            ├── home.html
            ├── fragments/layout.html
            ├── auth/login.html
            ├── auth/register.html
            ├── customer/
            │   ├── dashboard.html
            │   ├── booking-form.html
            │   ├── seat-selection.html
            │   ├── booking-confirmation.html
            │   ├── my-bookings.html
            │   └── profile.html
            ├── admin/
            │   ├── dashboard.html
            │   ├── add-screening.html
            │   ├── edit-screening.html
            │   ├── statistics.html
            │   └── support.html
            └── chat/
                └── chat-room.html
```
