package wanba.ott.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Forcs on 15/5/23.
 */
public class ProductInfo {

    private static final String KEY_STD_CODE = "stbCode";
    private static final String KEY_SP_KEY = "spKey";
    private static final String KEY_SP_CODE = "spCode";
    private static final String KEY_SERVICE_CODE = "serviceCode";
    private static final String KEY_PRODUCT = "product";
    private static final String KEY_PRODUCT_ID = "productId";
    private static final String KEY_PRODUCT_CODE = "productCode";

    public String stbCode = null;
    public String spKey = null;
    public String spCode = null;
    public String serviceCode = null;

    private Product[] mProduct = null;

    private static ProductInfo mInstance = new ProductInfo();

    private ProductInfo() {
    }

    public static ProductInfo getInstance() {
        return mInstance;
    }

    /**
     * 根据服务端返回的结果初始化产品信息
     * 该接口这在程序初始化的时候调用一次
     * @param response 服务端结果对象
     */
    public void init(JSONObject response) {

        if (response.has(KEY_STD_CODE)) {
            try {
                stbCode = response.getString(KEY_STD_CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response.has(KEY_SP_KEY)) {
            try {
                spKey = response.getString(KEY_SP_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response.has(KEY_SP_CODE)) {
            try {
                spCode = response.getString(KEY_SP_CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response.has(KEY_SERVICE_CODE)) {
            try {
                serviceCode = response.getString(KEY_SERVICE_CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response.has(KEY_PRODUCT)) {
            try {
                JSONArray array = response.getJSONArray(KEY_PRODUCT);
                if (array != null) {
                    final int n = array.length();
                    if (n > 0) {
                        mProduct = new Product[n];
                        for (int i = 0; i < n; i++) {
                            Product p = new Product();
                            JSONObject o = array.getJSONObject(i);
                            if (o.has(KEY_PRODUCT_ID)) {
                                p.id = o.getString(KEY_PRODUCT_ID);
                            }
                            if (o.has(KEY_PRODUCT_CODE)) {
                                p.code = o.getString(KEY_PRODUCT_CODE);
                            }

                            mProduct[i] = p;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getProductCount() {
        return mProduct != null ? mProduct.length : 0;
    }

    public Product getProductAt(int index) {
        return mProduct[index];
    }

    public Product getProductWithId(String productId) {
        if (TextUtils.isEmpty(productId)) {
            return null;
        }

        final int count = getProductCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Product p = getProductAt(i);
                if (productId.equals(p.id)) {
                    return p;
                }
            }
        }

        return null;
    }

    public static class Product {
        public String id = null;
        public String code = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|| ProductInfo \n")
                .append("|| stbCode : ").append(stbCode).append(",\n")
                .append("|| spCode : ").append(spCode).append(",\n")
                .append("|| spKey : ").append(spKey).append(",\n")
                .append("|| serviceCode : ").append(serviceCode).append(",\n")
                .append("|| code : \n");
        final int n = getProductCount();
        for (int i = 0; i < n; i++) {
            Product p = getProductAt(i);
            sb.append("||   ").append(p.id).append(" : ").append(p.code).append("\n");
        }
        return sb.toString();
    }
}
