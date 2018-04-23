package com.db.service.imp;

import com.common.util.FastJsonUtil;
import com.db.model.restfulModel;
import com.db.service.inf.irestfulService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class restfulService implements irestfulService {

    public restfulModel initiatePay(String msg,String token,String uri){
        restfulModel model =new restfulModel();
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost request2 = new HttpPost(uri);
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("msg", msg));
            nvps.add(new BasicNameValuePair("token", token));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, "GBK");
            request2.setEntity(formEntity);
            HttpResponse response2 = client.execute(request2);
            HttpEntity entity = response2.getEntity();
            ObjectMapper mapper = new ObjectMapper();
            model = mapper.readValue(entity.getContent(), restfulModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    public void responsePaySystem(String msg,String token,String uri){

    }
}
