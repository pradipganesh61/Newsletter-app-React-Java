### Newsletter Signup Front-End Application
This is a front-end application for subscribing and unsubscribing to a newsletter. The application allows users to enter their email addresses and manage their subscription status via a simple interface.

## Tools and Technologies
Editor: VSCode
Node Version: v18.17.0
React Version: 18.3.1
Getting Started
Prerequisites
Ensure that you have Node.js and npm installed on your system. You can download Node.js from nodejs.org.

## Installation
Save this project to your system.
Navigate to the project directory and run the following commands:
npm install
npm start
This will install the necessary dependencies and start the application.

## Features
Email Box
The UI includes an email box where users can enter their email address.
Two Button for Subscription and UnSubscription

# Restrictions
1. If the user tries to click the subscribe/unsubscribe button without entering an email address, an error message prompting them to enter the email address will be displayed.
2. If the user enters an invalid email address, an error message indicating that the email address is invalid will be displayed.
Valid Email Address
3. When the user enters a valid email address and clicks on the subscribe/unsubscribe button, it will call a backend API built using Spring Boot. The API link is located in the file /src/App.tsx
const webAPI = 'http://localhost:5000/api/newsletter';

4. The application will receive a response from the backend indicating whether the subscription or unsubscription was successful.
5. If the user successfully subscribes/unsubscribes to the newsletter, a success message will pop up in the bottom left corner of the page.
6. If the user is unable to subscribe/unsubscribe to the newsletter, an error message will pop up in the bottom left corner of the page.