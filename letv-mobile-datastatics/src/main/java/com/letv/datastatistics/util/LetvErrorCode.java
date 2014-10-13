package com.letv.datastatistics.util;

public class LetvErrorCode {

	/**
	 * 接口数据异常：按功能模块的接口分类
	 * */
	public final static String VIDEO_NOT_HAD = "00010";                      // 视频下线或者不存在
	public final static String VIDEO_FOREIGN_SHIELD = "00011";               // 海外屏蔽
	public final static String VIDEO_COPYRIGHT_EXPIRES = "00014";            // 版权到期
	public final static String VIDEO_NOT_NOLINE = "00013";					 // 未上线					
	public final static String VIDEO_NOT_FOUND = "00480";                    // 视频无法找到
	public final static String VIDEO_PLAY_TIMEOUT = "00481";                 // 播放视频超时
	public final static String VIDEO_PLAY_NOT_LEGITIMATE = "00483";          // 播放的视频文件不合法   
	public final static String VIDEO_PLAY_OTHER_ERROR = "00489";             // 播放视频文件其它错误
	public final static String VIDEO_DISPATCH_NETWORK_ERROR = "00470";       // 调度,网络错误
	public final static String VIDEO_DISPATCH_TIMEOUT_ERROR = "00471";       // 调度,超时错误
	public final static String VIDEO_DISPATCH_DATA_PRASE_ERROR = "00473";    // 调度,数据解析错误
	public final static String VIDEO_DISPATCH_DATA_OTHER_ERROR = "00474";    // 调度,数据其它错误
	public final static String VIDEO_DISPATCH_OTHER_ERROR = "00479";         // 调度,其它错误
	public final static String NEW_MIZHI_INTERFACE_FAIL = "00550";           // 新媒资接口访问失败
	public final static String NEW_MIZHI_INTERFACE_TIMEOUT = "00551";        // 新媒资接口访问超时
	public final static String NEW_MIZHI_INTERFACE_CONTENT_NOT_LEGITIMATE = "00553";   // 新媒资接口访问内容不合法
	public final static String NEW_MIZHI_INTERFACE_OTHER_ERROR = "00559";    // 新媒资接口访问其它错误
	public final static String VIDEO_OTHER_ERROR = "00999";                  // 其它异常
	
	
	/**
	 * 首页（01）
	 * */
	public final static String LTURLModule_NewIndex = "01001";                           //大媒资首页，开机图片
	public final static String LTURLModule_Recommend = "01002";							 //首页(推荐页)
	public final static String LTURLModule_Recommend_Personalized = "01003";             //首页(个性化推荐)
	public final static String LTURLModule_Recommend_APP_INDEX = "01004";				 //首页推荐App
	public final static String LTURLModule_Recommend_APP_POP = "01005";					 //首页弹窗推荐
	public final static String LTURLModule_Recommend_Live = "01006";					 //直播数据
	
	
	/**
	 * 专题/排行（02）
	 * */
	public final static String LTURLModule_Chart = "02001";								//排行榜
	public final static String LTURLModule_Sub_Chart = "02002";							//具体频道的排行榜
	public final static String LTURLModule_Subject = "02003";							//专题
	public final static String LTURLModule_Subject_Detail = "02004";					//专题——数据包

	
	/**
	 * 频道（03）
	 * */
	public final static String LTURLModule_Channel_List = "03001";								//频道列表
	public final static String LTURLModule_Channel_Index = "03002";								//频道首页
	public final static String LTURLModule_Channel_Filter = "03003";							//频道筛选
	public final static String LTURLModule_Channel_Type = "03004";								//频道分类
	public final static String LTURLModule_Channel_Album = "03005";								//频道列表——大媒资专辑
	public final static String LTURLModule_Channel_Video = "03006";								//频道列表——大媒资视频
	public final static String LTURLModule_Channel_VRS_Album = "03007";							//频道列表——vrs专辑
	public final static String LTURLModule_Channel_VRS_Video = "03008";							//频道列表——vrs单视频
	public final static String LTURLModule_Channel_VIP_Album = "03009";							//频道列表——vrs单视频
	
	
	/**
	 * 详情（04）
	 * */
	public final static String LTURLModule_Detail_VRS_Album = "04001";								//详情——vrs专辑
	public final static String LTURLModule_Detail_VRS_Video = "04002";								//详情——vrs单视频
	public final static String LTURLModule_Detail_VRS_Vid = "04003";								//详情——通过vid获取专辑或视频详情
	public final static String LTURLModule_Album_VideoList = "04004";								//详情——通过vid获取专辑或视频详情
	public final static String LTURLModule_Album_DetailAndVideoList = "04005";						//专辑详情和视频列表——专辑详情和视频列表
	public final static String LTURLModule_Video_FileInfo = "04006";								//视频文件信息
	public final static String LTURLMOdule_Video_UrlParse = "04007";								//防盗链url调度
	public final static String LTURLModule_Album_Pay = "04008";										//专辑付费详情
	
	
	/**
	 * 搜索(05)
	 * */
	public final static String LTURLModule_Search_Hotword = "05001";								//搜索热词
	public final static String LTURLModule_Search_Related = "05002";								//搜索联想词
	public final static String LTURLModule_Search_Init = "05003";									//搜索初始化数据
	public final static String LTURLModule_Search_Mixed_Search = "05004";							//搜索-混合搜索
	public final static String LTURLModule_Search_Star_Works = "05005";								//搜索明星的作品
	public final static String LTURLModule_Search_Star_Album = "05006";								//搜索明星专辑
	public final static String LTURLModule_Search_Star_Video = "05007";								//搜明明星单视频
	public final static String LTURLModule_Search_Video_Source = "05008";							//视频的数据来源(乐视、优酷等)
	public final static String LTURLModule_Search_OuterNet_VideoList = "05009";						//外网专辑视频列表
	public final static String LTURLModule_Search_Recommend = "05010";								//搜索推荐
	
	
	/**
	 * 直播(06)
	 * */
	public final static String LTURLModule_Live_List = "06001";								        //直播电视台
	public final static String LTURLModule_Live_LunboList = "06002";								//轮播台
	public final static String LTURLModule_Live_WeishiList = "06003";								//卫视台
	public final static String LTURLModule_Live_LiveList = "06004";									//直播厅节目单
	public final static String LTURLModule_Live_ChannelBill = "06005";								//轮播台，卫视台节目单
	public final static String LTURLModule_Live_Bill = "06006";										//直播节目单
	public final static String LTURLModule_Live_Focus = "06007";									//直播焦点图
	public final static String LTURLModule_Live_PlayingBill = "06008";								//正在直播的节目单
	public final static String LTURLModule_Live_ChannelInfo = "06009";								//
	public final static String LTURLModule_Live_SeverTime = "06010";								//获取服务器时间
	public final static String LTURLModule_Live_LiveTm = "06011";									//获取直播过期时间戳	
	public final static String LTURLModule_Live_CanPlay = "06012";									//是否可以播放
	
	
	/**
	 * 直播预定（07）
	 * */
	public final static String LTURLModule_BookLive_Add = "07001";								    //添加直播预定
	public final static String LTURLModule_BookLive_Del = "07002";								    //取消直播预定
	public final static String LTURLModule_BookLive_Close = "07003";								//关闭直播预定
	public final static String LTURLModule_BookLive_Open = "07004";								    //打开直播预定
	public final static String LTURLModule_BookLive_Clean = "07005";								//清空直播预定
	
	
	/**
	 * 追剧（08）
	 * */
	public final static String LTURLModule_Push_Add = "08001";								    //添加追剧
	public final static String LTURLModule_Push_Del = "08002";								    //取消追剧
	public final static String LTURLModule_Push_Close = "08003";								//关闭追剧
	public final static String LTURLModule_Push_Open = "08004";								    //打开追剧
	public final static String LTURLModule_Push_Clean = "08005";								//清空追剧
	
	
	/**
	 * app（09）
	 * */
	public final static String LTURLModule_ApiStatus = "09001";								    //接口初始化状态、客户端设备信息上报、升级、广告控制、精品推荐控制接口
	public final static String LTURLModule_IOSDevice = "09002";								    //设备统计
	public final static String LTURLModule_Audit = "09003";								    	//审核状态
	public final static String LTURLModule_About = "09004";								   		//关于我们
	public final static String LTURLModule_Alert_Info = "09005";								//提示语
	public final static String LTURLModule_Feedback = "09006";								    //意见反馈
	public final static String LTURLModule_Upgrade = "09007";								    //升级
	public final static String LTURLModule_ErrorUpload = "09008";								//错误上报
	
	
	/**
	 * 个人中心（10）
	 * */
	public final static String LTURLModule_UC_ThirdPartyLogin = "10001";						//第三方登录
	public final static String LTURLModule_UC_Login = "10002";									//登录
	public final static String LTURLModule_UC_Register = "10003";								//注册
	public final static String LTURLModule_UC_MovieAvaiable = "10004";							//付费片子是否可播
	public final static String LTURLModule_UC_GenerateOrderID = "10005";						//产生订单ID
	public final static String LTURLModule_UC_Pay = "10006";									//支付
	public final static String LTURLModule_UC_PayResult = "10007";								//轮询支付结果
	public final static String LTURLModule_UC_QueryLePoint = "10008";							//查询乐点
	public final static String LTURLModule_UC_QueryVIP = "10009";								//是否VIP
	public final static String LTURLModule_UC_UserInfo = "10010";								//获取用户信息
	public final static String LTURLModule_UC_CheckMobileExists = "10011";						//手机号是否已注册
	public final static String LTURLModule_UC_CheckEmailExists = "10012";						//邮箱是否已注册
	public final static String LTURLModule_UC_SMSMobile = "10013";								//发送验证短信
	public final static String LTURLModule_UC_ChangeEmail = "10014";							//修改邮箱
	public final static String LTURLModule_UC_ChangeMobile = "10015";							//修改邮箱
	public final static String LTURLModule_UC_ChangePassword = "10016";							//修改密码
	public final static String LTURLModule_UC_Consume = "10017";								//消费记录
	public final static String LTURLModule_UC_Recharge = "10018";								//充值记录
	public final static String LTURLModule_UC_VERTIFY_TOKEN = "10019";							//检测token是否过期
	public final static String LTURLModule_UC_SendBackPwdEmail = "10020";						//发送邮件找回密码
	public final static String LTURLModule_UC_SearchVoucher = "10021";							//查询观影券
	public final static String LTURLModule_UC_UseVoucher = "10022";								//使用观影券
	public final static String LTURLModule_UC_VoucherList = "10023";							//观影券列表
	
	/**
	 * IAP（11）
	 * */
	public final static String LTURLModule_IAP_Product = "11001";						//获取产品标识
	public final static String LTURLModule_IAP_OrderID = "11002";						//订单申请
	public final static String LTURLModule_IAP_OrderID_Test = "11003";					//订单申请，审核期间用
	public final static String LTURLModule_IAP_Receipt = "11004";						//订单回调
	public final static String LTURLModule_IAP_Receipt_Test = "11005";					//订单回调，审核期间用
	
	/**
	 * 播放记录云同步（12）
	 * */
	public final static String LTURLModule_Cloud_GetAll = "12001";						//获取播放记录
	public final static String LTURLModule_Cloud_GetFirst = "12002";					//获取第一条播放记录
	public final static String LTURLModule_Cloud_SubmitSingle = "12003";				//提交播放记录
	public final static String LTURLModule_Cloud_SubmitMore = "12004";					//批量提交播放记录,POST方式
	public final static String LTURLModule_Cloud_Delete = "12005";						//删除播放记录
	public final static String LTURLModule_Cloud_GetPoint = "12006";					//获取播放记录时间点
	public final static String LTURLModule_Cloud_Search = "12007";						//搜索播放记录
	public final static String LTURLModule_Cloud_PageSize = "12008";					//获取第一页指定条数的数据
	
	
	/**
	 * 追剧收藏云同步（13）
	 * */
	public final static String LTURLModule_Cloud_GetAllFavrite = "13001";						//获取所有收藏记录
	public final static String LTURLModule_Cloud_DeleteFavrite = "13002";						//删除收藏记录
	public final static String LTURLModule_Cloud_SubmitFavrite = "13003";						//上传收藏记录
	public final static String LTURLModule_Cloud_SubmitFavriteMore = "13004";					//批量上传收藏记录
	
	
	/**
	 * 广告（14）
	 * */
	public final static String LTURLModule_Ad_Config = "14001";							//广告配置
	public final static String LTURLModule_Ad_Combine = "14002";						//广告拼接
	
	
	/**
	 * 二维码（15）
	 * */
	public final static String LTURLModule_QRCode_Submit = "15001";							//二维码提交验证
	
	
	/**
	 * 摇一摇（16）
	 * */
	public final static String LTURLModule_Shake_Add = "16001";									//提交记录
	public final static String LTURLModule_Shake_Get = "16002";									//获取记录
	public final static String LTURLModule_IndexRecommend = "16003";							//首页推荐
	public final static String LTURLModule_DetailRecommend = "16004";							//详情推荐
	public final static String LTURLModule_Share_PlayUrl = "16005";								//动态获取分享播放地址
	public final static String LTURLModule_Recommend_APP = "16006";								//精品应用
	public final static String LTURLModule_Report_ASIdentifier = "16007";						//上报广告标示符
	public final static String LTURLModule_Get_TimeStamp = "16008";								//获取服务器当前时间戳
	public final static String LTURLModule_Get_Promotion_Info = "16009";						//获取产品推广信息
	public final static String LTURLModule_Get_VIP_Video_List = "16010";						//获取会员视频列表
	public final static String LTURLModule_Get_VIP_Privilege_Info = "16011";					//获取VIP特权信息
	public final static String LTURLModule_UploadErrorLogFile = "16012";						//错误日志文件上报
	
	
	/**
	 * 评论（17）
	 * */
	public final static String LTURLModule_Comment_List = "17001";									//获取评论列表
	public final static String LTURLModule_Comment_Add = "17002";									//添加评论
	public final static String LTURLModule_Comment_Reply = "17003";									//添加评论
	public final static String LTURLModule_Comment_Reply_List = "17004";							//评论列表
	public final static String LTURLModule_Comment_Like = "17005";									//喜欢
	public final static String LTURLModule_Comment_Unlike = "17006";								//取消喜欢
	
	
	/**
	 * 积分商城（18）
	 * */
	public final static String LTURLModule_Integretion_Rules = "18001";									//获取积分规则
	public final static String LTURLModule_Integretion_Task = "18002";									//积分任务
	public final static String LTURLModule_Integretion_Action = "18003";								//添加积分
	
	
	public final static String VIDEO_DOWNLOAD_CAN_NOT_PLAY = "19001";								//下载后不能够播放
	public final static String VIDEO_CRASH_NOT_DOWNLOAD = "19002";									//下载稳定性——崩溃导致无法下载
	public final static String VIDEO_DOWNLOAD_LINK_ERROR = "19003";									//下载链接错误
	
	
	public final static String VIDEO_CRASH_ERROR = "20001";											//crash错误
	
	
	
}
