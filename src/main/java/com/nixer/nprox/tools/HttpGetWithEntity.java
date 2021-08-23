package com.nixer.nprox.tools;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;
/**
 * @author xf
 * @version 1.0.0
 * @ClassName HttpGetWithEntity
 * @Description TODO 定义一个带body的GET请求 继承 HttpEntityEnclosingRequestBase
 * @createTime 2020.11.18 13:51
 */
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    private final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
    public HttpGetWithEntity() {
        super();
    }
    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }
    HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

}
