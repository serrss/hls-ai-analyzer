# HLS Analyzer via ChatGPT API 🚀

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17+-red.svg)](https://jdk.java.net/)
[![Maven](https://img.shields.io/badge/maven-build-blue.svg)](https://maven.apache.org/)
[![OpenAI](https://img.shields.io/badge/openai-api-blue.svg)](https://platform.openai.com/docs/guides/authentication)

## 📌 Overview

**HLS Analyzer** is an **open-source tool** that fetches `.m3u8` files (HLS playlists), sends them to **ChatGPT**, and receives **analysis on stream integrity**.  
It follows **RFC-8216 (HLS standard)** to detect **errors and anomalies** in the playlist structure.

## ✨ Features

- 📡 **Downloads `.m3u8` files** dynamically from a given URL.
- 🔍 **Validates stream integrity** using **ChatGPT (GPT-3.5 Turbo)**.
- ⚡ **Fast & Lightweight**, built with Java + Maven.
- 🔐 **API Key Management** via `config.properties` or environment variables.
- 📖 **Open Source** under **Apache License 2.0**.

---

## 🏗️ Installation & Setup

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/serrss/hls-ai-analyzer.git
cd hls-ai-analyzer
```

### 2️⃣ Ensure Java 17+ & Maven Installed

Check Java:

```sh
java -version
```

Check Maven:

```sh
mvn -version
```

> ⚠️ Java **17+** is required!

### 3️⃣ Set Up API Key

You **must** provide an OpenAI API key.

#### Option 1️⃣: Use `config.properties`

Create a file **`src/main/resources/config.properties`**:

```properties
openai.api.key=sk-your-api-key-here
```

> ⚠️ **DO NOT commit this file!** Add it to `.gitignore`.

#### Option 2️⃣: Use Environment Variable

```sh
export OPENAI_API_KEY="sk-your-api-key-here"
```

(For Windows: use `set OPENAI_API_KEY="sk-your-api-key-here"` in CMD.)

---

## 🚀 Build & Run

### 1️⃣ Build the Project

```sh
mvn clean package
```

### 2️⃣ Run the Analyzer

```sh
java -jar target/hls-analyzer-1.0-SNAPSHOT.jar
```

---

## 🔍 Example Response from ChatGPT

✅ If the manifest is **valid**:

```json
{"status": "OK", "message": "Manifest is correct"}
```

❌ If errors are **found**:

```json
{"status": "FAIL", "message": "Segment duration mismatch at #EXTINF:10.5 (should be 10.0)"}
```

---

## 📜 License

This project is licensed under **Apache License 2.0**.  
See [LICENSE](https://opensource.org/licenses/Apache-2.0) for details.

---

## 👨‍💻 Contributing

**Contributions are welcome!** 🎉 If you find a bug or have a feature request:

1. **Fork the repository** on GitHub.
2. **Create a feature branch** (`git checkout -b feature-new`).
3. **Commit your changes** (`git commit -m "Add new feature"`).
4. **Push to GitHub** (`git push origin feature-new`).
5. **Open a Pull Request** 🚀.

---

## 📬 Contact

👤 **Author:** Serhii Romanov  
📧 **Email:** sergey.romanov.kh@gmail.com  
🌍 **GitHub:** [serrss](https://github.com/serrss)
