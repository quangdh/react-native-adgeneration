
# react-native-adgeneration

## Installation
```
$ npm install react-native-adgeneration --save
$ react-native link react-native-adgeneration
```

### Android
add maven repositories

**./android/build.gradle**

```
allprojects {
    repositories {
        ....

        maven { url 'https://dl.google.com/dl/android/maven2/' }
        maven { url 'https://adgeneration.github.io/ADG-Android-SDK/repository' }
    }
}
```

## Usage
```javascript
import { AdGenerationBanner } from 'react-native-adgeneration';

<AdGenerationBanner
	locationId='your_ad_id'
	bannerType='sp' // sp|rect|tablet|large
/>
```

## License
MIT
  