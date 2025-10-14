SAJ Connect (Student–Alumni Job Connect App)

Overview

SAJ Connect is a prototype Android app created with Kotlin and Firebase for the OPSC6312 module. The app is designed to connect students, graduates, and mentors by listing job and internship opportunities. It allows users to apply for positions, upload CVs (as a simulation), and track their application status in real time.

Key features of the prototype include:

    User authentication and registration with password encryption, using Firebase Authentication.
    Cloud Firestore as a REST-based backend.
    Secure management of user data.
    The ability to post and search for jobs, including filter options.
    Application submission and cancellation functions.
    Real-time tracking of application status: Pending, Approved, or Rejected.
    Version control and automated build tests through GitHub integration.

Purpose of the App

The main purpose of this app is to help students and alumni find relevant opportunities and connect with employers or mentors through a user-friendly mobile platform.

The core goals are:

    To make it easier to discover and apply for jobs.
    To provide cloud-based data synchronization.
    To show how to integrate the Firebase SDK in an Android project.
    To practice secure authentication, user interface validation, and backend data operations.

Core Features

    User registration and login
    Password encryption using Firebase Authentication
    Listings of job opportunities
    Search and filter features, allowing users to sort by category and location
    Ability to apply to opportunities, add a message, and simulate CV uploads
    Section to view applications, cancel them, and track their status
    Profile management, including options to change display name and password
    Secure logout and safe navigation across the app
    Continuous integration (CI) testing with GitHub Actions

REST API & Firebase Integration

Firebase serves as a RESTful API, streamlining the process for CRUD (Create, Read, Update, Delete) operations. Here is how Firebase actions map to standard REST API operations:

    Create: db.collection("opportunities").add() → POST /opportunities
    Read: db.collection("opportunities").get() → GET /opportunities
    Update: db.collection("users").document(uid).update() → PUT /users/:id
    Delete: db.collection("applications").document(id).delete() → DELETE /applications/:id

Design Considerations

    The backend uses Firebase Firestore and Authentication for easy REST API and SDK integration.
    Passwords are managed with Firebase Authentication, which ensures they are hashed and encrypted for security.
    The user interface uses blue and green accent colors to create a professional and academic appearance.
    File uploads are simulated as PDF uploads, which is suitable for a prototype and avoids dealing with device permissions.
    The data models are based on Opportunities, Users, and Applications, following the design document.
    Unit testing is handled through GitHub Actions to ensure consistent builds.

