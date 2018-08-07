import React, { Component } from 'react';
import {
  requireNativeComponent,
  ViewPropTypes,
  UIManager,
  findNodeHandle,
} from 'react-native';
import PropTypes from 'prop-types';

const RNAdGenerationBanner = requireNativeComponent('RNAdGenerationBanner');

export default class AdGenerationBanner extends Component {
  constructor(props) {
    super(props);
    this.state = {
      bannerWidth: 0,
      bannerHeight: 0,
    };
  }

  componentDidMount() {
    // iOS側のpropsがcomponentDidMountよりも後に呼ばれるので遅延させる
    setTimeout(() => this.load());
  }

  componentWillMount() {
    var {
      bannerWidth,
      bannerHeight,
      bannerType,
    } = this.props;
    if (bannerType === 'free') {
      var style = {
        width: bannerWidth,
        height: bannerHeight,
      }
      this.setState({
        style: style
      });
    }
  }

  render() {
    return <RNAdGenerationBanner
      ref={ref => this._bannerView = ref}
      {...this.props}
      style={[this.props.style, this.state.style]}
      onMeasure={event => this._handleOnMeasure(event)}
      onLayout={event => this._handleOnLayout(event)}
    />;
  }

  _handleOnMeasure(event) {
    const { width, height } = event.nativeEvent;
    var {
      bannerType,
    } = this.props;
    // if (bannerType != 'free') {
    this.setState({
      style: { width, height }
    });
    // }
    if (this.props.onMeasure) this.props.onMeasure(event);
  }

  _handleOnLayout(event) {
    const { x, y, height, width } = event.nativeEvent.layout;
    const layoutSize = {
      layoutHeight: height,
      layoutWidth: width,
      layoutLeft: x,
      layoutTop: y,
    };
    this.setState({ layoutProps: layoutSize });
    if (this.props.onLayout) this.props._handleOnLayout(event);
  }

  load() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this._bannerView),
      UIManager.RNAdGenerationBanner.Commands.load,
      null,
    );
  }
}

AdGenerationBanner.propTypes = {
  ...ViewPropTypes,

  locationId: PropTypes.string,

  // sp|rect|large|tablet|free
  bannerType: PropTypes.string,

  // require as bannerType:free
  bannerWidth: PropTypes.number,
  bannerHeight: PropTypes.number,

  // layout measured event
  // (width, height)
  onMeasure: PropTypes.func,

  onLayout: PropTypes.func,

  // load ad
  load: PropTypes.func
};