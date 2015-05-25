package wanba.ott.util;

/**
 * Created by Forcs on 15/5/23.
 */
public class UrlUtils {

    public static String URL_PRODUCT_INFO = "http://121.201.7.173:8080/wanba_shzg/ott_product_info.jsp";

    public static String getAuthUrl(String productCode, String tranId) {
        ProductInfo pInfo = ProductInfo.getInstance();
        String sign = EncryptUtils.encrypt(pInfo.stbCode, tranId, pInfo.serviceCode, pInfo.spCode, pInfo.spKey);
        String url = "http://61.191.44.221:8090/aaa/singleAuth.do?" +
                "hd=1&stbType=skyworth&keyType=ott&eventType=app&stbModel=e8205" +
                "&stbCode=" + pInfo.stbCode +
                "&spCode=" + pInfo.spCode +
                "&serviceCode="+pInfo.serviceCode + "" +
                "&productCode=" + productCode +
                "&tranId=" + tranId +
                "&sign=" + sign +
                "&platform=ut" +
                "&successURL=http://121.201.7.173:8080/wanba_shzg/ott_auth_success.jsp" +
                "&errorURL=http://121.201.7.173:8080/wanba_shzg/ott_auth_error.jsp";
        return url;
    }

    public static String getOrderUrl(String productCode, String tranId) {
        ProductInfo pInfo = ProductInfo.getInstance();
        String sign = EncryptUtils.encrypt(pInfo.stbCode, tranId, pInfo.serviceCode, pInfo.spCode, pInfo.spKey);
        String orderUrl = "http://61.191.44.221:8090/aaa/auth.do?hd=1" +
                "&stbType=skyworth&keyType=ott&eventType=app&stbModel=e8205" +
                "&stbCode=" + pInfo.stbCode +
                "&spCode=" + pInfo.spCode +
                "&serviceCode="+pInfo.serviceCode + "" +
                "&productCode=" + productCode +
                "&tranId=" + tranId +
                "&sign=" + sign +
                "&platform=ut" +
                "&successURL=http://121.201.7.173:8080/wanba_shzg/ott_order_success.jsp" +
                "&errorURL=http://121.201.7.173:8080/wanba_shzg/ott_order_error.jsp";
        return orderUrl;
    }
}
