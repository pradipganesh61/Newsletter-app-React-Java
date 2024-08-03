# Newsletter Subscription Service

## Overview

This project is an application for managing newsletter subscriptions including rate limiting and IP blocking mechanisms. It is built using Spring Boot and utilizes Bucket4j for rate limiting and in-memory caching to store subscription states.

## Features

- **Newsletter Users**: List the number of users in the newsletter with subscription status.
- **Subscription/Unsubscription**: Allows users to subscribe or unsubscribe from the newsletter.
- **Format/Domain Validation**: Validate the users email and domain names.
- **Risk Assessment**: Validate the users email with hunter.io email verifier to make sure only valid email can subscribe.
- **Rate Limiting**: Controls the number of requests a user can make within a specified timeframe.
- **IP Blocking**: Temporarily blocks IP addresses that exceed the allowed number of requests per minute.
- **Global Exception Handling**: Provides error handling with appropriate HTTP status codes.

## Configuration

Configuration properties are managed in the `application.properties` file.

### `application.properties`

```properties
hunter.api.url=https://api.hunter.io/v2/email-verifier
hunter.api.key={YOUR_KEY}
rate.limit.requests.per.minute=10
rate.limit.block.time.in.minutes=5
bucket4j.requests.limit=10
bucket4j.refill.interval.minutes=1
```
## Running the Application

To run the application, use the following command:

```sh
mvn spring-boot:run
```