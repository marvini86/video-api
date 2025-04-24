# 📺 Video Streaming API

## Description

The **Video Streaming API** provides a robust and scalable backend for managing video content, metadata, and streaming-related operations. Designed for use in media platforms, content distribution systems, or educational streaming services, this API offers endpoints for uploading, publishing, searching, and analyzing videos.

---

## 🔧 Features

- Upload and publish video content
- Retrieve detailed video metadata and summaries
- Search for videos based on multiple criteria (title, genre, director, etc.)
- Track video statistics like views and impressions
- Manage relationships between videos and actors (cast and main actor)
- Perform CRUD operations on video entities


## Endpoints

| Method | Endpoint                       | Description                                     |
|--------|--------------------------------|-------------------------------------------------|
| GET    | `/v1/videos`                   | List paginated videos with optional sorting     |
| POST   | `/v1/videos`                   | Publish a new video with metadata               |
| GET    | `/v1/videos/{id}`              | Retrieve full details of a video by ID          |
| POST   | `/v1/videos/{id}/play`         | Simulate playing a video and retrieve URL       |
| POST   | `/v1/videos/search`            | Search videos by filters                        |
| GET    | `/v1/videos/{id}/stats`        | Get video view and impression statistics        |
| PUT    | `/v1/videos/{id}`              | Update existing video details                   |
| DELETE | `/v1/videos/{id}`              | Delete a video                                  |
| POST   | `/v1/videos/upload`            | Upload a video file for processing              |


## 🧰 Technologies

- Java 17
- Spring Boot
- H2
- Liquibase
- Docker
- GitHub Actions(CI/CD)
- Swagger

## 🚀 Running the Application

You can run the application using Docker Compose:

```
docker-compose up --build

```
Or using Maven directly:

```
mvn spring-boot:run
```


## ✅ Run tests

To execute tests, run:


```
mvn verify
```

## 📘 Swagger

API documentation is available via Swagger UI at:http://localhost:8080/api/swagger-ui/index.html.

## 📬 Postman

Postman collections are available in the `docs` folder.


## 🔄 CI/CD

![Build - Develop](https://github.com/marvini86/video-api/actions/workflows/develop.yml/badge.svg)
![Build - Main](https://github.com/marvini86/video-api/actions/workflows/prd.yml/badge.svg)

This project uses **GitHub Actions** for continuous integration and deployment.

### 🛠️ Configured Workflows

| Workflow Name | Branch Trigger | Description                                |
|---------------|----------------|--------------------------------------------|
| `develop.yml` | `develop`      | Builds the app and runs tests on every push |
| `prd.yml`     | `main`         | Builds the app and runs tests on every push |

### 🔍 Check Workflow Runs

You can view the latest pipeline activity here:  
👉 [**GitHub Actions Dashboard**](https://github.com/marvini86/video-api/actions)
