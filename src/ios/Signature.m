//
//  Signature.m
//
//  Created by Khun Sangkhim on 4/27/17.
//
//

#import "Signature.h"

#define degreesToRadian(x) (M_PI * (x) / 180.0)
#define gabLineFromImage 10
#define gabTitleFromImage 60

@implementation Signature

- (void)openSignature:(CDVInvokedUrlCommand*)command
{   
    SignViewController* sign = [[SignViewController alloc]initWithPath:@"/Signature" completion:^(id jsonObj) {
        [self.commandDelegate runInBackground:^{
            if([[jsonObj objectForKey:@"result"] boolValue]) {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[jsonObj valueForKey:@"sign_data"]];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }else{
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"User has been canceled the signature."];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }];
    }];
    
    [self.viewController presentViewController:sign animated:YES completion:nil];
}

@end



// SignView
@implementation BezierSignView
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.multipleTouchEnabled = YES;
        staticTouch = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)dealloc {
}

/**
 * 터치 시작 이벤트를 받는 메소드
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 터치 객체
 * @param 이벤트 객체
 * @return   void
 */
- (void) touchesBegan:(NSSet *) touches withEvent:(UIEvent *) event
{
    NSMutableDictionary* touchPaths = [[NSMutableDictionary alloc]initWithCapacity:1];
    
    for (UITouch *touch in touches)
    {
        NSString *key = [NSString stringWithFormat:@"%p", touch];
        CGPoint pt = [touch locationInView:self];
        
        UIBezierPath *path = [UIBezierPath bezierPath];
        path.lineWidth = 4;
        [path moveToPoint:pt];
        
        [touchPaths setObject:path forKey:key];
    }
    [staticTouch addEntriesFromDictionary:[touchPaths copy]];
}

/**
 * 터치 이동 이벤트를 받는 메소드
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 터치 객체
 * @param 이벤트 객체
 * @return   void
 */
- (void) touchesMoved:(NSSet *) touches withEvent:(UIEvent *) event
{
    for (UITouch *touch in touches)
    {
        NSString *key = [NSString stringWithFormat:@"%p", touch];
        UIBezierPath *path = [staticTouch objectForKey:key];
        if (!path) break;
        
        CGPoint pt = [touch locationInView:self];
        [path addLineToPoint:pt];
    }
    
    [self setNeedsDisplay];
}

/**
 * 터치 완료 이벤트를 받는 메소드
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 터치 객체
 * @param 이벤트 객체
 * @return   void
 */
- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    // Move all completed touches out of memory named keys
    for (UITouch *touch in touches)
    {
        NSString *key = [NSString stringWithFormat:@"%p", touch];
        UIBezierPath *path = [staticTouch objectForKey:key];
        [staticTouch removeObjectForKey:key];
        [staticTouch setObject:path forKey:[self nonce]];
    }
    
    [self setNeedsDisplay];
}

/**
 * 터치 취소 이벤트를 받는 메소드
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 터치 객체
 * @param 이벤트 객체
 * @return   void
 */
- (void) touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self touchesEnded:touches withEvent:event];
}

/**
 * 지우기
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param
 * @return   void
 */
- (void) clear
{
    [staticTouch removeAllObjects];
    [self setNeedsDisplay];
}

/**
 * 터치해서 만들어진 값들을 도형으로 나타냄
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param
 * @return   void
 */
- (void) drawRect:(CGRect)rect
{
    [[UIColor colorWithRed:0 green:0 blue:0 alpha:1.0f] set];
    for (UIBezierPath *path in [staticTouch allValues])
    [path stroke];
    
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

/**
 * UUID 값 가져오는 부분
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param
 * @return   UUID 값
 */
-(NSString*) nonce
{
    CFUUIDRef theUUID = CFUUIDCreate(NULL);
    NSString *nonceString = (__bridge_transfer NSString *)CFUUIDCreateString(NULL, theUUID);
    CFRelease(theUUID);
    return nonceString;
}
@end

@interface SignViewController(){
    BezierSignView *drawView;
    NSString* savePath;
}

@property (nonatomic, copy) void(^completion)(id result);
    
@end

// SignViewController
@implementation SignViewController
// The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.

/**
 * 저장 경로를 포함하여 서명뷰 실행
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 저장 경로
 * @return   void
 */
- (id) initWithPath:(NSString*)path;
{
    if( self = [super init] ) {
        savePath = [NSString stringWithString:path];
    }
    return self;
}
    
/**
 * 저장 경로를 포함하여 서명뷰 실행 (완료되면 completion을 통해서 완료된 후 작업이 가능 하도록 제공하는 메소드)
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 서명 저장 경로
 * @param 완료 블럭
 * @return   void
 */
- (id) initWithPath:(NSString*)path completion:(void(^)(id jsonObj))completion{
    if( self = [super init] ) {
        savePath = [NSString stringWithString:path];
        self.completion = completion;
    }
    return self;
}
    
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
    
    
}
    
    
    
    
- (void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    //    [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationLandscapeLeft animated:NO];
    //    NSLog(@"%@",NSStringFromCGRect(self.view.bounds) );
    
    
    self.title = @"서명";
    self.navigationController.navigationBarHidden = YES;
    
    UIImageView* imgView = [[UIImageView alloc]initWithFrame:CGRectMake(self.view.bounds.origin.x,self.view.bounds.origin.y, self.view.bounds.size.width,self.view.bounds.size.height)];
    imgView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    imgView.autoresizingMask = self.view.autoresizingMask;
    
    imgView.image = [UIImage imageNamed:@"bizmob_sign_bg.9"];
    [self.view addSubview:imgView];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithTitle:@"Clear" style:UIBarButtonItemStylePlain target:self action:@selector(onCancel:)];
    
    // 하단 툴바
    UIToolbar* toolbar = [[UIToolbar alloc]initWithFrame:CGRectMake(self.view.frame.origin.x, 8, self.view.frame.size.width, 44)];
    
    // set transparent toolbar
    [toolbar setBackgroundImage:[UIImage new]
             forToolbarPosition:UIBarPositionAny
                     barMetrics:UIBarMetricsDefault];
    [toolbar setShadowImage:[UIImage new]
         forToolbarPosition:UIBarPositionAny];
    
    toolbar.userInteractionEnabled = YES;
    toolbar.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    NSMutableArray *barBtnItems = [[NSMutableArray alloc] init];
    
    //TODO: global localization
    UIBarButtonItem *flexible = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    
//    UIBarButtonItem *clearBtn = [[UIBarButtonItem alloc]initWithTitle:@"Clear" style:UIBarButtonItemStylePlain target:self action:@selector(onClear:)];
    
    UIButton *saveBtnCustom = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 51, 34)];
    [saveBtnCustom setBackgroundImage:[UIImage imageNamed:@"bizmob_sign_ok_btn"] forState:UIControlStateNormal];
    [saveBtnCustom addTarget:self action:@selector(onSave:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *saveBtn = [[UIBarButtonItem alloc]initWithCustomView:saveBtnCustom];
    
    UIButton *cnacelBtnCustom = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 51, 34)];
    [cnacelBtnCustom setBackgroundImage:[UIImage imageNamed:@"bizmob_sign_cancel_btn"] forState:UIControlStateNormal];
    [cnacelBtnCustom addTarget:self action:@selector(onCancel:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *cnacelBtn = [[UIBarButtonItem alloc]initWithCustomView:cnacelBtnCustom];
    
    [barBtnItems addObject:flexible];
//    [barBtnItems addObject:clearBtn];
    [barBtnItems addObject:saveBtn];
    [barBtnItems addObject:cnacelBtn];
    toolbar.items = barBtnItems;
    [self.view addSubview:toolbar];
    
    //  Paint View
    NSInteger margin = gabLineFromImage;
    NSInteger topMargin = gabTitleFromImage;
    drawView = [[BezierSignView alloc]initWithFrame:CGRectMake(self.view.bounds.origin.x+margin,self.view.bounds.origin.y+topMargin, self.view.bounds.size.width-(2 * margin),self.view.bounds.size.height - (topMargin + margin))];
    drawView.backgroundColor = [UIColor whiteColor];
    drawView.multipleTouchEnabled = YES;
    drawView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:drawView];
}
    
    
- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc. that aren't in use.
}
    
- (void)viewDidUnload {
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}
    
    
- (void)dealloc {
}
    
    
#pragma mark - Orientation
-(BOOL) shouldAutorotate
{
    return YES;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskLandscape;
}

/**
 * 서명 취소 버튼 이벤트
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 요청 객체
 * @return   void
 */
-(void) onCancel:(id)sender
{
    NSMutableDictionary* resultJson = [[NSMutableDictionary alloc] initWithCapacity:3];
    [resultJson setObject:[NSNumber numberWithBool:NO] forKey:@"result"];
    [resultJson setObject:@"" forKey:@"sign_data"];
    [resultJson setObject:@"" forKey:@"file_path"];
    
    if (self.completion) {
        self.completion(resultJson);
    }
    
    [self dismissViewControllerAnimated:NO completion:nil];
}

/**
 * 서명 지우기 버튼 이벤트
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param
 * @return   void
 */
-(void) onClear:(id)sender
{
    [drawView clear];
}

/**
 * 서명 저장 버튼 이벤트
 *
 * <pre>
 * 수정이력
 * **********************************************************************************************************************************
 *  수정일                                    이름                               변경내용
 * **********************************************************************************************************************************
 *  2016-10-11                         이동훈                             최초 작성
 *
 *</pre>
 *
 * @param 요청 객체
 * @return   void
 */
-(void) onSave:(id)sender
{
    CGSize contextSize = drawView.bounds.size;
    UIGraphicsBeginImageContext(contextSize);
    [drawView.layer renderInContext:UIGraphicsGetCurrentContext()];
    
    UIImage * img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    CGSize newSize = CGSizeMake(128.0f, 64.0f);
    UIGraphicsBeginImageContext(newSize);
    [img drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *resizeImg = UIGraphicsGetImageFromCurrentImageContext();
    NSData* imgData = UIImageJPEGRepresentation(resizeImg,1.0);
    UIGraphicsEndImageContext();
    
    // save file to /Documents
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *path = [documentsDirectory stringByAppendingPathComponent:savePath];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil]; //Create folder
    }
    NSString *imageURL = [path stringByAppendingFormat:@"/signature.jpg"];
    [imgData writeToFile:imageURL atomically:YES];
    
    // save file to Photo Gallery
    UIImageWriteToSavedPhotosAlbum(resizeImg, nil, nil, nil);
    
    NSMutableDictionary* resultJson = [[NSMutableDictionary alloc] initWithCapacity:3];
    NSString* strBase64 = [imgData base64Encoding];
    [resultJson setObject:[NSNumber numberWithBool:YES] forKey:@"result"];
    [resultJson setObject:strBase64 forKey:@"sign_data"];
    [resultJson setObject:savePath forKey:@"file_path"];
    
    if (self.completion) {
        self.completion(resultJson);
    }
    
    [self dismissViewControllerAnimated:NO completion:nil];
}
@end
