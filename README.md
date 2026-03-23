# Brailllink
Braillink is an assistive technology system designed to improve smartphone accessibility for individuals who are blind, as well as those who are both blind and deaf.

It works by converting digital text into tactile Braille patterns using a hardware module attached to the back of a smartphone. This enables users to access and understand phone content through touch, rather than relying on visual or auditory feedback.

A key feature that differentiates Braillink from existing solutions is its compact and portable design. Unlike traditional Braille devices, which are often bulky and difficult to carry, Braillink is designed to integrate directly with a smartphone. This allows users to carry it effortlessly as part of their everyday device, making accessible communication more practical and convenient.

This repository contains the Android application responsible for capturing text from the device and transmitting it to the Braille hardware.

## Overview

The Braillink system consists of two main components:

- An Android application (this repository)
- A hardware module built using an ESP32 and solenoid-based Braille actuators

The app acts as the interface between the smartphone and the hardware, enabling real-time conversion of text into Braille patterns.

## Problem Statement

Most existing accessibility tools for smartphones rely on audio output, such as screen readers. While effective for visually impaired users, these solutions are not suitable for individuals who are both blind and deaf.

Braillink addresses this limitation by providing a tactile method of accessing smartphone content, allowing users to read information through touch rather than sound or sight.

## Application Features
### Notification Reading

The app uses Android’s Notification Listener Service to capture incoming notifications such as messages, emails, and app alerts. The text content is extracted and prepared for transmission.

### Screen Text Capture

An Accessibility Service is used to read on-screen text from applications and websites. This allows users to manually capture content beyond notifications.

### Bluetooth Communication

The app connects to an ESP32-based device using Bluetooth (Classic SPP). Once connected, it sends text data to the hardware module.

### Character-Based Transmission

Text is transmitted one character at a time to match the hardware’s ability to display Braille patterns sequentially. This ensures accurate and readable output.

### Control Interface

The app includes controls to:

- Start capturing and sending text
- Stop ongoing transmission immediately
- Monitor activity through a log display
## How It Works
1. The app captures text from notifications or the screen.
2. The text is processed and prepared for transmission.
3. A Bluetooth connection is established with the ESP32 device.
4. Characters are sent sequentially to the hardware.
5. The ESP32 converts each character into a Braille pattern.
6. Solenoids raise corresponding dots on the phone cover, allowing tactile reading.
## Tech Stack
- Kotlin (Android development)
- Jetpack Compose (UI)
- Android Accessibility Service
- Notification Listener Service
- Bluetooth Classic (SPP)
## Hardware Integration

The app is designed to work with a custom hardware setup that includes:

- ESP32 microcontroller
- Six-point Braille cell using solenoid actuators
- External power supply and relay system
## Permissions Required

The app requires the following permissions:

- Bluetooth (connect and scan)
- Notification access
- Accessibility service access
- Location (required for Bluetooth scanning on Android)
## Current Status

The project is currently at the prototype stage. Core functionality such as notification capture, Bluetooth transmission, and Braille actuation has been implemented. Further improvements are planned for performance, usability, and integration.

## Future Scope
- Improved text parsing and filtering
- Support for multiple Braille cells
- Better power optimization for hardware
- Enhanced UI for accessibility and control
##Team

Team HapticMinds
