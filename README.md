# Cafeteria Management System
## SW121 – Object Oriented Programming | Batch K25SW
### Mehran University of Engineering and Technology – Khairpur Campus

---

## 📋 Project Overview

The **Cafeteria Management System** is a full-featured desktop application built with **Java Swing** and **MySQL**, designed to digitise and streamline cafeteria operations. It addresses real business requirements including authentication, menu management, order processing, user management, and analytics reporting.

---

## 🎯 Complex Engineering Problem – Addressed Characteristics

| # | CEP Characteristic | How Addressed |
|---|---|---|
| 1 | Depth of Knowledge | Multi-tier architecture (UI → DAO → DB), JDBC, OOP design patterns |
| 2 | Range of Conflicting Requirements | Admin vs User role access, data validation vs usability |
| 3 | Depth of Analysis Required | Transaction management, relational DB design with foreign keys |
| 4 | Infrequently Encountered Issues | Concurrent order updates, JDBC connection pooling, Swing EDT |
| 5 | Beyond Codes/Standards | Coding standards: Javadoc, naming conventions, modular packages |
| 6 | Diverse Stakeholders | Admin (full control) and User (restricted) roles |
| 7 | Interdependence | Orders depend on Menu Items, which depend on Categories; Users placed Orders |
| 8 | Significant Consequences | Incorrect order handling = revenue loss; correct auth = data security |
| 9 | Judgement | Role-based access control requires design judgement |

---

## ✅ Functional Requirements Mapping

| Requirement | Implemented |
|---|---|
| Login / Authentication with roles | ✅ Admin & User login with role-based UI |
| CRUD – All major entities | ✅ Menu Items, Categories, Users, Orders |
| Search & Filter | ✅ Keyword + category/status filters on all panels |
| Data Validation (UI + DB) | ✅ Input checks in dialogs + DB constraints |
| Reports Generation | ✅ Summary, Daily, Top Items, User Spending |
| Attractive GUI | ✅ Custom colour theme, gradient login, card panels |
| Database Connectivity (JDBC) | ✅ MySQL via JDBC with singleton connection |
| Modular Code with Comments | ✅ Separate packages for model/dao/ui/util/auth |

---

## 🗂️ Project Structure

```
CafeteriaMS/
├── src/
│   └── cafeteria/
│       ├── Main.java                   ← Entry point
│       ├── auth/
│       │   └── Session.java            ← Singleton session manager
│       ├── model/
│       │   ├── User.java
│       │   ├── MenuItem.java
│       │   ├── Category.java
│       │   └── Order.java              ← (includes OrderItem inner class)
│       ├── dao/
│       │   ├── UserDAO.java
│       │   ├── MenuItemDAO.java
│       │   └── OrderDAO.java
│       ├── ui/
│       │   ├── LoginFrame.java
│       │   ├── MainFrame.java
│       │   ├── DashboardPanel.java
│       │   ├── MenuPanel.java
│       │   ├── OrderPanel.java
│       │   ├── UserManagementPanel.java
│       │   └── ReportPanel.java
│       └── util/
│           ├── DatabaseConnection.java  ← Singleton DB connection
│           └── UIConstants.java        ← Centralized style constants
├── database/
│   └── cafeteria_db.sql               ← Schema + default data
├── lib/
│   └── (place mysql-connector here)
├── MANIFEST.MF
├── build_and_run.bat                  ← Windows build script
└── build_and_run.sh                   ← Linux/Mac build script
```

---

## ⚙️ Installation & Setup

### Prerequisites
- Java JDK 11 or higher
- MySQL Server 8.0+
- MySQL Connector/J (JDBC Driver)
- NetBeans IDE (recommended) or any Java IDE

### Step 1 – Database Setup
1. Open MySQL Workbench or phpMyAdmin
2. Run the file: `database/cafeteria_db.sql`
3. This creates the `cafeteria_db` database with all tables and default data

### Step 2 – Add MySQL Connector
1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
2. Place the `.jar` file in the `lib/` folder
3. Update `MANIFEST.MF` with the correct JAR filename

### Step 3 – Configure Database Connection
Open `src/cafeteria/util/DatabaseConnection.java` and update:
```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/cafeteria_db";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "your_password_here";
```

### Step 4A – Run in NetBeans
1. Open NetBeans → File → Open Project → select `CafeteriaMS/`
2. Right-click project → Properties → Libraries → Add JAR → select connector JAR
3. Press **F6** or click Run

### Step 4B – Run via Command Line
**Windows:**
```
build_and_run.bat
```
**Linux/Mac:**
```
chmod +x build_and_run.sh
./build_and_run.sh
```

---

## 🔐 Default Login Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | admin    | admin123  |
| User  | user1    | user123   |
| User  | user2    | user123   |

---

## 🖥️ Application Features

### Login Screen
- Gradient background with university branding
- Enter key support, error feedback
- Background authentication thread (non-blocking UI)

### Dashboard
- Welcome message with current date
- 4 live stat cards: Today's Orders, Pending, Revenue, Menu Items
- System information panel

### Menu Management
- View all menu items with category, price, availability
- Search by name + filter by category
- Admin: Add, Edit, Delete items; Manage categories
- Double-click row to edit

### Order Management
- **Order List** tab: View, search, filter by status; Admin can update status or delete
- **New Order** tab: Cart-style ordering; select items + quantity; place order with notes
- Transaction-safe order creation (ACID)

### User Management (Admin only)
- Full CRUD on user accounts
- Role assignment (Admin/User)
- Password change functionality

### Reports (Admin only)
- **Summary**: Total revenue, order counts
- **Daily Orders**: Last 30 days breakdown
- **Top Items**: Best-selling menu items ranked
- **Orders by User**: Customer spending summary

---

## 🏗️ OOP Concepts Applied

| Concept | Where Used |
|---|---|
| **Encapsulation** | All model classes (User, MenuItem, Order) with getters/setters |
| **Inheritance** | All JPanels extend JPanel; custom renderers extend DefaultTableCellRenderer |
| **Polymorphism** | paintComponent() overriding in card panels |
| **Abstraction** | DAO layer abstracts all SQL from UI |
| **Singleton** | DatabaseConnection, Session classes |
| **Composition** | Order contains List of OrderItem |
| **Inner Class** | OrderItem is an inner class of Order |

---

## 📐 Coding Standards (R3)

- Package naming: lowercase (`cafeteria.ui`, `cafeteria.dao`)
- Class naming: PascalCase (`LoginFrame`, `UserDAO`)
- Method naming: camelCase (`getConnection`, `loadData`)
- Constants: UPPER_SNAKE_CASE (`PRIMARY_COLOR`, `TABLE_ROW_HEIGHT`)
- Javadoc comments on every class and public method
- Single Responsibility: each class has one clear purpose
- DRY: `UIConstants` centralises all styles; `DatabaseConnection` shared across all DAOs

---

## ♻️ Reusability (R4)

- `UIConstants` – used by all 7 UI classes for consistent styling
- `DatabaseConnection` – singleton reused by UserDAO, MenuItemDAO, OrderDAO
- `styledButton()` / `btn()` helper methods inside panels – reused for all buttons
- `extractUser()`, `extractItem()`, `extractOrder()` – reusable result-set mappers in DAOs
- `Session` singleton – accessed from any class without passing objects around
- Model classes (User, MenuItem, Order) – reused across DAO, UI, and report layers

---

## 👨‍💻 Developer Notes

- All database operations use `PreparedStatement` to prevent SQL injection
- Order creation uses a database transaction (`setAutoCommit(false)`) for data integrity
- SwingWorker is used in `LoginFrame` to keep the UI responsive during authentication
- The `CardLayout` in `MainFrame` allows smooth panel switching without re-creation
