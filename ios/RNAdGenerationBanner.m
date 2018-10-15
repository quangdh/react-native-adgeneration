//
//  RNAdGenerationBanner.m
//  RNAdGeneration
//
//  Created by chuross on 2018/05/28.
//  Copyright © 2018年 Facebook. All rights reserved.
//
#import "RNAdGenerationBanner.h"
#import <ADG/ADG.h>
#import "FBAudienceNetwork.h"

/**
 * Extension
 */
@interface RNAdGenerationBanner() <ADGManagerViewControllerDelegate>

@property (nonatomic) ADGManagerViewController *adg;

@end


/**
 * Implementation
 */
@implementation RNAdGenerationBanner : UIView

- (void)dealloc
{
    self.adg.delegate = nil;
    self.adg = nil;
}

- (void)load
{
    if (self.locationId == nil) {
        return;
    }
    if (self.bannerType == nil) {
        return;
    }
    if (self.bannerType == nil) {
        if(self.bannerWidth == nil || self.bannerHeight == nil) {
            return;
        }
    }
    
    NSMutableDictionary *params = @{}.mutableCopy;
    [params setObject:self.locationId forKey:@"locationid"];
    
    NSDictionary *event;
    if ([self.bannerType isEqualToString:@"sp"]) {
        [params setObject:@(kADG_AdType_Sp) forKey:@"adtype"];
        event = @{ @"width": @(kADGAdSize_Sp_Width), @"height": @(kADGAdSize_Sp_Height) };
    }
    if ([self.bannerType isEqualToString:@"rect"]) {
        [params setObject:@(kADG_AdType_Rect) forKey:@"adtype"];
        event = @{ @"width": @(kADGAdSize_Rect_Width), @"height": @(kADGAdSize_Rect_Height) };
    }
    if ([self.bannerType isEqualToString:@"large"]) {
        [params setObject:@(kADG_AdType_Large) forKey:@"adtype"];
        event = @{ @"width": @(kADGAdSize_Large_Width), @"height": @(kADGAdSize_Large_Height) };
    }
    if ([self.bannerType isEqualToString:@"tablet"]) {
        [params setObject:@(kADG_AdType_Tablet) forKey:@"adtype"];
        event = @{ @"width": @(kADGAdSize_Tablet_Width), @"height": @(kADGAdSize_Tablet_Height) };
    }
    if ([self.bannerType isEqualToString:@"free"]) {
        [params setObject:@(kADG_AdType_Free) forKey:@"adtype"];
        [params setObject:self.bannerHeight forKey:@"h"];
        [params setObject:self.bannerWidth forKey:@"w"];
        event = @{ @"width": self.bannerWidth, @"height": self.bannerHeight };
    }
    
    if (self.onMeasure) {
        self.onMeasure(event);
    }
    
    self.adg = [[ADGManagerViewController new] initWithAdParams:params adView:self];
    self.adg.delegate = self;
    self.adg.usePartsResponse = YES;
    [self.adg loadRequest];
}

- (void)ADGManagerViewControllerReceiveAd:(ADGManagerViewController *)adgManagerViewController
                        mediationNativeAd:(id)mediationNativeAd {
    NSLog(@"Received an ad.");
    
    UIView *nativeAdView;
    if ([mediationNativeAd isKindOfClass: [ADGNativeAd class]]) {
        UIView *adgNativeAdView = [self createNativeAdView:mediationNativeAd];
        //        [adgNativeAdView apply:(ADGNativeAd *)mediationNativeAd viewController:self];
        nativeAdView = adgNativeAdView;
    } else if ([mediationNativeAd isKindOfClass: [FBNativeAd class]]) {
        UIView *fbNativeAdView = [self createFBAdView:mediationNativeAd];
        //        [fbNativeAdView apply:(FBNativeAd *)mediationNativeAd
        //               viewController:self];
        nativeAdView = fbNativeAdView;
    }
    
    if (nativeAdView) {
        // ローテーション時に自動的にViewを削除します
        [adgManagerViewController setAutomaticallyRemoveOnReload:nativeAdView];
        
        [self addSubview:nativeAdView];
    }
}

- (void)ADGManagerViewControllerFailedToReceiveAd:(ADGManagerViewController *)adgManagerViewController code:(kADGErrorCode)code
{
    switch (code) {
        case kADGErrorCodeNeedConnection:
        case kADGErrorCodeExceedLimit:
        case kADGErrorCodeNoAd:
            break;
        default:
            [self.adg loadRequest];
            break;
    }
}

- (UIView *)createNativeAdView:(ADGNativeAd *)mediationNativeAd
{
    // セルの全体View
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0,0,[self.bannerWidth floatValue],[self.bannerHeight floatValue])];
    
    // 左部：イメージ、動画View
    UIView *imageView = [[UIView alloc] initWithFrame:CGRectMake(0,0,180,[self.bannerHeight floatValue])];
    UIImageView *imageIconView;
    if (mediationNativeAd.iconImage.url.length > 0) {
        NSURL *iconImageUrl = [NSURL URLWithString:mediationNativeAd.iconImage.url];
        NSData *iconImageData = [NSData dataWithContentsOfURL:iconImageUrl];
        imageIconView = [[UIImageView alloc]initWithFrame:CGRectMake(10,10,160,90)];
        imageIconView.image = [UIImage imageWithData:iconImageData];
        [imageView addSubview:imageIconView];
    }
    
    // 右部：タイトル、PR、スポンサー名View
    UIView *infoView = [[UIView alloc] initWithFrame:CGRectMake(imageView.frame.size.width,
                                                                0,
                                                                [self.bannerWidth floatValue] - imageView.frame.size.width,
                                                                [self.bannerHeight floatValue])];
    //// タイトルラベル
    UILabel *tLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                                10,
                                                                infoView.frame.size.width -20,
                                                                [self.bannerHeight floatValue])];
    [tLabel setNumberOfLines:3];
    [tLabel setFont:[UIFont systemFontOfSize:14]];
    tLabel.text = mediationNativeAd.title ? mediationNativeAd.title.text : @"";
    //// タイトルラベルの上寄せ
    CGRect rect = tLabel.frame;
    [tLabel sizeToFit];
    rect.size.height = CGRectGetHeight(tLabel.frame);
    tLabel.frame = rect;
    [infoView addSubview:tLabel];
    //// PRラベル
    UILabel *prLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,90,20,10)];
    [prLabel setFont:[UIFont systemFontOfSize:10]];
    [prLabel setText:@"PR"];
    [prLabel setTextAlignment:NSTextAlignmentCenter];
    [prLabel setTextColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0]];
    [prLabel.layer setBorderColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0].CGColor];
    [prLabel.layer setBorderWidth:1];
    [infoView addSubview:prLabel];
    //// スポンサー名ラベル
    float sponWidth = infoView.frame.size.width - 10;
    UILabel *sponLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,90,sponWidth,13)];
    [sponLabel setTextAlignment:NSTextAlignmentRight];
    [sponLabel setFont:[UIFont systemFontOfSize:10]];
    [sponLabel setTextColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0]];
    sponLabel.text = mediationNativeAd.sponsored.value.length > 0 ?
    [NSString stringWithFormat:@"%@",mediationNativeAd.sponsored.value] :
    @"sponsored";
    [infoView addSubview:sponLabel];
    
    [view addSubview:imageView];
    [view addSubview:infoView];
    
    [mediationNativeAd setTapEvent:self handler:nil];
    
    return view;
}

- (UIView *)createFBAdView:(FBNativeAd *)mediationFBAd
{
    // セルの全体View
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0,0,[self.bannerWidth floatValue],[self.bannerHeight floatValue])];
    
    // 左部：イメージ、動画View
    UIView *imageView = [[UIView alloc] initWithFrame:CGRectMake(0,0,180,[self.bannerHeight floatValue])];
    //// 画像アイコン
    FBAdIconView *imageIconView;
    imageIconView = [[FBAdIconView alloc]initWithFrame:CGRectMake(10,10,160,90)];
    [imageView addSubview:imageIconView];
    //// 動画
    UIView  *FBMediaViewCon = [[UIView alloc] initWithFrame:CGRectMake(10,10,160,90)];
    FBMediaView *mediaView = [[FBMediaView alloc] initWithFrame:CGRectMake(0,0,160,90)];
    [FBMediaViewCon addSubview:mediaView];
    //// FANのiマーク
    FBAdChoicesView *adChoices = [[FBAdChoicesView alloc] initWithNativeAd:mediationFBAd expandable:YES];
    adChoices.backgroundShown = NO;
    [FBMediaViewCon addSubview:adChoices];
    [adChoices updateFrameFromSuperview:UIRectCornerTopRight];
    [imageView addSubview:FBMediaViewCon];
    
    // 右部：タイトル、PR、スポンサー名View
    UIView *infoView = [[UIView alloc] initWithFrame:CGRectMake(imageView.frame.size.width,
                                                                0,
                                                                [self.bannerWidth floatValue] - imageView.frame.size.width,
                                                                [self.bannerHeight floatValue])];
    //// タイトルラベル
    UILabel *tLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                                10,
                                                                infoView.frame.size.width -20,
                                                                [self.bannerHeight floatValue])];
    [tLabel setNumberOfLines:3];
    [tLabel setFont:[UIFont systemFontOfSize:14]];
    tLabel.text = mediationFBAd.headline ? mediationFBAd.headline : @"";
    //// タイトルラベルの上寄せ
    CGRect rect = tLabel.frame;
    [tLabel sizeToFit];
    rect.size.height = CGRectGetHeight(tLabel.frame);
    tLabel.frame = rect;
    [infoView addSubview:tLabel];
    //// PRラベル
    UILabel *prLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,90,20,10)];
    [prLabel setFont:[UIFont systemFontOfSize:10]];
    [prLabel setText:@"PR"];
    [prLabel setTextAlignment:NSTextAlignmentCenter];
    [prLabel setTextColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0]];
    [prLabel.layer setBorderColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0].CGColor];
    [prLabel.layer setBorderWidth:1];
    [infoView addSubview:prLabel];
    //// スポンサー名ラベル
    float sponWidth = infoView.frame.size.width - 10;
    UILabel *sponLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,90,sponWidth,13)];
    [sponLabel setTextAlignment:NSTextAlignmentRight];
    [sponLabel setFont:[UIFont systemFontOfSize:10]];
    [sponLabel setTextColor:[UIColor colorWithRed:159/255.0 green:159/255.0 blue:159/255.0 alpha:1.0]];
    sponLabel.text = mediationFBAd.socialContext.length > 0 ?
    [NSString stringWithFormat:@"%@",mediationFBAd.socialContext] :
    @"sponsored";
    [infoView addSubview:sponLabel];
    
    [view addSubview:imageView];
    [view addSubview:infoView];
    
    // クリック領域
    NSArray *clickableViews = @[imageView, infoView];
    [mediationFBAd registerViewForInteraction:self
                                    mediaView:mediaView
                                     iconView:imageIconView
                               viewController:nil
                               clickableViews:clickableViews];
    
    return view;
}
@end
