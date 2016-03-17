package com.cheweishi.android.biz;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 图片助手
 * 
 * @author 健
 */
public class XUtilsImageLoader {
	private static XUtilsImageLoader mImageLoader;
	// static DisplayImageOptions options;
	// 异步加载图片
	protected static BitmapUtils mBitmapUtils;
	// 加载图片设置信息
	protected static BitmapDisplayConfig bigPicDisplayConfig;

	/**
	 * @param context
	 * @param resid
	 *            0为默认图片
	 */
	private XUtilsImageLoader(Context context, int resid) {
	}

	private XUtilsImageLoader(Context context) {
	}

	public static void clearCache() {
		mBitmapUtils.clearCache();
		mBitmapUtils.clearDiskCache();
	}

	/**
	 * 初始化缓存功能对�?设置图片缓存路径
	 * 
	 * @param path
	 *            各个模块路径
	 */
	protected static void setBitmap2FileDir(Context mContext, String path) {
		// if (mBitmapUtils == null) {
		// if (StringUtil.isEmpty(mBitmapUtils)) {
		mBitmapUtils = new BitmapUtils(mContext, Constant.BASE_IMG_CATCH_DIR
				+ path);
		mBitmapUtils.configDiskCacheEnabled(true); // 允许将图片缓存到sd�?
		mBitmapUtils.configDiskCacheEnabled(true);
		mBitmapUtils.configMemoryCacheEnabled(false);
		// }
		// }
		// 创建缓存目录
		// Files.createDirIfNoExists(Constant.BASE_IMG_CATCH_DIR + path);
		File file = new File(Constant.BASE_IMG_CATCH_DIR + path);
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	/**
	 * 显示图片
	 * 
	 * @param url
	 */
	protected void showImage(Context mContext, ImageView mImg, String url,
			int imgId) {
		if (bigPicDisplayConfig == null) {
			bigPicDisplayConfig = new BitmapDisplayConfig();
			// bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
			// 图片太大时容易OOM。
			bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
			bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils
					.getScreenSize(mContext));
		}
		// 加载失败是显示
		bigPicDisplayConfig.setLoadFailedDrawable(mContext.getResources()
				.getDrawable(imgId));
		// 加载中显示
		bigPicDisplayConfig.setLoadingDrawable(mContext.getResources()
				.getDrawable(imgId));
		if (mBitmapUtils != null) {
			mBitmapUtils.display(mImg, url, bigPicDisplayConfig);
		}

	}

//	/**
//	 * 单例拿到对象
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static synchronized XUtilsImageLoader getxUtilsImageLoader(
//			Context context, int resid) {
//		setBitmap2FileDir(context, "cws");
//		mImageLoader = new XUtilsImageLoader(context, resid);
//		// ImageLoader.getInstance().init(
//		// ImageLoaderConfiguration.createDefault(context));
//		// options = new DisplayImageOptions.Builder().cacheInMemory(true)
//		// .cacheOnDisk(true).showImageForEmptyUri(resid)
//		// .showImageOnFail(resid).showImageOnLoading(resid)
//		// .bitmapConfig(Bitmap.Config.RGB_565).build();
//		if (bigPicDisplayConfig == null) {
//			bigPicDisplayConfig = new BitmapDisplayConfig();
//			// bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
//			// 图片太大时容易OOM。
//			bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
//			bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils
//					.getScreenSize(context));
//		}
//		// // 加载失败是显示
//		// bigPicDisplayConfig.setLoadFailedDrawable(context.getResources()
//		// .getDrawable(resid));
//		// // 加载中显示
//		// bigPicDisplayConfig.setLoadingDrawable(context.getResources()
//		// .getDrawable(resid));
//		return mImageLoader;
//	}

	/**
	 * 单例拿到对象
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized XUtilsImageLoader getxUtilsImageLoader(
			Context context, int resid, ImageView imageView, String uri) {
		setBitmap2FileDir(context, "cws");
		mImageLoader = new XUtilsImageLoader(context, resid);
		// ImageLoader.getInstance().init(
		// ImageLoaderConfiguration.createDefault(context));
		// options = new DisplayImageOptions.Builder().cacheInMemory(true)
		// .cacheOnDisk(true).showImageForEmptyUri(resid)
		// .showImageOnFail(resid).showImageOnLoading(resid)
		// .bitmapConfig(Bitmap.Config.RGB_565).build();
		BitmapDisplayConfig bigPicDisplayConfig = new BitmapDisplayConfig();
		//if (bigPicDisplayConfig == null) {
			
			// bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
			// 图片太大时容易OOM。
			bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
			bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils
					.getScreenSize(context));
		//}
		// 加载失败是显示
		bigPicDisplayConfig.setLoadFailedDrawable(context.getResources()
				.getDrawable(resid));
		// 加载中显示
		bigPicDisplayConfig.setLoadingDrawable(context.getResources()
				.getDrawable(resid));
		if (mBitmapUtils != null) {
			mBitmapUtils.display(imageView, uri, bigPicDisplayConfig);
		}

		return mImageLoader;
	}

	/**
	 * 单例拿到对象
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized XUtilsImageLoader getxUtilsImageLoaderWithNullDefaultImg(
			Context context) {
		setBitmap2FileDir(context, "cws");
		mImageLoader = new XUtilsImageLoader(context);
		// ImageLoader.getInstance().init(
		// ImageLoaderConfiguration.createDefault(context));
		// options = new DisplayImageOptions.Builder().cacheInMemory(true)
		// .cacheOnDisk(true).showImageForEmptyUri(resid)
		// .showImageOnFail(resid).showImageOnLoading(resid)
		// .bitmapConfig(Bitmap.Config.RGB_565).build();
		if (bigPicDisplayConfig == null) {
			bigPicDisplayConfig = new BitmapDisplayConfig();
			// bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
			// 图片太大时容易OOM。
			bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
			bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils
					.getScreenSize(context));
		}
		return mImageLoader;
	}

	/**
	 * view和url下载图片
	 * 
	 * @param imageView
	 * @param uri
	 */
	public void load(ImageView imageView, String uri) {
		// ImageLoader.getInstance().displayImage(uri, imageView, options);
		if (mBitmapUtils != null) {
			mBitmapUtils.display(imageView, uri, bigPicDisplayConfig);
		}
	}

	/**
	 * 加载本地图片
	 * 
	 * @param imageView
	 * @param file
	 */
	public void load(ImageView imageView, File file) {
		if (file != null && file.exists()) {
			load(imageView, file.getAbsolutePath());
		}
	}
}
