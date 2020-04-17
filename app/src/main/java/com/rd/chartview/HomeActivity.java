package com.rd.chartview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rd.chartview.view.ChartView;
import com.rd.chartview.view.draw.data.Chart;
import com.rd.chartview.view.draw.data.InputData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {
	ChartView chartView;
	TextView txtPositif, txtOdp, txtPdp, txtWaktu;
	LinearLayout lyPositif, lyPdp, lyOdp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);
		initViews();
		getChart("positif");
		getCoronaLast();
	}

	private void initViews() {
		chartView = findViewById(R.id.charView);
		txtPositif = findViewById(R.id.positif);
		txtOdp = findViewById(R.id.odp);
		txtPdp = findViewById(R.id.pdp);
		txtWaktu = findViewById(R.id.waktu);

		lyPositif = findViewById(R.id.lyPositif);
		lyPdp = findViewById(R.id.lyPdp);
		lyOdp = findViewById(R.id.lyodp);

	}

	private void getChart(final String tipe) {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://minangtech.com/home/getDayData/sumatera_barat";
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				try {
					JSONArray jsonArray = new JSONArray(result);
					Chart.MAX_ITEMS_COUNT = jsonArray.length();
					List<InputData> dataList = new ArrayList<>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if(tipe.equalsIgnoreCase("positif")) {
							String positif = jsonObject.getString("positif");
							dataList.add(new InputData(Integer.parseInt(positif)));
						}else if(tipe.equalsIgnoreCase("pdp")){
							String pdp = jsonObject.getString("pdp");
							dataList.add(new InputData(Integer.parseInt(pdp)));
						}else if(tipe.equalsIgnoreCase("odp")){
							String pdp = jsonObject.getString("total_odp");
							dataList.add(new InputData(Integer.parseInt(pdp)));
						}
					}
					long currMillis = System.currentTimeMillis();
					currMillis -= currMillis % TimeUnit.DAYS.toMillis(1);
//
					for (int i = 0; i < dataList.size(); i++) {
						long position = dataList.size() -  i;
						long offsetMillis = TimeUnit.DAYS.toMillis(position);

						long millis = currMillis - offsetMillis;
						dataList.get(i).setMillis(millis);
					}
					chartView.setData(dataList);
				} catch (Exception e) {
					Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}


			}
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// Jika koneksi gagal
//                progressBar.setVisibility(View.INVISIBLE);
				String errorMessage;
				switch (statusCode) {
					case 401:
						errorMessage = statusCode + " : Bad Request";
						break;
					case 403:
						errorMessage = statusCode + " : Forbiden";
						break;
					case 404:
						errorMessage = statusCode + " : Not Found";
						break;
					default:
						errorMessage =  statusCode + " : " + error.getMessage();
						break;
				}
				Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}


	private void getCoronaLast() {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://minangtech.com/home/getCoronaLast/sumatera_barat";
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				Log.d("TAG", result);
				try {
					JSONObject responseObject = new JSONObject(result);
					String total_odp = responseObject.getString("total_odp");
					String pdp = responseObject.getString("pdp");
					String positif = responseObject.getString("positif");
					String waktu = responseObject.getString("waktu");

//					String kode = responseObject.getString("kode");
//					String odp_dalam_pemantauan = responseObject.getString("odp_dalam_pemantauan");
//					String odp_selesai_pemantauan = responseObject.getString("odp_selesai_pemantauan");
//					String pdp_masih_dirawat = responseObject.getString("pdp_masih_dirawat");
//					String pdp_meninggal = responseObject.getString("pdp_meninggal");
//					String pdp_isolasidirumah = responseObject.getString("pdp_isolasidirumah");
//					String pdp_pulangdan_sehat = responseObject.getString("pdp_pulangdan_sehat");
//					String covid_dirawat = responseObject.getString("covid_dirawat");
//					String covid_isolasi_dirumah = responseObject.getString("covid_isolasi_dirumah");
//					String covid_meninggal = responseObject.getString("covid_meninggal");
//					String covid_sembuh = responseObject.getString("covid_sembuh");

					txtWaktu.setText(waktu);
					txtPositif.setText(positif);
					txtOdp.setText(total_odp);
					txtPdp.setText(pdp);
				} catch (Exception e) {
					Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String errorMessage;
				switch (statusCode) {
					case 401:
						errorMessage = statusCode + " : Bad Request";
						break;
					case 403:
						errorMessage = statusCode + " : Forbiden";
						break;
					case 404:
						errorMessage = statusCode + " : Not Found";
						break;
					default:
						errorMessage =  statusCode + " : " + error.getMessage();
						break;
				}
				Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@SuppressLint("ResourceAsColor")
	public void odp(View view) {
		getChart("odp");
		lyPositif.setBackgroundColor(getResources().getColor(R.color.white));
		lyPdp.setBackgroundColor(getResources().getColor(R.color.white));
		lyOdp.setBackgroundColor(getResources().getColor(R.color.gray_50));
	}

	public void positif(View view) {
		getChart("positif");
		lyPositif.setBackgroundColor(getResources().getColor(R.color.gray_50));
		lyPdp.setBackgroundColor(getResources().getColor(R.color.white));
		lyOdp.setBackgroundColor(getResources().getColor(R.color.white));
	}

	public void pdp(View view) {
		getChart("pdp");
		lyPositif.setBackgroundColor(getResources().getColor(R.color.white));
		lyPdp.setBackgroundColor(getResources().getColor(R.color.gray_50));
		lyOdp.setBackgroundColor(getResources().getColor(R.color.white));
	}
}
