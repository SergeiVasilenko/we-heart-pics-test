package ru.sergeyvasilenko.weheartpics.interview;

import android.util.Log;
import android.util.SparseArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.sergeyvasilenko.weheartpics.interview.exceptions.ApplicationException;
import ru.sergeyvasilenko.weheartpics.interview.exceptions.RequestException;
import ru.sergeyvasilenko.weheartpics.interview.exceptions.ResponseParseException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 15:12
 */
public class NetworkContentProvider implements ContentProvider {

    private static final String TAG = "NetworkContentProvider";

    public static final String HOST_DEFAULT = "http://weheartpics.com";
    public static final String USER_AGENT = "we-heart-pics-interview/android";
    public static final int TIMEOUT_DEFAULT = 30000;


    private static HttpGet createHttpGet(String path) {
        return createHttpGet(HOST_DEFAULT, path);
    }

    private static HttpGet createHttpGet(String host, String path) {
        String url = host + path;
        Log.v(TAG, "send request to " + url);
        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent", USER_AGENT);
        return request;
    }

    private static HttpResponse sendRequest(HttpUriRequest request) throws IOException {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_DEFAULT);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_DEFAULT);

        HttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient.execute(request);
    }

    private static String getCharset() {
        return "UTF-8";
    }

    /*===================Request====================*/

    @Override
    public List<PhotoDescription> getPhotoDescriptions(int offset, int limit)
            throws IOException, ApplicationException {
        String path = String.format("/api/photo/popular/?offset=%d&limit=%d&lang=ru", offset, limit);
        HttpGet request = createHttpGet(path);
        HttpResponse response = sendRequest(request);
        HttpEntity entity = response.getEntity();

        String body = entity == null ? "" : EntityUtils.toString(response.getEntity(), getCharset());

        List<PhotoDescription> list = new ArrayList<PhotoDescription>();
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                JSONObject jo = new JSONObject(body);
                jo = jo.getJSONObject("photos");
                JSONArray ja = jo.getJSONArray("list");
                int length = ja.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jPhotoDescription = ja.getJSONObject(i);
                    PhotoDescription photoDescription = new PhotoDescription();
                    String caption = jPhotoDescription.getString("caption");
                    if (caption.equalsIgnoreCase("null")) {
                        caption = "";
                    }
                    photoDescription.setCaption(caption);
                    photoDescription.setLikesCount(jPhotoDescription.getInt("like"));
                    photoDescription.setCreateTime(jPhotoDescription.getLong("timestamp"));
                    photoDescription.setSiteUrl(jPhotoDescription.getString("site_url"));

                    getImageSizes(photoDescription, jPhotoDescription);

                    list.add(photoDescription);
                }
            } catch (JSONException ex) {
                throw new ResponseParseException("Cannot parse response", request, response, ex);
            }
        } else {
            throw new RequestException(request, response);
        }
        return list;
    }

    private void getImageSizes(PhotoDescription photoDescription, JSONObject jo) throws JSONException {
        Set<Integer> sizes = new HashSet<Integer>();
        SparseArray<String> imageUrls = new SparseArray<String>();
        Pattern pattern = Pattern.compile("i\\d+x\\d+");

        Iterator iterator = jo.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (pattern.matcher(key).matches()) {
                String value = key.substring(1, key.indexOf('x'));
                int size = Integer.valueOf(value);
                sizes.add(size);
                imageUrls.put(size, jo.getString(key));
            }
        }
        photoDescription.setImageSizes(sizes);
        photoDescription.setImageUrls(imageUrls);
    }

}
