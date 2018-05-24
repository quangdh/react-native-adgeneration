import React, { PureComponent } from 'react';
import { requireNativeComponent, ViewPropTypes } from 'react-native';

const RNAdGenerationBanner = requireNativeComponent('RNAdGenerationBanner');

export default class AdGenerationBanner extends PureComponent {
  render() {
    return <RNAdGenerationBanner {...this.props} />
  }
}

AdGenerationBanner.propTypes = {
  ...ViewPropTypes,

  locationId: string,

  // sp|rect|large|tablet
  bannerType: string,
  
  // layout measured event
  // (width, height)
  onMeasure: func,

  // load ad
  load: func
};