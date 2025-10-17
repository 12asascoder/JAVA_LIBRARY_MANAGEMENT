# 🚀 Digital Asset Management System - Java

A comprehensive Digital Asset Management System built using Object-Oriented Analysis and Design principles. This system provides efficient management of digital assets, client assignments, and administrative operations through a console-based interface.

## 🎯 Overview

The Digital Asset Management System (DAMS) is designed to handle the complete lifecycle of digital assets within an organization. It supports multiple user roles, asset tracking, assignment management, and comprehensive reporting capabilities.

## 🏗️ System Architecture

The system follows a modular design with clear separation of concerns:

- **Asset Management**: Complete CRUD operations for digital assets
- **User Management**: Role-based access control for different user types
- **Assignment Tracking**: Monitor asset assignments and returns
- **Penalty System**: Automated penalty calculation for overdue assets
- **Reservation System**: Queue-based asset reservation management

## 👥 User Roles

### 🔹 **Client**
- Search and browse available digital assets
- Place reservation requests for assets
- View personal information and assignment history
- Check penalty status

### 🔹 **Operator**
- All Client functionalities, plus:
- Assign assets to clients
- Process asset returns
- Extend assignment periods
- Manage client accounts
- Update client information

### 🔹 **Manager**
- All Operator functionalities, plus:
- Add new digital assets to the repository
- Remove assets from the system
- Modify asset information
- View operator information
- Access comprehensive system reports

### 🔹 **Administrator**
- Add new operators and managers
- View complete assignment history
- Access system-wide asset inventory
- Manage user accounts and permissions

## 🚀 Getting Started

### Prerequisites

- **Java SE Development Kit 8 (JDK 8)** or higher
- **NetBeans IDE** (recommended for development)

### Installation & Setup

1. **Clone or Download** the project files
2. **Open NetBeans IDE** and navigate to File → Open Project
3. **Select the Project folder** to load the NetBeans project
4. **Configure Database Connection** (optional - system works without database)

### Database Configuration (Optional)

If you want to use the database features:

1. **Set up Java DB (Derby)**:
   - In NetBeans Services tab, right-click JavaDB → Properties
   - Change database location to the "Database" folder
   - Create a new connection with these credentials:
     ```
     Host: localhost
     Port: 1527
     Database: DAM
     Username: admin
     Password: admin123
     ```

2. **Connect to Database**:
   - Right-click the connection and select "Connect"
   - Ensure connection is active before running the project

## 🎮 Running the Application

### Method 1: With Database
```bash
java -cp ".:derbyclient-10.2.2.0.jar:src" DAM.AssetController
```

### Method 2: Demo Mode (No Database Required)
```bash
java -cp ".:derbyclient-10.2.2.0.jar:src" DAM.AssetControllerDemo
```

## 🔐 Default Credentials

- **Administrator Password**: `admin`
- **User IDs**: Auto-generated when creating new users
- **User Passwords**: Same as User ID (can be changed)

## 📋 Core Features

### 🔍 **Asset Management**
- Search assets by name, category, or creator
- Add new digital assets to the repository
- Modify existing asset information
- Remove assets from the system
- Track asset availability status

### 👤 **User Management**
- Create new clients, operators, and managers
- Update user information
- Role-based access control
- User authentication and authorization

### 📊 **Assignment Tracking**
- Assign assets to clients
- Track assignment history
- Process asset returns
- Extend assignment periods
- Calculate penalties for overdue assets

### 📝 **Reservation System**
- Place assets on reservation queue
- Manage reservation requests
- Automatic expiration of old reservations
- Priority-based asset assignment

### 💰 **Penalty Management**
- Automatic penalty calculation
- Configurable penalty rates
- Payment tracking
- Overdue asset notifications

## 🛠️ Technical Details

### **Design Patterns Used**
- **Singleton Pattern**: For AssetRepository management
- **Factory Pattern**: For user creation
- **Observer Pattern**: For asset status updates

### **Key Classes**
- `AssetController`: Main application controller
- `AssetRepository`: Core business logic and data management
- `DigitalAsset`: Represents individual digital assets
- `User`: Base class for all system users
- `Client`: End-user asset consumers
- `Employee`: Base class for system operators
- `Operator`: Asset assignment and return processing
- `Manager`: Full system administration capabilities

### **Database Schema**
- **ASSET**: Digital asset information
- **PERSON**: User account details
- **EMPLOYEE**: Staff member information
- **CLIENT**: Client-specific data
- **OPERATOR**: Operator workstation details
- **MANAGER**: Manager office information
- **ASSIGNMENT**: Asset assignment history
- **ON_RESERVATION_ASSET**: Active reservation requests
- **ASSIGNED_ASSET**: Currently assigned assets

## 📈 System Configuration

### **Default Settings**
- **Penalty Rate**: Rs 20 per day
- **Assignment Deadline**: 5 days
- **Reservation Expiry**: 7 days
- **Repository Name**: "TechCorp Digital Repository"

### **Customization**
All system parameters can be modified in the `AssetRepository` class:
```java
repo.setPenalty(20);           // Daily penalty rate
repo.setRequestExpiry(7);      // Reservation expiry days
repo.setReturnDeadline(5);     // Assignment deadline days
repo.setName("Your Company");  // Repository name
```

## 🔧 Development

### **Project Structure**
```
Project/
├── src/
│   └── DAM/
│       ├── AssetController.java      # Main application
│       ├── AssetRepository.java      # Core business logic
│       ├── DigitalAsset.java         # Asset entity
│       ├── User.java                 # Base user class
│       ├── Client.java               # Client implementation
│       ├── Employee.java             # Base employee class
│       ├── Operator.java             # Operator implementation
│       ├── Manager.java              # Manager implementation
│       ├── Assignment.java           # Assignment tracking
│       ├── ReservationRequest.java   # Reservation system
│       └── ReservationRequestOperations.java
├── Database/                         # Database files
└── derbyclient-10.2.2.0.jar         # Database driver
```

### **Building the Project**
```bash
javac -cp "derbyclient-10.2.2.0.jar" src/DAM/*.java
```

## 🐛 Troubleshooting

### **Common Issues**

1. **Database Connection Error**
   - Ensure Derby server is running
   - Check database credentials
   - Use demo mode if database is not available

2. **Compilation Errors**
   - Verify JDK 8+ is installed
   - Check classpath includes derbyclient jar
   - Ensure all source files are present

3. **Runtime Errors**
   - Check user permissions
   - Verify input format
   - Review error messages for specific issues

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### **How to Contribute**
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📞 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Review existing issues in the repository
3. Create a new issue with detailed information
4. Contact the development team

## 🎉 Acknowledgments

- Built using Java SE and Object-Oriented Design principles
- Database integration with Apache Derby
- Console-based interface for maximum compatibility
- Designed for educational and professional use

---

**Digital Asset Management System** - Efficiently managing digital resources for modern organizations.

*Built with ❤️ using Java*