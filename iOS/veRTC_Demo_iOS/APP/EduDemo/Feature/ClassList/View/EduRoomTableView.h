//
//  EduRoomTableView.h
//  veRTC_Demo
//
//  Created by bytedance on 2021/5/18.
//  Copyright © 2021 . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EduActiveRoomCell.h"
#import "EduHistoryRoomCell.h"

@class EduRoomTableView;

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EduRoomType) {
    EduRoomTypeLecture,
    EduRoomTypeBreakout,
    EduRoomTypeHistory
};

@protocol EduRoomTableViewDelegate <NSObject>

- (void)EduRoomTableView:(EduRoomTableView *)EduRoomTableView didSelectRowAtIndexPath:(EduRoomModel *)model type:(EduRoomType)classType;

@end

@interface EduRoomTableView : UIView

@property (nonatomic, copy) NSArray *activeRooms;
@property (nonatomic, copy) NSArray *historyRooms;

@property (nonatomic, weak) id<EduRoomTableViewDelegate> delegate;


@end

NS_ASSUME_NONNULL_END
