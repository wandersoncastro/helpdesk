package com.wanderson.helpdesk.api.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Response<T> {

    private Optional<T> data;

    private List<String> errors;

    public Optional<T> getData() {
        return data;
    }

    public void setData(Optional<T> data) {
        this.data = data;
    }

    public List<String> getErrors() {
        if(this.errors == null){
            this.errors = new ArrayList<String>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
