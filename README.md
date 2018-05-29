# react-native-adgeneration
![](https://badge.fury.io/js/react-native-adgeneration.svg)

## Installation
## Android
```
$ npm install react-native-adgeneration --save
$ react-native link react-native-adgeneration
```

## iOS
1. setup CocoaPods
```
$ cd ios/
$ pod init
```

2. instllation in root dir
```
$ npm install react-native-adgeneration --save
$ react-native link react-native-adgeneration
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
  