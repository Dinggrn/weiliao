package com.Dinggrn.weiliao.app;

import java.io.File;

import android.app.Application;
import android.media.MediaPlayer;
import cn.bmob.im.BmobChat;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.constant.Constant;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApp extends Application{

	//����һЩ����Ϊ�ǳ���Ҫ����ȫ�ֿ��õ�һЩ���ԣ�������
	//������������¼�û���ʹ�����ʱ��λ��
	public static BmobGeoPoint lastPoint;
	//�����µ���Ϣ/�µĺ���������뱻���������͹���ʱ��������ʾ��
	public static MediaPlayer mediaPlayer;
	//ͼƬ��������⣬��Ҫ����һ�γ�ʼ������ʹ��
	public static ImageLoader imageLoader;
	//�ṩһ��ȫ�������Ķ���ʹ�õ�ʱ�������أ����������г��϶������ã��ܲ��þͱ�Ϲ�ã���
	public static MyApp context;
	

	@Override
	public void onCreate() {
		super.onCreate();
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
		//ע��÷���Ҫ��setContentView����֮ǰʵ��  
		SDKInitializer.initialize(getApplicationContext());  
		// ��ʼ�� Bmob SDK
		// ʹ��ʱ�뽫�ڶ�������Application ID�滻������Bmob�������˴�����Application ID
		// Bmob.initialize(this, Constant.BmobKey);
		// �����õ���ģʽ����Ϊtrue��ʱ�򣬻���logcat��BmobChat�����һЩ��־���������ͷ����Ƿ��������У��������˷��ش���Ҳ��һ����ӡ���������㿪���ߵ��ԣ���ʽ����Ӧע�ʹ˾䡣
		BmobChat.DEBUG_MODE = true;
		//BmobIM SDK��ʼ��--ֻ��Ҫ��һ�δ��뼴����ɳ�ʼ��
		BmobChat.getInstance(this).init(Constant.BmobKey);

		//���Եĳ�ʼ��
		context = this;
		mediaPlayer = MediaPlayer.create(context, R.raw.notify);
		initImageLoader();
	}

	/**
	 * ��ʼ��ImageLoader
	 * ��ʼ����������copy��
	 * http://blog.csdn.net/liu1164316159/article/details/38728259
	 * 
	 */
	private void initImageLoader() {
		File cacheDir =StorageUtils.getOwnCacheDirectory(this, "imageloader/Cache");  
		ImageLoaderConfiguration  configuration= new ImageLoaderConfiguration   
				.Builder(this)   
		.memoryCacheExtraOptions(480, 800) // maxwidth, max height���������ÿ�������ļ�����󳤿�   
		.threadPoolSize(3)//�̳߳��ڼ��ص�����   
		.threadPriority(Thread.NORM_PRIORITY -2)   
		.denyCacheImageMultipleSizesInMemory()   
		.memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) // You can pass your own memory cache implementation/�����ͨ���Լ����ڴ滺��ʵ��   
		.memoryCacheSize(5 * 1024 * 1024)     
		.discCacheSize(50 * 1024 * 1024)     
		.discCacheFileNameGenerator(new Md5FileNameGenerator())//�������ʱ���URI������MD5 ����   
		.tasksProcessingOrder(QueueProcessingType.LIFO)   
		.discCacheFileCount(200) //������ļ�����   
		.discCache(new UnlimitedDiscCache(cacheDir))//�Զ��建��·��   
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple())   
		.imageDownloader(new BaseImageDownloader(this,5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)��ʱʱ��   
		.writeDebugLogs() // Remove for releaseapp   
		.build();//��ʼ���� 
		ImageLoader.getInstance().init(configuration);
	}
}
