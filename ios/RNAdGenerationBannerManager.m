//
//  RNAdGenerationBannerManager.m
//  RNAdGeneration
//
//  Created by chuross on 2018/05/28.
//  Copyright © 2018年 Facebook. All rights reserved.
//
#import "RNAdGenerationBannerManager.h"
#import "RNAdGenerationBanner.h"
#import <React/RCTUIManager.h>

@implementation RNAdGenerationBannerManager : RCTViewManager

- (UIView *)view
{
    return [RNAdGenerationBanner new];
}

- (void)load:(NSNumber *)node
{
    dispatch_async(dispatch_get_main_queue(), ^{
        RNAdGenerationBanner *bannerView = (RNAdGenerationBanner *)[self.bridge.uiManager viewForReactTag: node];
        [bannerView load];
    });
}

@end
