<h1 align="center">Compose-Utils</h1>

<p align="center">    
    <a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API">
</a>
<img src="https://img.shields.io/github/v/release/w2sv/Compose-Utils?include_prereleases" alt="GitHub release (latest by date including pre-releases)">
<a href="https://github.com/w2sv/Compose-Utils/actions/workflows/workflow.yaml">
    <img src="https://github.com/w2sv/Compose-Utils/actions/workflows/workflow.yaml/badge.svg" alt="Build">
</a>
<img src="https://img.shields.io/github/license/w2sv/Compose-Utils" alt="GitHub License">
</p>

## Contents

### rememberSaveable state savers

```kotlin
var color by rememberSaveable(stateSaver = colorSaver()) {
    mutableStateOf(Color.Red)
}

var nullableColor by rememberSaveable(stateSaver = nullableColorSaver()) {
    mutableStateOf<Color?>(null)
}

var nullableCustomObject by rememberSaveable(stateSaver = nullableListSaver(saveNonNull = { /* saveNonNull logic */ }, restoreNonNull = { /* restoreNonNull logic */ }) {
    mutableStateOf<CustomObject?>(null)
}
```

### Configuration

- isLandscapeModeActivated
- isPortraitModeActivated
