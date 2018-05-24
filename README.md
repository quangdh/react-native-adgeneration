
# react-native-ad-generation

## Getting started

`$ npm install react-native-ad-generation --save`

### Mostly automatic installation

`$ react-native link react-native-ad-generation`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-ad-generation` and add `RNAdGeneration.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNAdGeneration.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.github.chuross.rn.RNAdGenerationPackage;` to the imports at the top of the file
  - Add `new RNAdGenerationPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-ad-generation'
  	project(':react-native-ad-generation').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-ad-generation/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-ad-generation')
  	```


## Usage
```javascript
import RNAdGeneration from 'react-native-ad-generation';

// TODO: What to do with the module?
RNAdGeneration;
```
  