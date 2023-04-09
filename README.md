
Features:

Background location tracking: The app continuously tracks the user's location in the background, ensuring minimal impact on device performance and battery life.
Firebase/Cloud Firestore integration: The tracked location data is sent to Firebase/Cloud Firestore server for real-time storage and retrieval.
Room database: The app saves location data locally in the Room database, providing fast and efficient access to the user's location history.
Shared authentication module: The authentication module is shared between Tracker and Map modules, allowing users to log in once and access their location data across both modules.
Offline location storage: When the device is offline, the app saves the location data in the Room database. Once the internet connection is restored, the app uses WorkManager to send the saved location data to Firebase/Cloud Firestore server.
