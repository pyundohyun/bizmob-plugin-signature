//
//  Signature.h
//
//  Created by Khun Sangkhim on 4/27/17.
//
//

#import <Cordova/CDV.h>

@interface Signature : CDVPlugin
{
    // Member variables go here.
}

- (void)openSignature:(CDVInvokedUrlCommand*)command;
    
@end

/**
 *
 * 01.클래스 설명 : 서명 뷰에서 서명 그리는 영역 뷰
 * 02.제품구분 : Native Container<br>
 * 03.기능(콤퍼넌트) 명 : 서명 그리는 영역 <br>
 * 04.관련 API/화면/서비스 : UIView <br>
 * 05.수정이력<br>
 * <pre>
 * **********************************************************************************************************************************
 *  수정일                                                이름                          변경 내용
 * **********************************************************************************************************************************
 *  2016-10-11                                     이동훈                         최초 작성
 * **********************************************************************************************************************************
 *</pre>
 *
 * @author 이동훈
 * @version 1.0
 *
 */
@interface BezierSignView : UIView
{
    NSMutableDictionary *staticTouch;
}

- (void) clear;

-(NSString*) nonce;
    
@end

/**
*
* 01.클래스 설명 : 서명 뷰
* 02.제품구분 : Native Container<br>
* 03.기능(콤퍼넌트) 명 : 서명 <br>
* 04.관련 API/화면/서비스 : UIViewController <br>
* 05.수정이력<br>
* <pre>
* **********************************************************************************************************************************
*  수정일                                                이름                          변경 내용
* **********************************************************************************************************************************
*  2016-10-11                                     이동훈                         최초 작성
* **********************************************************************************************************************************
*</pre>
*
* @author 이동훈
* @version 1.0
*
*/
@interface SignViewController : UIViewController {
    // UIBarButtonItem* btnClear;
}

- (id) initWithPath:(NSString*)path;

- (id) initWithPath:(NSString*)path completion:(void(^)(id jsonObj))completion;

@end
