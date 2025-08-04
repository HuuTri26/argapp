Trae

# ARG App - Agricultural Products Shopping Application
## Overview
ARG App is a modern Android mobile application developed to provide an online agricultural products shopping platform. The app connects consumers with fresh agricultural products, allowing users to easily browse, search, and purchase high-quality agricultural products.

## Key Features
### User Authentication
- Register new account
- Login with email and password
- Email OTP verification
- Session persistence (automatic login)
- Personal information management
### Product Browsing
- Display product categories
- Search products by name
- Filter products by category
- View product details
- Display popular products based on sales data
### Shopping Cart and Checkout
- Add products to cart
- Adjust product quantities
- Remove products from cart
- Calculate total order value
- Process payments and generate invoices
### Wishlist
- Add products to wishlist
- View and manage wishlist
- Add products from wishlist to shopping cart
### Account Management
- View and edit personal information
- Change password
- View order history
- View order details
## Architecture and Technology
### MVC Architecture
The application is built following the Model-View-Controller (MVC) pattern to separate processing logic, data, and user interface:

- Model : Manages data and interacts with Firebase
- View : User interface (Activities, Fragments, Layouts)
- Controller : Handles business logic and connects Model with View
### Technologies Used
- Language : Java
- Database : Firebase Realtime Database
- Authentication : Firebase Authentication
- User Interface : Material Design Components
- Navigation : Navigation Component
- State Management : SharedPreferences
## Technical Highlights
### 1. Flexible Data Model
The application uses HashMap data structures to manage shopping cart and wishlist, allowing quick data retrieval and updates with O(1) complexity.

### 2. Asynchronous Processing
Uses callbacks to handle asynchronous operations such as loading data from Firebase, ensuring the user interface remains responsive and unblocked.

### 3. Flexible Navigation
Uses Android Jetpack's Navigation Component to manage navigation between screens, providing a smooth and consistent user experience.

### 4. Login State Management
Stores login information using SharedPreferences to support automatic login feature, enhancing user experience.

### 5. Secure Transaction Processing
Implements order processing and payment mechanisms with security checks, ensuring data integrity.

## System Requirements
- Android SDK 24 or higher 
- Device with internet connection
## Installation and Running Guide
1. 
   Clone the repository to your local machine: git clone https://github.com/HuuTri26/argapp.git

  2. 1.
   Open the project with Android Studio
3. 2.
   Sync Gradle and install dependencies
4. 3.
   Connect with Firebase:
   
   - Create a new Firebase project
   - Add Android application to Firebase project
   - Download google-services.json file and place it in the app/ directory
   - Enable Firebase Authentication and Realtime Database
5. 4.
   Run the application on a real device or emulator
## Development Prospects
- Integrate online payment systems
- Add product rating and review features
- Develop push notification system
- Optimize performance and user experience
- Expand multilingual support
## Contribution
We always welcome contributions from the community. If you want to contribute, please:

1. 1.
   Fork the project
2. 2.
   Create a feature branch ( git checkout -b feature/amazing-feature )
3. 3.
   Commit your changes ( git commit -m 'Add some amazing feature' )
4. 4.
   Push to the branch ( git push origin feature/amazing-feature )
5. 5.
   Open a Pull Request


  
