//
//  RNAdGenerationBanner.h
//  RNAdGeneration
//
//  Created by chuross on 2018/05/28.
//  Copyright © 2018年 Facebook. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <ADG/ADG.h>
#import <React/RCTComponent.h>

@interface RNAdGenerationBanner : UIView

@property (nonatomic, copy) NSString *locationId;
@property (nonatomic, copy) NSString *bannerType;
@property (nonatomic, copy) NSNumber *bannerWidth;
@property (nonatomic, copy) NSNumber *bannerHeight;
@property (nonatomic, copy) RCTDirectEventBlock onMeasure;

- (void)load;

@end
