package com.umsit.bustrackerdriver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umsit.bustrackerdriver.constants.Constants;
import com.umsit.bustrackerdriver.constants.ToastHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{

	// Progress Dialog Object
	ProgressDialog prgDialog;
	
	EditText etBusNo, etPassword;
	Button btnLogin;
	//String busNo, password;
	String email;
	String password;
	
	ProgressDialog _pDialogue;
	private HttpClient _clinet;
	private HttpGet _post;
	SharedPreferences _sp;
	SharedPreferences.Editor _ed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_login1);
		
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
		prgDialog.setMessage("Please wait...");
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		        
		etBusNo= (EditText)findViewById(R.id.et_username);
		etPassword = (EditText)findViewById(R.id.et_password);
		btnLogin =(Button)findViewById(R.id.btn_login);
		

		_sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		_ed = _sp.edit();
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Get Email Edit View Value
				email = etBusNo.getText().toString().toUpperCase();
				// Get Password Edit View Value
				password = etPassword.getText().toString();
				// Instantiate Http Request Param Object
				RequestParams params = new RequestParams();
				// When Email Edit View and Password Edit View have values other than Null
				if(isNotNull(email) && isNotNull(password)){
					// When Email entered is Valid
					//if(Utility.validate(email)){
						// Put Http parameter username with value of Email Edit View control
						params.put("user", email);
						//busNo= email;
						// Put Http parameter password with value of Password Edit Value control
						params.put("password", password);
						params.put("format", "json");
						// Invoke RESTful Web Service with Http parameters
						invokeWS(params);
					//} 
					// When Email is invalid
					//else{
					//	Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
					//}
				} 
				// When any of the Edit View control left blank
				else{
					Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * Checks for Null String object
	 * 
	 * @param txt
	 * @return true for not null and false for null String object
	 */
	public static boolean isNotNull(String txt){
		return txt!=null && txt.trim().length()>0 ? true: false;
	}
	
	private void checkLogin(String bus_no, String password) {
		// Tag used to cancel the request
		String tag_json_obj = "json_obj_req";
		
		final String BASE_URL = "http://192.168.43.173:8080/useraccount/";
		String query = BASE_URL + "/login/driver?username="+bus_no+"&password="+password;

		/*final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
		pDialog.setMessage("Loading...");
		pDialog.show();*/
		
		_pDialogue.setMessage("Please wait...");
		_pDialogue.setCancelable(false);
		_pDialogue.show();
		
		StringRequest strReq = new StringRequest(Method.GET, query, new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				if (_pDialogue.isShowing()) {
					_pDialogue.dismiss();
				}
				try {
           	 	 // JSON Object
                    JSONObject obj = new JSONObject(arg0);
                    // When the JSON response has status boolean value assigned with true
                    if(obj.getBoolean("status")){
                   	 Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                   	 // Navigate to Home screen
                   	
    				Intent i = new Intent(getApplicationContext(), MainActivity.class);
    				startActivity(i);
    				finish();
                    } 
                    // Else display error message
                    else{
                   	 //errorMsg.setText(obj.getString("error_msg"));
                   	 Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                
            }
				//ToastHelper.showToast(getApplicationContext(), "Response: " +arg0.toString());
				
				
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if (_pDialogue.isShowing()) {
					_pDialogue.dismiss();
				}
				
				ToastHelper.showToast(getApplicationContext(), "Error");
			}
		});
		AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
		
		/*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, query,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("Json response", response.toString());
						ToastHelper.showToast(getApplicationContext(), "In Response");

						try {
							JSONArray weather = response.getJSONObject("data").getJSONArray("weather");
							//GlobalData.weather = weather;
							Intent i = new Intent(getApplicationContext(), MainActivity.class);
					        startActivity(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
						//pDialog.hide();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						
						VolleyLog.d("Json error",
								"Error: " + error.getMessage());
						ToastHelper.showToast(getApplicationContext(), "Error Loging in");
						ToastHelper.showToast(getApplicationContext(), error.getMessage());
						// hide the progress dialog
					//	pDialog.hide();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);*/
	}
	
	public void invokeWS(RequestParams params){
		// Show Progress Dialog
		 prgDialog.show();
		 // Make RESTful webservice call using AsyncHttpClient object
		 AsyncHttpClient client = new AsyncHttpClient();
		/* final String BASE_URL = "http://192.168.43.173:8080/useraccount/";
			String query = BASE_URL + "/login/driver?username="+bus_no+"&password="+password;*/
			
         client.get(Constants.HOST_ADDRESS+"/login.php",params ,new AsyncHttpResponseHandler() {
        	 // When the response returned by REST has Http response code '200'
             @Override
             public void onSuccess(String response) {
            	 // Hide Progress Dialog
            	 prgDialog.hide();
                 try {
                	 	 // JSON Object
                         JSONObject obj = new JSONObject(response);
                         // When the JSON response has status boolean value assigned with true
                         int status = obj.getJSONArray("login").getJSONObject(0).getInt("status");
                         if(status==1){
                        	 Toast.makeText(getApplicationContext(), obj.getJSONArray("login").getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
                        	 // Navigate to Home screen
                        	_ed.putString("bus_no", email);
             				_ed.commit();
                        	 navigatetoHomeActivity();
                         } 
                         // Else display error message
                         else{
                        	// errorMsg.setText(obj.getString("error_msg"));
                        	 Toast.makeText(getApplicationContext(), obj.getJSONArray("login").getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
                         }
                 } catch (JSONException e) {
                     // TODO Auto-generated catch block
                     Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                     e.printStackTrace();
                     
                 }
             }
             // When the response returned by REST has Http response code other than '200'
             @Override
             public void onFailure(int statusCode, Throwable error,
                 String content) {
                 // Hide Progress Dialog 
                 prgDialog.hide();
                 // When Http response code is '404'
                 if(statusCode == 404){
                     Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                 } 
                 // When Http response code is '500'
                 else if(statusCode == 500){
                     Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                 } 
                 // When Http response code other than 404, 500
                 else{
                     Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                 }
             }
         });
	}
	
	/**
	 * Method which navigates from Login Activity to Home Activity
	 */
	public void navigatetoHomeActivity(){
		Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}
	
	public class feedback extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			_pDialogue.setMessage("Please wait...");
			_pDialogue.setCancelable(false);
			_pDialogue.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			/*ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// complete URL :
			// http://appliconic.com/SG_Taxi/json.php?tag=login&tag_type=driver&email=n&password=n
			

			nameValuePairs.add(new BasicNameValuePair("refference_id", ""
					+ _sp.getString("refference_id", "")));
			nameValuePairs.add(new BasicNameValuePair("txtb_pid", ""
					+ _sp.getString("passenger_email", "")));
			nameValuePairs.add(new BasicNameValuePair("txtb_did", ""
					+ _sp.getString("driver_email", "")));
			nameValuePairs.add(new BasicNameValuePair("txtb_comments", ""
					+ strComment));
			nameValuePairs.add(new BasicNameValuePair("txtb_stars", ""
					+ String.valueOf(ratings)));
			nameValuePairs
					.add(new BasicNameValuePair("txtb_issuccessfull", "y"));
			nameValuePairs.add(new BasicNameValuePair("txtb_ratedby", "d"));
			try {
				_post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			HttpResponse response = null;
			try {
				response = _clinet.execute(_post);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity e = response.getEntity();
				String data = null;
				try {
					data = EntityUtils.toString(e);
				} catch (org.apache.http.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JSONObject last = null;
				try {
					last = new JSONObject(data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Toast.makeText(getApplicationContext(),
				// "Sucessfull",Toast.LENGTH_SHORT).show();
				return last;
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub

			if (_pDialogue.isShowing()) {
				_pDialogue.dismiss();
			}

			/*
			 * if($result>0){$Json_Object->Message="FeedBack Added Successfully";
			 * } else{$Json_Object->Error="Error Adding FeedBack";}
			 */

			Log.i("Feedback Responce", "" + result);

			if (result == null) {
				Toast.makeText(getApplicationContext(), "Check your Internet",
						Toast.LENGTH_SHORT).show();
			} else {
				String _successMsg = null;
				String _errorMsg = null;
				try {
					_successMsg = result.getString("Message");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					_errorMsg = result.getString("Error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (_successMsg.equals("FeedBack Added Successfully")) {

					Toast.makeText(getApplicationContext(),
							"FeedBack Added Successfully", Toast.LENGTH_SHORT)
							.show();

				} else {

					Toast.makeText(getApplicationContext(),
							"Error Adding FeedBack", Toast.LENGTH_SHORT).show();
				}

				startActivity(new Intent(getApplicationContext(),
						MainActivity.class));

				finish();
			}
		}
}
}
