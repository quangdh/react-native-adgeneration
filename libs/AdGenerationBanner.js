import React, { PureComponent } from 'react';
import { requireNativeComponent } from 'react-native';

const RNAdGenerationBanner = requireNativeComponent('RNAdGenerationBanner');

export default class AdGenerationBanner extends PureComponent {
  render() {
    return <RNAdGenerationBanner {...this.props} />
  }
}