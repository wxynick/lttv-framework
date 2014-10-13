package com.letv.datastatistics.util;

public final class DataConstant {

	private DataConstant() {
	}

	public static final String STAT_VERSION = "2.0";// 1.6

	/** 信息统计 登录 */
	public static final String STAT_LOGIN_URL_TEST = "http://dev.dc.letv.com/m/l?";
	public static final String STAT_LOGIN_URL = "http://dc.letv.com/m/l?";
	public static final String STAT_LOCAL_LOGIN_URL = "http://dc.app.m.letv.com/m/l?";
	// public static final String STAT_LOGIN_URL_TEST =
	// "http://test.m.letv.com/android/mindex.php?mod=minfo&ctl=applog&act=login&";

	/** 视频播放结束数据的发送地址 */
	public static final String STAT_VIDEOCLOSED_URL_TEST = "http://dev.dc.letv.com/m/p?";
	public static final String STAT_VIDEOCLOSED_URL = "http://dc.letv.com/m/p?";
	public static final String STAT_LOCAL_VIDEOCLOSED_URL = "http://dc.app.m.letv.com/m/p?";
	// public static final String STAT_VIDEOCLOSED_URL_TEST =
	// "http://test.m.letv.com/android/mindex.php?mod=minfo&ctl=applog&act=play&";

	/** 贴片广告的发送地址 */
	public static final String STAT_AD_URL_TEST = "http://dev.dc.letv.com/m/c?";
	public static final String STAT_AD_URL = "http://dc.letv.com/m/c?";
	public static final String STAT_LOCAL_AD_URL = "http://dc.app.m.letv.com/m/c?";

	/** 动作码数据的发送地址 */
	public static final String STAT_ACTIONCODE_URL_TEST = "http://dev.dc.letv.com/m/a?";
	public static final String STAT_ACTIONCODE_URL = "http://dc.letv.com/m/a?";
	public static final String STAT_LOCAL_ACTIONCODE_URL = "http://dc.app.m.letv.com/m/a?";
	// public static final String STAT_ACTIONCODE_URL_TEST =
	// "http://test.m.letv.com/android/mindex.php?mod=minfo&ctl=applog&act=action&";

	/** 下载日志的发送地址 */
	public static final String STAT_DOWNLOADLOG_URL_TEST = "http://dev.dc.letv.com/m/d?";
	public static final String STAT_DOWNLOADLOG_URL = "http://dc.letv.com/m/d?";
	public static final String STAT_LOCAL_DOWNLOADLOG_URL = "http://dc.app.m.letv.com/m/d?";
	// public static final String STAT_DOWNLOADLOG_URL_TEST =
	// "http://test.m.letv.com/android/mindex.php?mod=minfo&ctl=applog&act=download&";

	// ====================================================================================================
	// ====================================================================================================
	// ====================================================================================================
	/** 精品换量的发送地址 */
	public static final String STAT_RECOMMEND_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/op/?";
	public static final String STAT_RECOMMEND_URL = "http://dc.letv.com/op/?";
	public static final String STAT_LOCAL_RECOMMEND_URL = "http://dc.app.m.letv.com/op/?";

	/** 播放日志发送地址 */
	public static final String STAT_PLAYER_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/pl/?";
	public static final String STAT_PLAYER_URL = "http://dc.letv.com/pl/?";
	public static final String STAT_LOCAL_PLAYER_URL = "http://dc.app.m.letv.com/pl/?";

	/** 动作日志发送地址 */
	public static final String STAT_ACTION_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/op/?";
	public static final String STAT_ACTION_URL = "http://dc.letv.com/op/?";
	public static final String STAT_LOCAL_ACTION_URL = "http://dc.app.m.letv.com/op/?";

	/** PV日志发送地址 */
	public static final String STAT_PV_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/pgv/?";
	public static final String STAT_PV_URL = "http://dc.letv.com/pgv/?";
	public static final String STAT_LOCAL_PV_URL = "http://dc.app.m.letv.com/pgv/?";

	/** ENV日志发送地址 */
	public static final String STAT_ENV_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/env/?";
	public static final String STAT_ENV_URL = "http://dc.letv.com/env/?";
	public static final String STAT_LOCAL_ENV_URL = "http://dc.app.m.letv.com/env/?";

	/** 信息统计 登录 */
	public static final String STAT_LOGIN2_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/lg/?";
	public static final String STAT_LOGIN2_URL = "http://dc.letv.com/lg/?";
	public static final String STAT_LOCAL_LOGIN2_URL = "http://dc.app.m.letv.com/lg/?";

	/** query 搜索日志包括搜索关键词，用户信息和搜索结果信息 */
	public static final String STAT_QUERY_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/qy/?";
	public static final String STAT_QUERY_URL = "http://dc.letv.com/qy/?";
	public static final String STAT_LOCAL_QUERY_URL = "http://dc.app.m.letv.com/qy/?";

	/** ad 页面广告信息 */
	public static final String STAT_AD2_URL_TEST = "http://dev.dc.letv.com/pad/?";
	public static final String STAT_AD2_URL = "http://dc.letv.com/pad/?";
	public static final String STAT_LOCAL_AD2_URL = "http://dc.app.m.letv.com/pad/?";

	/** error 信息 */
	public static final String STAT_ERROR_URL_TEST = "http://develop.bigdata.leshiren.com/0af9/er/?";
	public static final String STAT_ERROR_URL = "http://dc.letv.com/er/?";
	public static final String STAT_LOCAL_ERROR_URL = "http://dc.app.m.letv.com/er/?";
	
	
	
	// ====================================================================================================
	// ====================================================================================================
	// ====================================================================================================
	/** 接口初始化状态、客户端设备信息上报、升级接口 */
	public static final String UPLOAD_CLIENTDATA_URL = "http://dynamic.app.m.letv.com/android/dynamic.php?mod=minfo&ctl=apistatus&act=index&";
	public static final String UPLOAD_CLIENTDATA_URL_TEST = "http://dynamic.app.m.letv.com/android/dynamic.php?mod=minfo&ctl=apistatus&act=index&";

	// public static final String UPLOAD_CLIENTDATA_URL =
	// "http://test.m.letv.com/android/dynamic.php?mod=minfo&ctl=apistatus&act=index&";
	// public static final String UPLOAD_CLIENTDATA_URL_TEST =
	// "http://test2.m.letv.com/android/dynamic.php?mod=minfo&ctl=apistatus&act=index&";

	public static final class PAGE {

		/**
		 * 通用
		 * */
		public static final String PUBLIC = "0";
		/**
		 * 导航
		 * */
		public static final String NAVIGATION = "1";
		/**
		 * 首页
		 * */
		public static final String HOME = "2";
		/**
		 * 频道
		 * */
		public static final String CHANNLE = "3";
		/**
		 * 播放器
		 * */
		public static final String PLAY = "4";
		/**
		 * 搜索
		 * */
		public static final String SEARCH = "5";
		/**
		 * 下载
		 * */
		public static final String DOWNLOAD = "6";
		/**
		 * 详情页
		 * */
		public static final String DETAIL = "7";
		/**
		 * 更多
		 * */
		public static final String MORE = "8";
		/**
		 * 收藏夹
		 * */
		public static final String FAVORITE = "9";
		/**
		 * 直播
		 * */
		public static final String LIVE = "l";
		/**
		 * 我的视频
		 * */
		public static final String MYVIDEO = "v";
		/**
		 * 我的乐视
		 * */
		public static final String MYLETV = "m";
		/**
		 * 我的预约
		 * */
		public static final String MYBOOK = "a";
		/**
		 * 分享浮层-详情页
		 * */
		public static final String MYSHARE = "b";
		/**
		 * 我的追剧
		 * */
		public static final String MYAFTER = "a";

	}

	public static final class STATUS {

		public static final String INITIAL = "1";// 初始

		public static final String MANUAL = "2";// 用户手动结束播放

		public static final String AUTO = "3";// 视频自动完成播放

		public static final String ERROR = "4";// 播放出错
	}

	public static final class ERROR {

		public static final String NORMAL = "0";// 正常

		public static final String DIAODU = "1";// 调度错误

		public static final String CDN = "2";// cdn资源错误

		public static final String LOADING = "3";// 加载失败

		public static final String UNKNOWN = "4";// 未知错误
		/**
		 * 下面两个为特殊用
		 */
		public static final String PLAY_ERROR_D = "000";
		public static final String DOWNLOAD_ERROR_D = "111";
	}

	public static final class FROM {

		public static final String CHANNEL_LIST = "1";// 频道列表

		public static final String DETAIL = "2";// 详情

		public static final String PLAY_RECORD = "3";// 播放记录

		public static final String DOWNLOAD = "4";// 下载

		public static final String SEARCH_LIST = "5";// 搜索结果列表

		public static final String RANK = "6";// 排行

		public static final String HOME = "7";// 首页

		public static final String OTHER = "8";// 其它

		public static final String TRACE = "9";// 追剧

		public static final String COLLECT = "10";// 收藏
	}

	public static final class ACTION {

		public static final class COMMON {
			public static final String EXIT = "00-";
			public static final String ENTER = "01-";
			public static final String UPDATE = "02-";
		}

		public static final class NAVIGATION {
			public static final String CLICK = "10";
		}

		public static final class PUBLIC {
			/**
			 * 首页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "095";

			/**
			 * 首页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "094";
		}

		public static final class HOME {
			public static final String FOCUS_IMAGE_FLING = "201";// 焦点图滑动
			public static final String FOCUS_IMAGE_CLICK = "200";// 焦点图点击

			public static final String RECOMEND_CHANNEL = "210";// 推荐分栏
			public static final String RECOMEND_LIST = "220";// 推荐列表

			public static final String HOME_RECOMMEND_SHOW = "255";// 首页推荐（猜您喜欢）曝光
			public static final String HOME_RECOMMEND_CLICK = "254";// 首页推荐（猜您喜欢）点击

			public static final String HOME_TENCENT_INSTALL = "284";// 首页腾讯管家安装
			public static final String HOME_TENCENT_CANCEL = "285";// 首页腾讯管家取消

			/**
			 * 焦点图点击 extra:需要统计焦点图点击次数,第几张焦点图以及焦点图对应视频信息点击（pid_vid）
			 */
			public static final String FOCUS_CLICK = "2f4";

			/**
			 * 首页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "295";

			/**
			 * 首页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "294";
		}

		public static final class CHANNEL {
			public static final String CHOOSE_CHANNEL = "300";// 选择频道
			public static final String CHOOSE_CATEGORY = "310";// 选择分类
			public static final String ORDER = "320";// 排序
			public static final String PAGETURN = "330";// 翻页
			public static final String CHOOSE_PAGE = "340";// 选择页码

			/**
			 * 频道页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "395";

			/**
			 * 频道页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "394";
		}

		public static final class PLAYER {
			public static final String PLAY = "400";// 播放
			public static final String PAUSE = "410";// 暂停
			public static final String FASTFORWARD = "420";// 快进
			public static final String REWIND = "430";// 快退
			public static final String VOLUME = "440";// 音量
			public static final String FINISH = "450";// 完成
			public static final String CHOOSE = "460";// 选集
			public static final String BLOG = "470";// 微博
			public static final String COLLECT = "480";// 收藏
			public static final String TRACE = "490";// 追剧
			public static final String DOWNLOAD = "4a0";// 下载
			/**
			 * 添加收藏 extra:频道(cid);剧集标识（pid_vid）;收藏或取消状态，1为收藏、0为取消
			 */
			public static final String COLLECT_CLICK = "406";
			/**
			 * 分享按钮 extra:频道(cid);剧集标识（pid_vid）;
			 */
			public static final String SHARE_CLICK = "416";

			/**
			 * 播放页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "495";

			/**
			 * 播放页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "494";
		}

		public static final class SEARCH {
			public static final String RECOMMEND_KEY = "500";// 推荐关键词
			public static final String SEARCH_BYSELF = "510";// 主动搜索
			public static final String SEARCH_HISTORY = "520";// 搜索历史
			/**
			 * 文字输入框搜索 extra:搜索内容
			 */
			public static final String TEXT_SEARCH_CLICK = "504";
			/**
			 * 语音输入按钮搜索 extra:搜索内容
			 */
			public static final String VOICE_SEARCH_CLICK = "514";
			/**
			 * 搜索历史 extra:搜索内容
			 */
			public static final String SEARCH_HISTORY_CLICK = "524";
			/**
			 * 热门搜索推荐关键词输入 extra:搜索内容
			 */
			public static final String SEARCH_HOT_RECOMMEND_CLICK = "534";
			/**
			 * 关键词联想输入 extra:搜索内容
			 */
			public static final String SEARCH_TIP_CLICK = "544";

			/**
			 * 搜索页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "595";

			/**
			 * 搜索页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "594";
		}

		public static final class DOWNLOAD {
			public static final String DOWNLOAD_PAUSE = "v00";// 暂停
			public static final String DOWNLOAD_START = "v10";// 下载
			public static final String DELETE_NOFINISH = "v20";// 未下载完成删除

			/**
			 * 下载页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "v95";

			/**
			 * 下载页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "v94";
		}

		public static final class DETAIL {
			public static final String CHOOSE = "700";// 选集
			public static final String PLAY = "710";// 播放
			public static final String DOWNLOAD = "720";// 下载
			public static final String COLLECT = "730";// 收藏
			public static final String BLOG = "740";// 微博
			public static final String RECOMMEND_LIST = "750";// 推荐列表
			public static final String DIRECTOR_RELATE = "760";// 导演相关
			public static final String ACTOR_RELATE = "770";// 演员相关
			public static final String TRACE = "780";// 追剧
			public static final String RECOMMEND_LIST_SHOW = "755";// 你可能喜欢曝光
			public static final String DIRECTOR_RELATE_SHOW = "765";// 导演相关曝光
			public static final String ACTOR_RELATE_SHOW = "775";// 主演相关曝光
			public static final String RECOMMEND_LIST_CLICK = "754";// 你可能喜欢点击
			public static final String DIRECTOR_RELATE_CLICK = "764";// 导演相关点击
			public static final String ACTOR_RELATE_CLICK = "774";// 主演相关点击
			/**
			 * 添加收藏 extra:频道(cid);剧集标识（pid_vid）;收藏或取消状态，1为收藏、0为取消
			 */
			public static final String COLLECT_CLICK = "706";
			/**
			 * 分享按钮 extra:频道(cid);剧集标识（pid_vid）;
			 */
			public static final String SHARE_CLICK = "716";

			/**
			 * 详情页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "795";

			/**
			 * 详情页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "794";
		}

		/**
		 * 分享图层
		 * 
		 * @author liang
		 * 
		 */
		public static final class SHARE {
			/**
			 * 分享 extra:频道(cid);剧集标识（pid_vid）;分享渠道代码，0为并没有有效分享
			 */
			public static final String SHARE_DIALOG = "b04";

			public static final String SHARE_DIALOG_SINA = "1";

			public static final String SHARE_DIALOG_QQ_WEIBO = "2";

			public static final String SHARE_DIALOG_QZONE = "3";

			public static final String SHARE_DIALOG_WEIXIN = "4";

			public static final String SHARE_DIALOG_RENREN = "5";

			public static final String SHARE_DIALOG_DAKA = "6";

			public static final String SHARE_DIALOG_HOMESHARE = "7";
		}

		/**
		 * 直播模块
		 * 
		 * @author liang
		 * 
		 */
		public static final class LIVE {
			/**
			 * 选台 extra:直播频道信息
			 */
			public static final String CHOOSE_CHANNEL_CLICK = "l04";
			/**
			 * 选时间表 extra:无
			 */
			public static final String CHOOSE_TIME_CLICK = "l14";
			/**
			 * 我的预约 extra:无
			 */
			public static final String MY_BOOK_CLICK = "l24";
			/**
			 * 节目单 extra:扩展字段提供去观看(0)、预定(1)和取消(2)
			 */
			public static final String ITEM_LIST_CLICK = "l36";
			public static final String ITEM_LIST_CLICK_PLAY = "0";
			public static final String ITEM_LIST_CLICK_BOOK = "1";
			public static final String ITEM_LIST_CLICK_CANCEL_BOOK = "2";
			/**
			 * 我的预约-预约提醒开关 extra:无
			 */
			public static final String MY_BOOK_REMIND_CLICK = "a04";

			/**
			 * 直播页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "l95";

			/**
			 * 直播页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "l94";
		}

		public static final class MORE {
			public static final String SHARE_SWITCH = "800";// 自动分享微博开关
			public static final String PLAY_RECORD = "810";// 播放记录
			public static final String NET_DIAGNOSE = "820";// 网络诊断
			public static final String FEEDBACK = "830";// 意见反馈
			public static final String ABOUT = "840";// 关于我们

			/**
			 * 更多页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "895";

			/**
			 * 更多页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "894";
		}

		/**
		 * 收藏夹
		 * 
		 * @author liang
		 * 
		 */
		public static final class COLLECT {
			/**
			 * 顶部删除按钮 extra:频道(cid);剧集标识（pid_vid）
			 */
			public static final String COLLECT_DELETE_TOP_CLICK = "904";
			/**
			 * 底部左侧删除按钮 extra:频道(cid);剧集标识（pid_vid）
			 */
			public static final String COLLECT_DELETE_LEFT_CLICK = "914";
			/**
			 * 底部右侧清空按钮 extra:无
			 */
			public static final String COLLECT_DELETE_RIGHT_CLICK = "924";

			/**
			 * 收藏
			 */
			public static final String DO_COLLECT = "1";
			/**
			 * 取消收藏
			 */
			public static final String CANCEL_COLLECT = "0";
		}

		public static final class MYLETV {

			/**
			 * 我的乐视页广告曝光 extra:广告标识(tfid)
			 * */
			public static final String AD_SHOW = "m95";

			/**
			 * 我的乐视页广告点击 extra:广告标识(tfid)
			 * */
			public static final String AD_CLICK = "m94";
		}

		public static final class LE123 {

			public static final class NAVIGATION {
				public static final String HOME = "10"; // 首页
				public static final String CHANNEL = "11"; // 频道
				public static final String SEARCH = "12"; // 搜索
				public static final String MORE = "13"; // 我的
			}

			public static final class HOME {
				public static final String FOCUS_IMAGE_FLING = "201";// 焦点图滑动
				public static final String FOCUS_IMAGE_CLICK = "200";// 焦点图点击

				public static final String RECOMEND_SPECIAL = "210";// 推荐专题
				public static final String RECOMEND_TODAY = "220";// 今日推荐
				public static final String MOVIE_FORECAST = "230";// 新片预告
				public static final String HOT_MOVIE = "240";// 热门影视
			}

			public static final class CHANNEL {
				public static final String SERIAL = "300";// 电视剧
				public static final String MOVIE = "310";// 电影
				public static final String CARTOON = "320";// 动漫
				public static final String TVSHOW = "330";// 综艺
				public static final String MUSIC = "340";// 音乐
				public static final String SERIAL_SPECIAL = "350";// 电视剧专题
				public static final String MOVIE_SPECIAL = "360";// 电影专题
				public static final String CARTOON_SPECIAL = "370";// 动漫专题
				public static final String SPECIAL = "380";// 专题

				public static final String CATEGORY = "0";// 分类
				public static final String AREA = "1";// 地区
				public static final String YEAR = "2";// 年份
				public static final String NEW = "3";// 最新
				public static final String HOT = "4";// 最热
				public static final String COMMENT = "5";// 好评
			}

			public static final class SEARCH {
				public static final String HOT = "500";// 热点
				public static final String SPECIAL = "510";// 专题
				public static final String RANK = "520";// 排行
			}

			public static final class DETAIL {
				public static final String CHOOSE = "700";// 选集
				public static final String PLAY = "710";// 播放
				public static final String COLLECT = "720";// 收藏
				public static final String RECOMMEND_LIST = "730";// 推荐列表
			}

			public static final class MORE {
				public static final String COLLECT_RECORD = "800";// 收藏夹
				public static final String PLAY_RECORD = "810";// 观看记录
				public static final String FEEDBACK = "820";// 意见反馈
				public static final String UPDATE = "830";// 升级
				public static final String ABOUT = "840";// 关于
			}

			public static final String RECOMMEND = "900";// 精品推荐
		}

		/**
		 * 我的相关动作
		 */
		public static final class MYACTION {
			public static final String HOME_CLICK_ACTION = "204";// 推荐页顶部栏
			public static final String CHANNEL_CLICK_ACTION = "304";// 频道页顶部栏
			public static final String LIVEPLAYER_CLICK_ACTION = "l44";// 直播页顶部栏
			public static final String RECOMMEND_CLICK_ACTION = "d04";// 精品推荐页顶部栏
			public static final String CHANNEL_LIST_CLICK_ACTION = "e04";// 频道列表页顶部栏
		}

		/**
		 * 收藏相关动作码
		 */
		public static final class FAVORITEACTION {
			public static final String HOME_RECOMMEND_CLICK_ACTION = "214";// 首页推荐内容
																			// 点击数
																			// 214
																			// 频道列表页内容：共15个频道
			public static final String CHANNEL_LIST_CLICK_ACTION = "e14";// 频道列表页内容：共15个频道
																			// 点击数
																			// e14
																			// cid
			public static final String HALF_PLAYER_CLICK_ACTION = "424";// 半屏播放器
																		// 点击数
																		// 424
			public static final String FULL_PLAYER_CLICK_ACTION = "434";// 全屏播放器
																		// 点击数
																		// 434
			public static final String SEARCH_RESULT_CLICK_ACTION = "554";// 搜索结果
																			// 点击数
																			// 554
		}
	}

	public static class StaticticsCacheTrace {

		public static final String TABLE_NAME = "staticticsCache";

		public static class Field {
			public static final String CACHEID = "cacheId";
			public static final String CACHEDATA = "cachedata";
			public static final String CACHETIME = "cachetime";

		}
	}

	public static class StaticticsVersion2Constatnt {
		public static class PlayerAction {
            /**
             * 点击播放LAUNCHER
             * */
            public static final String LAUNCHER_ACTION = "launch";
 			/**
			 * 播放器初始化
			 */
			public static final String INIT_ACTION = "init";
			/**
			 * 获取videoFile信息
			 */
			public static final String GSLB_ACTION = "gslb";
			/**
			 * 获取真实url
			 */
			public static final String CLOAD_ACTION = "cload";
			/**
			 * 开始播放
			 */
			public static final String PLAY_ACTION = "play";
			/**
			 * 播放时长上报
			 */
			public static final String TIME_ACTION = "time";
			/**
			 * 播放缓冲
			 */
			public static final String BLOCK_ACTION = "block";
			/**
			 * 播放结束
			 */
			public static final String END_ACTION = "end";
			
		}

		/**
		 * @author haitian
		 * @category Action property: 动作属性
		 */
		public static class StaticticsName {
			/**
			 * 分类
			 */
			public static final String STATICTICS_NAM_FL = "fl=";
			/**
			 * 位置
			 */
			public static final String STATICTICS_NAM_WZ = "wz=";
			/**
			 * 名称
			 */
			public static final String STATICTICS_NAM_NA = "name=";
			/**
			 * 频道ID
			 */
			public static final String STATICTICS_NAM_CID = "cid=";
			
			/**
			 * 模块id
			 * */
			public static final String STATICTICS_NAM_BID = "bid=";
			/**
			 * 页面id
			 * */
			public static final String STATICTICS_NAM_PAGE_ID = "pageid=";
		}

		public static class CategoryCode {
			/**
			 * 11、首页焦点图
			 */
			public static final String HOME_TOP_FOCUS = "11";
			/**
			 * 12、首页模板单元数据图
			 */
			public static final String HOME_BLOCK_ITEM = "12";
			/**
			 * 13、首页底部
			 */
			public static final String HOME_RECOMMEND_BOTTOM = "13";

			/**
			 * 21、频道导航栏
			 */
			public static final String CATRGORY_MAIN_TITLE = "21";
			/**
			 * 频道分类检索使用211，同时上报位置
			 */
			public static final String CHANNEL_CONTENT_FILTER = "211";
			/**
			 * 频道页焦点图使用212，同时上报频道id和位置
			 */
			public static final String CHANNEL_CONTENT_HOME_FOCUS = "212";
			/**
			 * 频道页区块使用213，同时上报频道id、区块名、位置
			 */
			public static final String CHANNEL_CONTENT_HOME_BLOCK = "213";

			/**
			 * 全屏播放器超级电视外部区块 a13
			 */
			public static final String FULL_PLAYER_PUSH_SUPER_TV = "a13";
			/**
			 * 全屏播放器左侧功能栏超级电视内部区块 wz =1、2、3
			 */
			public static final String FULL_PLAYER_PUSH_SUPER_TV_BLOCK = "a132";
			/**
			 * 全屏播放器左下角推送到超级电视按钮 wz=1
			 */
			public static final String FULL_PLAYER_PUSH_SUPER_TV_LEFT_BOTTOM = "a16";
			/**
			 * 全屏播放器左下角高清按钮1080P wz = 1
			 */
			public static final String FULL_PLAYER_PUSH_SUPER_TV_LEFT_1080P = "a17";

			/**
			 * 搜索热词使用52，同时上报位置
			 */
			public static final String SEARCH_WORD_HOT = "52";
			/**
			 * 搜索页直接搜索结果
			 */
			public static final String SEARCH_ACCESS_WORD_RESULT = "511";
			/**
			 * 搜索页联想词搜索
			 */
			public static final String SEARCH_LINK_WORD_RESULT = "512";
			/**
			 * 搜索页内/外网点击
			 */
			public static final String SEARCH_RESULT_CLICK = "513";

			/**
			 * 41、精品换量焦点图
			 */
			public static final String RECOMMEND_RECOMMEND_FOCUS = "41";
			/**
			 * 42、精品换量装机必备
			 */
			public static final String RECOMMEND_RECOMMEND_INSTALLED = "42";
			/**
			 * 43、精品换量游戏
			 */
			public static final String RECOMMEND_RECOMMEND_GAMES = "43";
			/**
			 * 44、精品换量热门软件
			 */
			public static final String RECOMMEND_RECOMMEND_HOT = "44";
			/**
			 * 直播页焦点图
			 */
			public static final String LIVE_HALL_FOCUS_CLICK = "32";
			/**
			 * 精选推广页面焦点图
			 */
			public static final String LIVE_HALL_RECOMMEND_CLICK = "41";

		}

		/**
		 * @author haitian
		 * @category 动作码
		 */
		public static class ActionCode {
			/**
			 * 点击
			 */
			public static final String CLICK = "0";
			/**
			 * 评论
			 */
			public static final String COMMENT = "1";
			/**
			 * 下载
			 */
			public static final String DOWNLOAD = "2";
			/**
			 * 收藏
			 */
			public static final String COLLECT = "3";
			/**
			 * 分享
			 */
			public static final String SHARE = "4";
			/**
			 * 充值
			 */
			public static final String RECHARGE = "5";
			/**
			 * 缴费
			 */
			public static final String PAY = "6";
			/**
			 * 待定
			 */
			public static final String UNSURE = "7";
			/**
			 * 节目单呼出
			 */
			public static final String DISPEPSIODE = "8";
			/**
			 * 安装
			 */
			public static final String INSTALL = "9";
			/**
			 * 卸载
			 */
			public static final String UNINSTALL = "10";
			/**
			 * 启动
			 */
			public static final String START = "11";
			/**
			 * 退出
			 */
			public static final String EXIT = "12";
			/**
			 * 在线
			 */
			public static final String ONLINE = "13";
			/**
			 * 升级
			 */
			public static final String UPDATE = "14";
			/**
			 * 频道列表显示
			 */
			public static final String DISCHANNELLIST = "15";
			/**
			 * 换台
			 */
			public static final String SWITCH = "16";
			/**
			 * 推荐点击
			 */
			public static final String RECOMMENDCLICK = "17";

		}

		/**
		 * @author haitian
		 * @category 播放器的vtype
		 */
		public static class VType {
			public static final String FLV_350 = "1";
			public static final String P3GP_320X240 = "2";
			public static final String FLV_ENP = "3";
			public static final String CHINAFILM_350 = "4";
			public static final String FLV_VIP = "8"; // vip
			public static final String MP4 = "9";
			public static final String FLV_LIVE = "10"; // 直播回看
			public static final String UNION_LOW = "11"; // 清华合作
			public static final String UNION_HIGH = "12"; // 清华合作
			public static final String MP4_800 = "13";
			public static final String FLV_1000 = "16";
			public static final String FLV_1300 = "17";
			public static final String FLV_720P = "18";
			public static final String MP4_1080P = "19";
			public static final String FLV_1080P6M = "20";
			public static final String MP4_350 = "21";
			public static final String MP4_1300 = "22";
			public static final String MP4_800_DB = "23";// 杜比800mp4
			public static final String MP4_1300_DB = "24";// 杜比1300mp4
			public static final String MP4_720P_DB = "25";// 杜比720pmp4
			public static final String MP4_1080P6M_DB = "26";// 杜比1080p6mmp4
			public static final String FLV_YUANHUA = "27";
			public static final String MP4_YUANHUA = "28";
			public static final String FLV_720P_3D = "29";
			public static final String MP4_720P_3D = "30";
			public static final String FLV_1080P6M_3D = "31";
			public static final String MP4_1080P6M_3D = "32";
			public static final String FLV_1080P_3D = "33";
			public static final String MP4_1080P_3D = "34";
			public static final String FLV_1080P3M = "35";
			public static final String FLV_4K = "44";
			public static final String H265_FLV_800 = "47";
			public static final String H265_FLV_1300 = "48";
			public static final String H265_FLV_720P = "49";
			public static final String H265_FLV_1080P = "50";
		}
	}

	/**
	 * 播放出错统计 code
	 * 
	 * @author haitian
	 * 
	 */
	public static class ERRORCODE {
		// --------------点播--------------------
		/**
		 * 请求专辑详情失败--1001
		 */
		public static final String REQUEST_ALBUM_DETAIL_ERROR = "1001";
		/**
		 * 请求视频详情失败--1002
		 */
		public static final String REQUEST_VIDEO_DETAIL_ERROR = "1002";
		/**
		 * 专辑视频列表失败--1003
		 */
		public static final String REQUEST_VIDEO_LIST_ERROR = "1003";
		/**
		 * 请求视频文件信息失败--1004
		 */
		public static final String REQUEST_VIDEO_FILE_ERROR = "1004";
		/**
		 * 得到过期时间戳失败—1005
		 */
		public static final String REQUEST_PLAY_TM_ERROR = "1005";
		/**
		 * 请求调度失败--1006
		 */
		public static final String REQUEST_DDURLS_ERROR = "1006";
		/**
		 * 视频缓冲或播放失败—1007
		 */
		public static final String REQUEST_LOADING_ERROR = "1007";
		/**
		 * 网络异常—1008
		 */
		public static final String REQUEST_PLAY_NET_ER_ERROR = "1008";
		/**
		 * 未知错误--1009
		 */
		public static final String REQUEST_PLAY_OTHERS_ERROR = "1009";
		// ---------------------直播---------------------

		/**
		 * 直播海外屏蔽无法播放--2001
		 */
		public static final String REQUEST_OVERSEA_AREA_ERROR = "2001";
		/**
		 * 请求真实的播放地址--2002
		 */
		public static final String REQUEST_REAL_URL_ERROR = "2002";
		/**
		 * 请求过期时间戳（直播）tm失败—2003
		 */
		public static final String REQUEST_LIVE_TM_ERROR = "2003";
		/**
		 * 网络异常—1008
		 */
		public static final String REQUEST_LIVE_NET_ER_ERROR = "2004";
		/**
		 * 未知错误--1009
		 */
		public static final String REQUEST_LIVE_OTHERS_ERROR = "2005";
	}
	
	public static final String P3 = "001";
}
