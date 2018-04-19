package com.db.service.inf;

import com.db.model.restfulModel;

public interface irestfulService {
    restfulModel initiatePay(String msg,String token, String uri);
}
