# RoboCrop — Automated Real-Time Pest Detection & Removal System

> 🏆 **1st Prize — VJ National Hackathon 2024** (Precision Farming Automation)  
> 🥈 **Runner-up — Smart India Hackathon 2023** (Top 5 of 500+ teams nationwide)

RoboCrop is a real-time automated pest detection and removal system built in collaboration with the ECE hardware team. A camera-equipped rover navigates fields autonomously, detects pests via computer vision, and triggers targeted removal — eliminating the need for manual inspection.

---

## 📈 Results

| Metric | Before | After |
|---|---|---|
| Pest Detection Accuracy | 77% | **93%** |
| Manual Inspection Required | Yes | **Eliminated** |

---

## 🔧 System Architecture

```
┌──────────────────────────────────────────┐
│              Camera Rover                │
│  (Arduino + ESP32-CAM + DHT Sensor)      │
└───────────────────┬──────────────────────┘
                    │ Live video stream + weather data
                    ▼
┌──────────────────────────────────────────┐
│         Flask Backend (Python)           │
│  ┌─────────────────────────────────┐     │
│  │  OpenCV — Real-time Detection   │     │
│  │  Pest Classification Model      │     │
│  │  Removal Method Classifier      │     │
│  └─────────────────────────────────┘     │
└───────────────────┬──────────────────────┘
                    │ Control signal
                    ▼
        Targeted Pest Removal Action
```

---

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| Vision & Detection | Python, OpenCV |
| Backend | Flask |
| Hardware (Rover) | Arduino, ESP32-CAM |
| Sensors | DHT11 (Temperature & Humidity) |
| Data | Weather parameters, pest classification |

---

## ✨ Key Features

- **Real-time pest detection** using live camera feed from the rover
- **Pest removal method classification** — selects appropriate removal strategy per pest type
- **Weather-aware detection** — integrates temperature and humidity from DHT sensor for contextual analysis
- **Hardware-software co-design** — built jointly with ECE team for a complete end-to-end solution
- **Accuracy improved from 77% → 93%** through model tuning and dataset curation

---

## 📁 Project Structure

```
RoboCrop-Project/
├── RoboCrop-master/                  # Core Flask app & detection pipeline
├── PEST REMOVAL METHOD CLASSIFICATION/  # Classifier for removal strategy
├── Weather_parameters/               # DHT sensor data integration
├── Camera_Car.ino                    # Arduino code for rover movement
├── dht_sheets.ino                    # DHT sensor → Google Sheets logging
└── pests.json                        # Pest metadata
```

---

## 🚀 Getting Started

### Prerequisites

- Python 3.8+
- OpenCV: `pip install opencv-python`
- Flask: `pip install flask`
- Arduino IDE (for hardware setup)
- ESP32-CAM module + DHT11 sensor

### Run the Detection Server

```bash
cd RoboCrop-master
pip install -r requirements.txt
python app.py
```

### Flash the Rover

Open `Camera_Car.ino` in Arduino IDE, configure your Wi-Fi credentials and the Flask server URL, then flash to the ESP32-CAM board.

---

## 🏆 Awards

- **1st Prize** — VJ National Hackathon 2024, Precision Farming Automation Track
- **Runner-up (Top 5 / 500+ teams)** — Smart India Hackathon 2023

---

## 👤 Author

**Ravikiran Pedapalli**  
[LinkedIn(https://linkedin.com/in/pedapalli-ravi-kiran-ab5006254) · [GitHub](https://github.com/ravikiranp04)
