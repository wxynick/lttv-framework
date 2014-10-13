package com.letv.datastatistics.parse;

import java.util.HashMap;

import com.letv.datastatistics.entity.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class DataStatusInfoParse {

	public DataStatusInfo parseJson(JSONObject jsonObject) throws JSONException {

		DataStatusInfo mDataStatusInfo = null;

		if (jsonObject == null) {
			return null;
		}

		JSONObject jsonBodyObject = null;

		if (jsonObject.has("body")) {
			jsonBodyObject = jsonObject.optJSONObject("body");

			if (jsonBodyObject == null) {
				return null;
			}

			mDataStatusInfo = new DataStatusInfo();

			if (jsonBodyObject.has("tm")) {
				mDataStatusInfo.setTm(jsonBodyObject.optInt("tm"));
			}

			if (jsonBodyObject.has("apiinfo")) {

				JSONObject apiInfoObject = jsonBodyObject.optJSONObject("apiinfo");

				if (apiInfoObject != null) {
					ApiInfo mApiInfo = new ApiInfo();
					mApiInfo.setApistatus(apiInfoObject.optString("apistatus"));
					mDataStatusInfo.setApiInfo(mApiInfo);
				}
			}

			if (jsonBodyObject.has("statinfo")) {

				JSONObject statInfoObject = jsonBodyObject.optJSONObject("statinfo");

				if (statInfoObject != null) {
					StatInfo mStatInfo = new StatInfo();
					mStatInfo.setResult(statInfoObject.optString("result"));
					mDataStatusInfo.setStatInfo(mStatInfo);
				}
			}

			if (jsonBodyObject.has("upgrade")) {

				JSONObject upgradeObject = jsonBodyObject.optJSONObject("upgrade");

				if (upgradeObject != null) {
					UpgradeInfo mUpgradeInfo = new UpgradeInfo();
					mUpgradeInfo.setV(upgradeObject.optString("v"));
					mUpgradeInfo.setTitle(upgradeObject.optString("title"));
					mUpgradeInfo.setMsg(upgradeObject.optString("msg"));
					mUpgradeInfo.setUptype(upgradeObject.optString("uptype"));
					mUpgradeInfo.setUrl(upgradeObject.optString("url"));
					mUpgradeInfo.setUpgrade(upgradeObject.optString("upgrade"));
					mDataStatusInfo.setUpgradeInfo(mUpgradeInfo);
				}
			}

			if (jsonBodyObject.has("adinfo")) {

				JSONObject adsInfoObject = jsonBodyObject.optJSONObject("adinfo");
				AdsInfo mAdsInfo = null;

				if (adsInfoObject != null) {
					mAdsInfo = new AdsInfo();
					mAdsInfo.setKey(adsInfoObject.optString("key"));
					mAdsInfo.setValue(adsInfoObject.optString("val"));
					mDataStatusInfo.setAdsInfo(mAdsInfo);
				}

				if (jsonBodyObject.has("adpininfo") && mAdsInfo != null) {

					JSONObject adsPinInfoObject = jsonBodyObject.optJSONObject("adpininfo");

					if (adsPinInfoObject != null) {
						mAdsInfo.setPinKey(adsPinInfoObject.optString("key"));
						mAdsInfo.setPinValue(adsPinInfoObject.optString("val"));
					}
				}
			}

			if (jsonBodyObject.has("recommendinfo")) {

				JSONArray recommendInfoArray = jsonBodyObject.optJSONArray("recommendinfo");

				if (recommendInfoArray != null && recommendInfoArray.length() > 0) {
					HashMap<String, RecommendInfo> recommendInfos = new HashMap<String, RecommendInfo>();
					for (int i = 0; i < recommendInfoArray.length(); i++) {
						JSONObject recommendInfoObject = recommendInfoArray.optJSONObject(i);
						if (recommendInfoObject != null) {
							String key = recommendInfoObject.optString("key");
							String val = recommendInfoObject.optString("val");
							int num = recommendInfoObject.optInt("num");
							if (!TextUtils.isEmpty(key)) {
								RecommendInfo mRecommendInfo = new RecommendInfo();
								mRecommendInfo.setKey(key);
								mRecommendInfo.setValue(val);
								mRecommendInfo.setNum(num);
								recommendInfos.put(key, mRecommendInfo);
							}
						}
					}
					mDataStatusInfo.setRecommendInfos(recommendInfos);
				}
			}

			if (jsonBodyObject.has("defaultbr")) {
				JSONObject object = jsonBodyObject.optJSONObject("defaultbr");

				if (object != null) {

					if (object.has("play")) {
						JSONObject o = object.optJSONObject("play");
						if (o != null && o.has("gphone")) {
							o = o.optJSONObject("gphone");
							if (o != null) {
								Defaultbr pd = new Defaultbr();

								pd.setTopSpeed(o.optString("jisu", "180"));
								pd.setLow(o.optString("low", "350"));
								pd.setNormal(o.optString("normal", "1000"));
								pd.setHigh(o.optString("high", "1300"));
								
								pd.setTopSpeed_zh(o.optString("jisu_zh", "极速"));
								pd.setLow_zh(o.optString("low_zh", "流畅"));
								pd.setNormal_zh(o.optString("normal_zh", "高清"));
								pd.setHigh_zh(o.optString("high_zh", "超清"));

								mDataStatusInfo.setPlayDefaultbr(pd);
							}
						}
					}

					if (object.has("download")) {
						JSONObject o = object.optJSONObject("download");
						if (o != null && o.has("gphone")) {
							o = o.optJSONObject("gphone");
							if (o != null) {
								Defaultbr dd = new Defaultbr();

								dd.setLow(o.optString("low", "350"));
								dd.setNormal(o.optString("normal", "1000"));
								dd.setHigh(o.optString("high", "1300"));
								dd.setLow_zh(o.optString("low_zh", "流畅"));
								dd.setNormal_zh(o.optString("normal_zh", "高清"));
								dd.setHigh_zh(o.optString("high_zh", "超清"));

								mDataStatusInfo.setDownloadDefaultbr(dd);
							}
						}
					}
				}
			}

            if (jsonBodyObject.has("exchange_page")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("exchange_page");
                if (exchangeObj != null) {
                    mDataStatusInfo.setChannelRecommendSwitch("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("exchange_bottom")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("exchange_bottom");
                if (exchangeObj != null) {
                    mDataStatusInfo.setBottomRecommendSwitch("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("exchange_pop")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("exchange_pop");
                if (exchangeObj != null) {
                    mDataStatusInfo.setPopRecommendSwitch("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("adoffline")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("adoffline");
                if (exchangeObj != null) {
                    mDataStatusInfo.setAdOffline("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("china_unicom")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("china_unicom");
                if (exchangeObj != null) {
                    mDataStatusInfo.setChinaUnicom("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("linkshell")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("linkshell");
                if (exchangeObj != null) {
                    mDataStatusInfo.setLinkShell("1".equals(exchangeObj.optString("status")));
                }
            }

            if (jsonBodyObject.has("mp4_utp")) {
                JSONObject exchangeObj = jsonBodyObject.optJSONObject("mp4_utp");
                if (exchangeObj != null) {
                    mDataStatusInfo.setMp4Utp("1".equals(exchangeObj.optString("status")));
                }
            }

			if (jsonBodyObject.has("androidOpen350")) {
				mDataStatusInfo.setAndroidOpen350(jsonBodyObject.getInt("androidOpen350"));
			}
		}
		if (jsonBodyObject.has("logoinfo")) {
			JSONObject logoInfoObject = jsonBodyObject.optJSONObject("logoinfo");
			if (logoInfoObject != null) {
				LogoInfo mLogoInfo = new LogoInfo();
				mLogoInfo.setIcon(logoInfoObject.optString("icon"));
				mLogoInfo.setJumpUrl(logoInfoObject.optString("url"));
				mLogoInfo.setStatus(logoInfoObject.optString("status"));
				mLogoInfo.setComments(logoInfoObject.optString("comments"));
				mDataStatusInfo.setmLogoInfo(mLogoInfo);
			}
		}
		//modified by zengsonghai 20140218,去掉极光推送
//		if (jsonBodyObject.has("jpush")) {
//			JSONObject jpushInfoObject = jsonBodyObject.optJSONObject("jpush");
//			if (jpushInfoObject != null) {
//				JpushInfo mJpushInfo = new JpushInfo();
//				mJpushInfo.setStatus(jpushInfoObject.optString("status"));
//				mDataStatusInfo.setmJpushInfo(mJpushInfo);
//			}
//		}
		
		//wangxuefang  H265开关
		if (jsonBodyObject.has("h265")) {
			JSONObject jpushInfoObject = jsonBodyObject.optJSONObject("h265");
			if (jpushInfoObject != null) {
				H265Info h265Info = new H265Info();
				h265Info.setStatus(jpushInfoObject.optString("status"));
				mDataStatusInfo.setmH265Info(h265Info);
			}
		}
		//androidUtp 开关, modified by zengsonghai
		if (jsonBodyObject.has("androidUtp")) {
			JSONObject jpushInfoObject = jsonBodyObject.optJSONObject("androidUtp");
			if (jpushInfoObject != null) {
				UtpInfo mUtpInfo = new UtpInfo();
				mUtpInfo.setStatus(jpushInfoObject.optString("status"));
				mDataStatusInfo.setmUtpInfo(mUtpInfo);
			}
		}
		//付费视频免费试看时长
		if (jsonBodyObject.has("freetime")) {
			JSONObject jpushInfoObject = jsonBodyObject.optJSONObject("freetime");
			if (jpushInfoObject != null) {
				FreeTime freeTime = new FreeTime();
				freeTime.setTime(jpushInfoObject.optString("time"));
				mDataStatusInfo.setmFreeTime(freeTime);
			}
		}
		//广告配置信息
		if (jsonBodyObject.has("adconfig")) {
			JSONObject adConfigInfo = jsonBodyObject.optJSONObject("adconfig");
			if (adConfigInfo != null) {
				AdsConfig adsConfig = new AdsConfig();
				adsConfig.setAdsConfig(adConfigInfo.optString("data"));
				mDataStatusInfo.setmAdsConfig(adsConfig);
			}
		}
		
		if (jsonBodyObject.has("phonePay")) {
			JSONObject statInfoObject = jsonBodyObject.optJSONObject("phonePay");
			if (statInfoObject != null) {
				PhonePayInfo payInfo = new PhonePayInfo();
				payInfo.setData(statInfoObject.optString("status"));
				mDataStatusInfo.setPhonePayInfo(payInfo);
			}
		}
		/**
		 * tempChannel
			字段
			类型
			说明
			channel_name	string	频道名称
			channel_url	string	频道URL
			channel_position	string	频道位置
			channel_status	string	频道状态:1-上线,0-下线
		 */
		if (jsonBodyObject.has("tempChannel")) {
			JSONObject channelInfoObject = jsonBodyObject.optJSONObject("tempChannel");
			if (channelInfoObject != null) {
				ChannelWorldCupInfo mChannelWorldCupInfo = new ChannelWorldCupInfo();
				mChannelWorldCupInfo.setChannel_name(channelInfoObject.optString("channel_name"));
				mChannelWorldCupInfo.setChannel_position(channelInfoObject.optString("channel_position"));
				mChannelWorldCupInfo.setChannel_status(channelInfoObject.optInt("channel_status"));
				mChannelWorldCupInfo.setChannel_url(channelInfoObject.optString("channel_url"));
				mDataStatusInfo.setmChannelWorldCupInfo(mChannelWorldCupInfo);
			}
		}
        if (jsonBodyObject.has("apiTimeout")) {
            JSONObject timeOutInfoObject = jsonBodyObject.optJSONObject("apiTimeout");
            if (timeOutInfoObject != null) {
                TimeOutInfo mTimeOutInfo = new TimeOutInfo();
                mTimeOutInfo.setTimeValue(timeOutInfoObject.optDouble("value"));
                mDataStatusInfo.setTimeOutInfo(mTimeOutInfo);
            }
        }
		return mDataStatusInfo;
	}
}
