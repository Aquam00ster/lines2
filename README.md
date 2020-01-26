# Install Native Libraries

In command prompt, execute the following:

```
gradlew extractTestJNI
```

This will install opencv, networktables, etc.

# Running Application

## Single Camera Shot

In command prompt:

```
gradlew runDisplay
```

NOTE: if Microsoft LifeCam 3000 available, it will be chosen.

## Process an Image

To process, for example, "folder\camerashot.jpg":

```
gradlew runDisplay --args folder\camerashot.jpg
```

## Live Video Processing

```
gradlew runStream
```

# Running Tests

```
gradlew test
```