package com.Dinggrn.weiliao.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.app.MyApp;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
/**
 * ��ͼ����
 * 
 * @author pjy
 *
 */
public class LocationActivity extends BaseActivity {

	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.mapview_location)
	MapView mapView;


	String type;//����ȡֵ"button","item"
	double lat;//γ��ֵ
	double lng;//����ֵ
	String address;//(lat,lng)���������Ӧ�Ľֵ�����

	BaiduMap baiduMap;
	LocationClient client;
	MyLocationListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		ButterKnife.bind(this);
		type = getIntent().getStringExtra("type");
		log("type------>"+type);
		initHeaderView();
	}

	private void initHeaderView() {
		if(type.equals("button")){
			//����
			setHeaderTitle(headerView, "�ҵ�λ��");
			//�����˰�ť
			setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			//�Ҳ��ͼ��ť
			setHeaderImage(headerView, R.drawable.ic_map_snap, Position.RIGHT, new OnClickListener() {

				@Override
				public void onClick(View v) {
					//��ͼ��ʼǰ���û�һ����ʾ
					final ProgressDialog pd = ProgressDialog.show(LocationActivity.this, "", "��ͼ��ͼ��...");
					pd.show();
					//���е�ͼ��ͼ
					baiduMap.snapshot(new SnapshotReadyCallback() {
						@Override
						public void onSnapshotReady(Bitmap bitmap) {
							try {
								
								//1)bitmap��ͼ��ͼ�ϴ���Bmob������
								File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+".png");
								String filePath = file.getAbsolutePath();
								OutputStream stream = new FileOutputStream(file);
								bitmap.compress(CompressFormat.PNG, 30, stream );
								BmobProFile.getInstance(LocationActivity.this).upload(filePath, new UploadListener() {
									
									@Override
									public void onError(int arg0, String arg1) {
										// TODO Auto-generated method stub
										pd.dismiss();
									}
									
									@Override
									public void onSuccess(String arg0, String arg1, BmobFile arg2) {
										pd.dismiss();
										String url = arg2.getUrl();//��ͼ��ͼ�ϴ����������ϵĵ�ַ
										//2)�ѽ�ͼ�ڷ������ϵĵ�ַ��LocationActivity���ݸ�ChatActivity
										Intent data = new Intent();
										data.putExtra("url", url);
										data.putExtra("address", address);
										data.putExtra("lat",lat);
										data.putExtra("lng", lng);
										setResult(RESULT_OK, data);
										finish();
									}
									
									@Override
									public void onProgress(int arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							
						}
					});

				}
			});
			
			getLocation();//���ж�λ
			
		}else{
			//��item��õ���λ����Ϣ����ʾ������λ��
			String titleaddress = getIntent().getStringExtra("address");
			if(!TextUtils.isEmpty(titleaddress)){
				setHeaderTitle(headerView, titleaddress);
			}else{
				setHeaderTitle(headerView, "����λ��");
			}
			
			setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			showLocation();//��ʾλ��
		}
	}

	private void getLocation() {
		baiduMap = mapView.getMap();
		baiduMap.setMaxAndMinZoomLevel(20, 15);
		client = new LocationClient(this);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy
				);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span=5*60*1000;
		option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(true);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��  
		option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
		option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		listener = new MyLocationListener();
		client.registerLocationListener(listener);
		client.start();
	}

	private void showLocation() {
		//��ʾ��������Ϣ�л�õĵ���λ������
		baiduMap = mapView.getMap();
		baiduMap.setMaxAndMinZoomLevel(20, 15);
		double locLat = Double.parseDouble(getIntent().getStringExtra("lat"));
		double locLng = Double.parseDouble(getIntent().getStringExtra("lng"));
		log("���꣺("+locLat+" , "+locLng+" )");
		MarkerOptions overlay = new MarkerOptions();
		overlay.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
		overlay.position(new LatLng(locLat, locLng));
		baiduMap.addOverlay(overlay);
		//�ƶ���Ļ����
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(locLat, locLng));
		baiduMap.animateMapStatus(msu);
	}

	@Override  
	protected void onDestroy() {  
		super.onDestroy();  
		mapView.onDestroy();  
	}  
	@Override  
	protected void onResume() {  
		super.onResume();  
		//��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
		mapView.onResume();  
	}  
	@Override  
	protected void onPause() {  
		super.onPause();  
		//��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
		mapView.onPause();  
	} 
	
	/**
	 * ���lat,lng�����һ�ζ�λ���MyApp.lastPoint��һ��
	 * �����MyApp.lastPoint��ֵ
	 * ���Ҹ�ϸ�������ϵ�ǰ��¼�û�������ֵ
	 */
	public void check(double latitude, double longitude) {
		if(latitude == MyApp.lastPoint.getLatitude() && longitude==MyApp.lastPoint.getLongitude()){
			return;
		}else{
			
			MyApp.lastPoint.setLatitude(latitude);
			MyApp.lastPoint.setLongitude(longitude);
			
			User user = new User();
			user.setPoint(new BmobGeoPoint(longitude, latitude));
			user.update(this, bmobUserManager.getCurrentUserObjectId(), new UpdateListener() {
				
				@Override
				public void onSuccess() {
					toast("����λ�óɹ�");
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					toast("����λ��ʧ��");
				}
			});
		}
	}

	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {

			int code = arg0.getLocType();
			
			if(code==61||code==66||code==161){
				lat = arg0.getLatitude();
				lng = arg0.getLongitude();
				/**
				 * ���lat,lng�����һ�ζ�λ���MyApp.lastPoint��һ��
				 * �����MyApp.lastPoint��ֵ
				 * ���Ҹ�ϸ�������ϵ�ǰ��¼�û�������ֵ
				 */
				check(lat,lng);

			}else{
				lat = MyApp.lastPoint.getLatitude();
				lng = MyApp.lastPoint.getLongitude();
			}
			
			if(client.isStarted()){
				client.stop();
				client.unRegisterLocationListener(listener);
			}
			
			//���ݶ�λ�õ���(lat,lng)���ڵ�ͼ����ʾһ��������
			showMarker(lat,lng);
			//���ݶ�λ�õ���(lat,lng)����ø�λ������Ӧ�Ľֵ���Ϣ
			getAddress(lat,lng);
		}
	}

	public void showMarker(double latitude, double longitude) {
		//��Ӹ�����
		MarkerOptions overlay = new MarkerOptions();
		overlay.position(new LatLng(latitude, longitude));
		overlay.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
		baiduMap.addOverlay(overlay);
		//�����Ϣ��
		View view = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
		InfoWindow infoWindow = new InfoWindow(view,new LatLng(latitude, longitude),-50);
		baiduMap.showInfoWindow(infoWindow);
		//Ų����Ļ����
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
		baiduMap.animateMapStatus(msu);
	}
	
	public void getAddress(double latitude, double longitude) {
		// ����(latitude, longitude)���������λ����Ϣ��ѯ
		// ����һ����ַ--->(lat,lng) ��������ѯ
		// ����һ��(lat,lng)--->��ַ  �����������ѯ
		GeoCoder geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				//�����������ѯ�����ص��÷���
				if(result==null||result.error!=SearchResult.ERRORNO.NO_ERROR){
					toast("�Ҳ�����Ӧ�Ľֵ���Ϣ");
					address = "������·";
				}else{
					address = result.getAddress();
					toast(address);
				}
				
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO ��������ѯ�����ص��÷���
				
			}
		});
		
		ReverseGeoCodeOption options = new ReverseGeoCodeOption();
		options.location(new LatLng(latitude, longitude));
		geoCoder.reverseGeoCode(options);
	}


}
