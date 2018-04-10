package com.common.Elasticsearch.meta;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseModel<BS> {
    protected BS id;

}
