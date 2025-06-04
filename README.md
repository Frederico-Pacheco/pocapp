# PoC Application

This is a Proof of Concept (POC) project for connecting to a Wi-Fi network programmatically.

## Setup
Before running the project, you need to configure the Wi-Fi network details by adding them to the `local.properties` file in the project root directory.

**Required Properties:**

```
wifi.ssid=YourNetworkName
wifi.pks=YourNetworkPassword
```

## Wi-Fi Network Configuration Restriction (Android 10+)

Starting from **Android 10 (API level 29)**, the Android platform introduced stricter privacy and security measures regarding how applications can interact with Wi-Fi configurations on the device.
As part of these changes, the `WifiManager.addNetwork()` and related APIs were deprecated and restricted for third-party apps.

### Restrictions on direct access to configured Wi-Fi networks

To protect user privacy, manual configuration of the list of Wi-Fi networks is restricted to system apps and device policy controllers (DPCs). A given DPC can be either the device owner or the profile owner.

If your app targets Android 10 or higher, and it isn't a system app or a DPC, then the following methods don't return useful data:

- The `getConfiguredNetworks()` method always returns an empty list.

> ★ **Note:** If a carrier app calls getConfiguredNetworks(), the system returns a list containing only the networks that the carrier configured.

- Each network operation method that returns an integer value — `addNetwork()` and `updateNetwork()` — always returns -1.

- Each network operation that returns a boolean value — `removeNetwork()`, `reassociate()`, `enableNetwork()`, `disableNetwork()`, `reconnect()`, and `disconnect()` — always returns false.

If your app needs to connect to Wi-Fi networks, use the following alternative methods:

- To trigger an instant local connection to a Wi-Fi network, use `WifiNetworkSpecifier` in a standard `NetworkRequest` object.
- To add Wi-Fi networks for consideration for providing internet access to the user, work with `WifiNetworkSuggestion` objects. You can add and remove networks that appear in the auto-connect network selection dialog by calling `addNetworkSuggestions()` and `removeNetworkSuggestions()`, respectively. These methods don't require any location permissions.

source: https://developer.android.com/about/versions/10/privacy/changes#configure-wifi

## CTS Verifier

The Android Compatibility Test Suite Verifier (CTS Verifier) supplements the Compatibility Test Suite (CTS). While CTS checks APIs and functions that can be automated, CTS Verifier provides tests for APIs and functions that can't be tested on a stationary device without manual input or positioning, such as audio quality, touchscreen, accelerometer, and camera.

source: https://source.android.com/docs/compatibility/cts/verifier

### Setup

To set up the CTS Verifier testing environment:

1. Download the [CTS Verifier APK](https://source.android.com/docs/compatibility/cts/downloads) for the version of Android to test.
2. Connect the DUT to the Linux computer.
3. From a terminal on the Linux computer, install `CtsVerifier.apk` on the DUT.
   ```shell
   adb install -r -g CtsVerifier.apk
   ```